package com.scriptauto.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.WindowManager
import com.scriptauto.app.ScriptAutoApp
import com.scriptauto.app.accessibility.AutomationAccessibilityService
import com.scriptauto.app.accessibility.CoordinateResolver
import com.scriptauto.app.accessibility.ScriptExecutor
import com.scriptauto.app.data.InUseScriptTracker
import com.scriptauto.app.data.ScriptComponent
import com.scriptauto.app.data.ScriptRecord
import com.scriptauto.app.data.hasCanvasIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed class StartScriptOutcome {
    data object Started : StartScriptOutcome()
    data object AlreadyRunningAnotherScript : StartScriptOutcome() // Q9
    data object OrientationMismatch : StartScriptOutcome() // Q26
}

/**
 * 懸浮視窗:顯示/隱藏元件圖示、執行/中止、結束 三個按鍵(原始規格)。
 * - 全系統同時間只能有一個懸浮視窗(Q9):由 companion object 的 activeScriptId 把關。
 * - 從啟動(Started)到「結束」為止,對應的腳本在 InUseScriptTracker 中標記為使用中(Q22),
 *   無論當下是「執行中」還是「中止」都算使用中。
 * - 預設不顯示元件圖示、預設中止狀態(原始規格)。
 */
class FloatingControlService : Service() {

    companion object {
        @Volatile
        private var activeScriptId: Long? = null

        fun isAnyScriptActive(): Boolean = activeScriptId != null
    }

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var executor: ScriptExecutor? = null
    private var iconsVisible = false // 預設不顯示元件圖示
    private var isRunning = false // 預設中止狀態

    private var currentScript: ScriptRecord? = null
    private var currentResolver: CoordinateResolver? = null
    private lateinit var overlay: FloatingOverlayController

    /** 由呼叫端注入,用來查詢「其他腳本」引用的腳本內容(避免這個 Service 直接依賴 Repository) */
    var repositoryLookup: (suspend (Long) -> ScriptRecord?)? = null

    override fun onCreate() {
        super.onCreate()
        overlay = FloatingOverlayController(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val scriptId = intent?.getLongExtra("scriptId", -1L) ?: -1L
        if (scriptId < 0) return START_NOT_STICKY

        val repository = (application as ScriptAutoApp).repository
        repositoryLookup = { id -> repository.getScript(id) }

        scope.launch {
            val script = repository.getScript(scriptId) ?: return@launch
            when (tryStart(script, buildResolver())) {
                StartScriptOutcome.Started -> {
                    overlay.showControlPanel(
                        onToggleIcons = { onToggleIconsPressed() },
                        onToggleRun = { onToggleRunPressed() },
                        onExit = { onExitPressed() },
                    )
                }
                StartScriptOutcome.AlreadyRunningAnotherScript, StartScriptOutcome.OrientationMismatch -> {
                    stopSelf(startId)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun buildResolver(): CoordinateResolver {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealMetrics(metrics)
        return CoordinateResolver(metrics.widthPixels, metrics.heightPixels, metrics.xdpi, metrics.ydpi)
    }

    /**
     * 嘗試啟動腳本。呼叫端(UI)應先呼叫這個函式並依回傳結果決定是否真的顯示懸浮視窗。
     */
    fun tryStart(script: ScriptRecord, resolver: CoordinateResolver): StartScriptOutcome {
        if (isAnyScriptActive()) {
            return StartScriptOutcome.AlreadyRunningAnotherScript
        }
        if (!resolver.matchesOrientation(script.orientation)) {
            return StartScriptOutcome.OrientationMismatch
        }
        activeScriptId = script.id
        InUseScriptTracker.markInUse(script.id)
        iconsVisible = false
        isRunning = false
        currentScript = script
        currentResolver = resolver

        val service = AutomationAccessibilityService.instance
            ?: return StartScriptOutcome.OrientationMismatch // 服務未連線時視同無法啟動,交由 UI 顯示錯誤
        executor = ScriptExecutor(
            service = service,
            resolver = resolver,
            resolveSubScript = { id -> repositoryLookup?.invoke(id) },
        )
        return StartScriptOutcome.Started
    }

    /** 執行/中止 按鍵:是執行中就中止,是中止狀態就從頭開始執行 */
    fun onToggleRunPressed() {
        val script = currentScript ?: return
        if (isRunning) {
            executor?.abort()
            isRunning = false
            overlay.setRunButtonLabel(false)
        } else {
            isRunning = true
            overlay.setRunButtonLabel(true)
            scope.launch {
                executor?.run(script)
                isRunning = false
                overlay.setRunButtonLabel(false)
            }
        }
    }

    /** 顯示/隱藏元件圖示 按鍵(這些圖示本身沒有互動功能,見原始規格) */
    fun onToggleIconsPressed() {
        iconsVisible = !iconsVisible
        overlay.setIconsButtonLabel(iconsVisible)
        if (iconsVisible) {
            val script = currentScript ?: return
            val resolver = currentResolver ?: return
            overlay.showComponentIcons(collectIconPoints(script, resolver))
        } else {
            overlay.hideComponentIcons()
        }
    }

    private fun collectIconPoints(script: ScriptRecord, resolver: CoordinateResolver): List<Triple<Float, Float, String>> {
        val points = mutableListOf<Triple<Float, Float, String>>()
        script.components.forEach { c ->
            if (!c.hasCanvasIcon()) return@forEach
            when (c) {
                is ScriptComponent.LoopTap -> resolver.toPixel(c.position).let { (x, y) -> points += Triple(x, y, c.color.hex) }
                is ScriptComponent.Hold -> resolver.toPixel(c.position).let { (x, y) -> points += Triple(x, y, c.color.hex) }
                is ScriptComponent.Drag -> {
                    resolver.toPixel(c.start.position).let { (x, y) -> points += Triple(x, y, c.color.hex) }
                    resolver.toPixel(c.end.position).let { (x, y) -> points += Triple(x, y, c.color.hex) }
                }
                else -> Unit
            }
        }
        return points
    }

    /** 結束 按鍵:關閉懸浮視窗,腳本解除「使用中」狀態(Q22) */
    fun onExitPressed() {
        executor?.abort()
        overlay.removeAll()
        activeScriptId?.let { InUseScriptTracker.clear() }
        activeScriptId = null
        currentScript = null
        currentResolver = null
        stopSelf()
    }

    override fun onDestroy() {
        executor?.abort()
        overlay.removeAll()
        activeScriptId?.let { InUseScriptTracker.clear() }
        activeScriptId = null
        scope.coroutineContext[Job]?.cancel()
        super.onDestroy()
    }
}
