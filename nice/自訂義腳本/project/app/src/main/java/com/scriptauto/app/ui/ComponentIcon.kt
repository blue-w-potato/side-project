package com.scriptauto.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private const val TAP_SLOP_PX = 12f

/**
 * 編排畫面上的元件圖示。offsetPx 為圖示中心在畫布上的像素座標(呼叫端負責換算相對座標)。
 * 純拖曳視為移動位置;幾乎沒有位移(小於 TAP_SLOP_PX)視為輕點,觸發 onTap 開啟屬性視窗。
 */
@Composable
fun DraggableComponentIcon(
    offsetPx: Offset,
    color: Color,
    label: String,
    onMove: (Offset) -> Unit,
    onTap: () -> Unit,
) {
    var totalDrag = Offset.Zero
    var iconSize by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .size(40.dp)
            .onSizeChanged { iconSize = it }
            .background(color, CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { totalDrag = Offset.Zero },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                        onMove(dragAmount)
                    },
                    onDragEnd = {
                        if (abs(totalDrag.x) < TAP_SLOP_PX && abs(totalDrag.y) < TAP_SLOP_PX) {
                            onTap()
                        }
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White)
    }
}
