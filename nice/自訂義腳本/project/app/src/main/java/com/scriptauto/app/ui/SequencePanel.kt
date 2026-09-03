package com.scriptauto.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.scriptauto.app.data.ComponentColor
import com.scriptauto.app.data.ScriptComponent
import kotlin.math.roundToInt

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
    onReorder: (from: Int, to: Int) -> Unit,
    onItemTap: (Int) -> Unit,
    onSave: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxHeight().width(220.dp).background(Color(0xFF1C1C1C))) {
        Text("序列", color = Color.White, modifier = Modifier.padding(12.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items, key = { it.uiId }) { item ->
                val index = items.indexOf(item)
                SequenceBlock(
                    item = item,
                    index = index,
                    totalCount = items.size,
                    onDragBy = { deltaSteps ->
                        val target = (index + deltaSteps).coerceIn(0, items.lastIndex)
                        if (target != index) onReorder(index, target)
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
    totalCount: Int,
    onDragBy: (Int) -> Unit,
    onTap: () -> Unit,
) {
    var heightPx by remember { mutableStateOf(1) }
    var accumulated by remember { mutableStateOf(0f) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .onSizeChanged { heightPx = it.height.coerceAtLeast(1) }
            .background(item.component.color.toComposeColor(), RoundedCornerShape(6.dp))
            .padding(10.dp)
            .clickable(onClick = onTap)
            .pointerInput(item.uiId) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { accumulated = 0f },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        accumulated += dragAmount.y
                        val steps = (accumulated / heightPx).roundToInt()
                        if (steps != 0) {
                            onDragBy(steps)
                            accumulated -= steps * heightPx
                        }
                    },
                )
            },
    ) {
        Text("第${index + 1}步 · ${componentLabel(item.component)}", color = Color.White, modifier = Modifier.weight(1f))
    }
}
