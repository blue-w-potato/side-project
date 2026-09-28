package com.scriptauto.app.service

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 懸浮視窗的實際畫面:一個可以拖曳移動位置的控制列(三個按鍵 + 一個拖曳把手),
 * 以及「顯示元件圖示」時疊加在最上層的一堆小圓點(沒有互動功能,見原始規格)。
 */
class FloatingOverlayController(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var controlView: LinearLayout? = null
    private var controlParams: WindowManager.LayoutParams? = null
    private lateinit var iconsButton: Button
    private lateinit var runButton: Button
    private val iconDots = mutableListOf<View>()

    fun showControlPanel(
        onToggleIcons: () -> Unit,
        onToggleRun: () -> Unit,
        onExit: () -> Unit,
    ) {
        if (controlView != null) return

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#CC222222"))
            setPadding(12, 16, 20, 16)
        }

        val dragHandle = TextView(context).apply {
            text = "⠿"
            setTextColor(Color.WHITE)
            textSize = 20f
            setPadding(16, 0, 24, 0)
        }
        iconsButton = Button(context).apply {
            text = "顯示元件圖示" // 預設不顯示元件圖示(原始規格)
            setOnClickListener { onToggleIcons() }
        }
        runButton = Button(context).apply {
            text = "執行" // 預設中止狀態(原始規格)
            setOnClickListener { onToggleRun() }
        }
        val exitButton = Button(context).apply {
            text = "結束"
            setOnClickListener { onExit() }
        }
        layout.addView(dragHandle)
        layout.addView(iconsButton)
        layout.addView(runButton)
        layout.addView(exitButton)

        // 用 TOP|START(以左上角為原點)而不是 TOP|END,拖曳位移的加減法才直覺、不會反向
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 16
            y = 120
        }

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        dragHandle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    runCatching { windowManager.updateViewLayout(layout, params) }
                    true
                }
                else -> false
            }
        }

        windowManager.addView(layout, params)
        controlView = layout
        controlParams = params
    }

    fun setIconsButtonLabel(visible: Boolean) {
        iconsButton.text = if (visible) "隱藏元件圖示" else "顯示元件圖示"
    }

    fun setRunButtonLabel(running: Boolean) {
        runButton.text = if (running) "中止" else "執行"
    }

    /** points: 每個要顯示的圖示中心座標(裝置像素)與顏色 */
    fun showComponentIcons(points: List<Triple<Float, Float, String>>) {
        hideComponentIcons()
        val sizePx = 32
        points.forEach { (x, y, colorHex) ->
            val dot = View(context).apply {
                setBackgroundColor(Color.parseColor(colorHex))
            }
            val params = WindowManager.LayoutParams(
                sizePx, sizePx,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                // 沒有互動功能,不能攔截觸控(原始規格),避免影響腳本自己的點擊手勢
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                this.x = x.toInt() - sizePx / 2
                this.y = y.toInt() - sizePx / 2
            }
            runCatching { windowManager.addView(dot, params) }
            iconDots.add(dot)
        }
    }

    fun hideComponentIcons() {
        iconDots.forEach { runCatching { windowManager.removeView(it) } }
        iconDots.clear()
    }

    fun removeAll() {
        hideComponentIcons()
        controlView?.let { runCatching { windowManager.removeView(it) } }
        controlView = null
        controlParams = null
    }
}
