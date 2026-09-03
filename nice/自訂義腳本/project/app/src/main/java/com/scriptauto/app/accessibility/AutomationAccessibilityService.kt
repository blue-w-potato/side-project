package com.scriptauto.app.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.scriptauto.app.data.RelativePosition
import com.scriptauto.app.data.ScriptComponent
import com.scriptauto.app.data.ScriptOrientation
import com.scriptauto.app.data.ScriptRecord
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

    fun dispatchTap(xPx: Float, yPx: Float, durationMs: Long = 50, onDone: () -> Unit) {
        val path = Path().apply { moveTo(xPx, yPx) }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        dispatchGesture(GestureDescription.Builder().addStroke(stroke).build(), null, null)
        onDone()
    }

    fun dispatchHold(xPx: Float, yPx: Float, durationMs: Long, onDone: () -> Unit) {
        val path = Path().apply { moveTo(xPx, yPx) }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        dispatchGesture(GestureDescription.Builder().addStroke(stroke).build(), null, null)
        onDone()
    }

    fun dispatchDrag(startX: Float, startY: Float, endX: Float, endY: Float, durationMs: Long, onDone: () -> Unit) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        dispatchGesture(GestureDescription.Builder().addStroke(stroke).build(), null, null)
        onDone()
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

    /** 啟動前的方向一致性檢查(ADR-0002 / Q26) */
    fun matchesOrientation(recorded: ScriptOrientation): Boolean {
        val isCurrentlyLandscape = screenWidthPx > screenHeightPx
        return when (recorded) {
            ScriptOrientation.LANDSCAPE -> isCurrentlyLandscape
            ScriptOrientation.PORTRAIT -> !isCurrentlyLandscape
        }
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
                is ScriptComponent.SubScript -> {
                    val sub = resolveSubScript(component.referencedScriptId) ?: continue
                    executeSequence(sub.components.sortedBy { it.sequenceIndex })
                }
            }
        }
    }

    private suspend fun runLoopTap(c: ScriptComponent.LoopTap) {
        var count = 0
        val limit = when (val lc = c.loopCount) {
            is com.scriptauto.app.data.LoopCount.Infinite -> Int.MAX_VALUE
            is com.scriptauto.app.data.LoopCount.Fixed -> lc.count
        }
        while (!isAborted && count < limit) {
            val (x, y) = resolver.resolve(c.position, c.jitterRadiusMm)
            var done = false
            service.dispatchTap(x, y) { done = true }
            kotlinx.coroutines.delay(c.intervalMs.toLong())
            count++
        }
    }

    private suspend fun runHold(c: ScriptComponent.Hold) {
        val (x, y) = resolver.resolve(c.position, 0)
        service.dispatchHold(x, y, c.durationMs.toLong()) {}
    }

    private suspend fun runDrag(c: ScriptComponent.Drag) {
        val (sx, sy) = resolver.resolve(c.start.position, c.start.jitterRadiusMm)
        val (ex, ey) = resolver.resolve(c.end.position, c.end.jitterRadiusMm)
        service.dispatchDrag(sx, sy, ex, ey, c.durationMs.toLong()) {}
    }
}
