package com.scriptauto.app.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.scriptauto.app.data.RelativePosition
import com.scriptauto.app.data.ScriptComponent
import com.scriptauto.app.data.ScriptRecord
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.random.Random

/**
 * 無障礙服務本體。實際手勢模擬透過 dispatchGesture 完成,見 ADR-0001。
 * 使用者需在啟動流程第一步授予此服務(見 Q25:與懸浮視窗權限一起請求)。
 */
class AutomationAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit

    companion object {
        /** 供其他元件(如 FloatingControlService)取得目前存活的服務實例,以送出手勢指令 */
        @Volatile
        var instance: AutomationAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    /** 真正等到系統回報手勢執行完成(或被中斷)才 resume,而不是派送出去就當作完成 */
    private suspend fun dispatchAndAwait(gesture: GestureDescription): Boolean =
        suspendCancellableCoroutine { continuation ->
            val callback = object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    Log.d("ScriptAuto", "手勢執行完成 onCompleted")
                    if (continuation.isActive) continuation.resume(true) {}
                }
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    Log.w("ScriptAuto", "手勢被系統取消 onCancelled(可能是同時有其他手勢正在進行,或目標位置無法接受手勢)")
                    if (continuation.isActive) continuation.resume(false) {}
                }
            }
            val accepted = dispatchGesture(gesture, callback, null)
            Log.d("ScriptAuto", "dispatchGesture 呼叫結果 accepted=$accepted")
            if (!accepted && continuation.isActive) {
                Log.w("ScriptAuto", "手勢沒有被系統接受(accepted=false),可能是無障礙服務還沒真正連線,或裝置目前狀態不允許手勢注入")
                continuation.resume(false) {}
            }
        }

    suspend fun dispatchTap(xPx: Float, yPx: Float, durationMs: Long = 50): Boolean {
        val path = Path().apply { moveTo(xPx, yPx) }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        return dispatchAndAwait(GestureDescription.Builder().addStroke(stroke).build())
    }

    suspend fun dispatchHold(xPx: Float, yPx: Float, durationMs: Long): Boolean {
        val path = Path().apply { moveTo(xPx, yPx) }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        return dispatchAndAwait(GestureDescription.Builder().addStroke(stroke).build())
    }

    suspend fun dispatchDrag(startX: Float, startY: Float, endX: Float, endY: Float, durationMs: Long): Boolean {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        return dispatchAndAwait(GestureDescription.Builder().addStroke(stroke).build())
    }
}

/**
 * 把「相對座標 + 誤差範圍」換算成裝置目前螢幕的實際像素座標,並套用隨機誤差(mm 換算為 px 需搭配裝置 DPI)。
 */
class CoordinateResolver(
    private val screenWidthPx: Int,
    private val screenHeightPx: Int,
    private val xdpi: Float,
    private val ydpi: Float,
) {
    private fun mmToPx(mm: Int, dpi: Float): Float = (mm / 25.4f) * dpi

    /** 不套用誤差範圍的純位置換算,供「顯示元件圖示」疊加畫面使用(那只是視覺參考,不代表實際點擊會落在的隨機位置) */
    fun toPixel(position: RelativePosition): Pair<Float, Float> =
        (position.xPercent * screenWidthPx) to (position.yPercent * screenHeightPx)

    /** 在誤差範圍(圓形半徑)內取均勻分布的隨機偏移,見 CONTEXT.md「誤差範圍」 */
    fun resolve(position: RelativePosition, jitterRadiusMm: Int): Pair<Float, Float> {
        val baseX = position.xPercent * screenWidthPx
        val baseY = position.yPercent * screenHeightPx
        if (jitterRadiusMm <= 0) return baseX to baseY

        val radiusPx = mmToPx(jitterRadiusMm, (xdpi + ydpi) / 2f)
        val angle = Random.nextDouble(0.0, 2 * Math.PI)
        val r = radiusPx * Math.sqrt(Random.nextDouble())
        val dx = (r * Math.cos(angle)).toFloat()
        val dy = (r * Math.sin(angle)).toFloat()
        return (baseX + dx) to (baseY + dy)
    }
}

/**
 * 依序執行一份腳本的元件序列。支援中止(見懸浮視窗「執行/中止」)與「其他腳本」的遞迴呼叫。
 * 注意:是否形成循環引用已在儲存階段用 ScriptValidation.detectCircularReference 擋下(ADR-0003),
 * 這裡執行期不需要再做循環偵測。
 */
class ScriptExecutor(
    private val service: AutomationAccessibilityService,
    private val resolver: CoordinateResolver,
    private val resolveSubScript: suspend (Long) -> ScriptRecord?,
) {
    @Volatile
    var isAborted: Boolean = false
        private set

    fun abort() {
        isAborted = true
    }

    suspend fun run(script: ScriptRecord) {
        isAborted = false
        executeSequence(script.components.sortedBy { it.sequenceIndex })
    }

    private suspend fun executeSequence(components: List<ScriptComponent>) {
        for (component in components) {
            if (isAborted) return
            when (component) {
                is ScriptComponent.LoopTap -> runLoopTap(component)
                is ScriptComponent.Drag -> runDrag(component)
                is ScriptComponent.Wait -> kotlinx.coroutines.delay(component.durationMs.toLong())
                is ScriptComponent.Hold -> runHold(component)
                is ScriptComponent.SubScript -> runSubScript(component)
            }
        }
    }

    private suspend fun runSubScript(c: ScriptComponent.SubScript) {
        val sub = resolveSubScript(c.referencedScriptId) ?: return
        val limit = when (val lc = c.loopCount) {
            is com.scriptauto.app.data.LoopCount.Infinite -> Int.MAX_VALUE
            is com.scriptauto.app.data.LoopCount.Fixed -> lc.count
        }
        var count = 0
        while (!isAborted && count < limit) {
            executeSequence(sub.components.sortedBy { it.sequenceIndex })
            count++
        }
    }

    private suspend fun runLoopTap(c: ScriptComponent.LoopTap) {
        var count = 0
        val limit = when (val lc = c.loopCount) {
            is com.scriptauto.app.data.LoopCount.Infinite -> Int.MAX_VALUE
            is com.scriptauto.app.data.LoopCount.Fixed -> lc.count
        }
        android.util.Log.d("ScriptAuto", "開始執行循環點擊,原始相對位置=(${c.position.xPercent}, ${c.position.yPercent})")
        while (!isAborted && count < limit) {
            val (x, y) = resolver.resolve(c.position, c.jitterRadiusMm)
            android.util.Log.d("ScriptAuto", "第${count + 1}次點擊,換算後裝置座標=($x, $y)")
            service.dispatchTap(x, y)
            kotlinx.coroutines.delay(c.intervalMs.toLong())
            count++
        }
        android.util.Log.d("ScriptAuto", "循環點擊結束,共執行 $count 次(isAborted=$isAborted)")
    }

    private suspend fun runHold(c: ScriptComponent.Hold) {
        val (x, y) = resolver.resolve(c.position, 0)
        service.dispatchHold(x, y, c.durationMs.toLong())
    }

    private suspend fun runDrag(c: ScriptComponent.Drag) {
        val (sx, sy) = resolver.resolve(c.start.position, c.start.jitterRadiusMm)
        val (ex, ey) = resolver.resolve(c.end.position, c.end.jitterRadiusMm)
        service.dispatchDrag(sx, sy, ex, ey, c.durationMs.toLong())
    }
}
