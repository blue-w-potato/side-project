package com.scriptauto.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.scriptauto.app.data.ComponentColor
import com.scriptauto.app.data.ScriptComponent
import kotlin.math.abs
import kotlin.math.roundToInt

private const val TAP_SLOP_PX = 12f

private fun ComponentColor.toComposeColor(): Color = Color(android.graphics.Color.parseColor(hex))

private fun componentLabel(c: ScriptComponent): String = when (c) {
    is ScriptComponent.LoopTap -> "循環點擊"
    is ScriptComponent.Drag -> "拖曳"
    is ScriptComponent.Wait -> "等待"
    is ScriptComponent.Hold -> "按住"
    is ScriptComponent.SubScript -> "其他腳本"
}

@Composable
fun SequencePanel(
    items: List<EditorItem>,
    onDragBy: (uiId: Int, deltaSteps: Int) -> Unit,
    onItemTap: (Int) -> Unit,
    onSave: () -> Unit,
    onCollapse: () -> Unit,
) {
    var itemHeightPx by remember { mutableStateOf(1) }
    var draggingUiId by remember { mutableStateOf<Int?>(null) }
    var dragOffsetPx by remember { mutableStateOf(0f) }

    Column(modifier = Modifier.fillMaxHeight().width(220.dp).background(Color(0xFF1C1C1C))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCollapse() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("收起 ▶", color = Color.White)
        }
        Text("序列", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items, key = { it.uiId }) { item ->
                val index = items.indexOf(item)
                val isDragging = draggingUiId == item.uiId
                SequenceBlock(
                    item = item,
                    index = index,
                    offsetPxWhileDragging = if (isDragging) dragOffsetPx else 0f,
                    isDragging = isDragging,
                    onMeasuredHeight = { h -> if (itemHeightPx <= 1) itemHeightPx = h },
                    onDragStart = {
                        draggingUiId = item.uiId
                        dragOffsetPx = 0f
                    },
                    onDragDelta = { dy ->
                        dragOffsetPx += dy
                        val steps = (dragOffsetPx / itemHeightPx).roundToInt()
                        if (steps != 0) {
                            onDragBy(item.uiId, steps)
                            dragOffsetPx -= steps * itemHeightPx
                        }
                    },
                    onDragEnd = {
                        draggingUiId = null
                        dragOffsetPx = 0f
                    },
                    onTap = { onItemTap(item.uiId) },
                )
            }
        }
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        ) { Text("儲存腳本") }
    }
}

@Composable
private fun SequenceBlock(
    item: EditorItem,
    index: Int,
    offsetPxWhileDragging: Float,
    isDragging: Boolean,
    onMeasuredHeight: (Int) -> Unit,
    onDragStart: () -> Unit,
    onDragDelta: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onTap: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .offset { IntOffset(0, offsetPxWhileDragging.roundToInt()) }
            .zIndex(if (isDragging) 1f else 0f)
            .onSizeChanged { onMeasuredHeight(it.height.coerceAtLeast(1)) }
            .background(item.component.color.toComposeColor(), RoundedCornerShape(6.dp))
            .padding(10.dp)
            .pointerInput(item.uiId) {
                var totalDrag = 0f
                detectDragGestures(
                    onDragStart = {
                        totalDrag = 0f
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += abs(dragAmount.y) + abs(dragAmount.x)
                        onDragDelta(dragAmount.y)
                    },
                    onDragEnd = {
                        if (totalDrag < TAP_SLOP_PX) onTap()
                        onDragEnd()
                    },
                    onDragCancel = { onDragEnd() },
                )
            },
    ) {
        Text("第${index + 1}步 · ${componentLabel(item.component)}", color = Color.White, modifier = Modifier.weight(1f))
    }
}
