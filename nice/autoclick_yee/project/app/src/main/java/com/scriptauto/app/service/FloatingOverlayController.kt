package com.scriptauto.app.service

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout

/**
 * 懸浮視窗的實際畫面:一個固定在畫面右上角的控制列(三個按鍵),
 * 以及「顯示元件圖示」時疊加在最上層的一堆小圓點(沒有互動功能,見原始規格)。
 */
class FloatingOverlayController(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var controlView: LinearLayout? = null
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
            setPadding(20, 16, 20, 16)
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
        layout.addView(iconsButton)
        layout.addView(runButton)
        layout.addView(exitButton)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16
            y = 120
        }
        windowManager.addView(layout, params)
        controlView = layout
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
    }
}
