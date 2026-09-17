package com.scriptauto.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scriptauto.app.data.ComponentColor

private data class PickerEntry(val kind: ComponentKind, val label: String, val color: Color)

private val pickerEntries = listOf(
    PickerEntry(ComponentKind.LOOP_TAP, "循環點擊", Color(android.graphics.Color.parseColor(ComponentColor.YELLOW.hex))),
    PickerEntry(ComponentKind.DRAG, "拖曳", Color(android.graphics.Color.parseColor(ComponentColor.BLUE.hex))),
    PickerEntry(ComponentKind.WAIT, "等待", Color(android.graphics.Color.parseColor(ComponentColor.RED.hex))),
    PickerEntry(ComponentKind.HOLD, "按住", Color(android.graphics.Color.parseColor(ComponentColor.ORANGE.hex))),
    PickerEntry(ComponentKind.SUB_SCRIPT, "其他腳本", Color(android.graphics.Color.parseColor(ComponentColor.GREEN.hex))),
)

@Composable
fun LeftPanel(
    onAddComponent: (ComponentKind) -> Unit,
    onCollapse: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight().width(160.dp).background(Color(0xFF1C1C1C)),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCollapse() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("◀ 收起", color = Color.White)
            }
            Text("基本元件", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))
            pickerEntries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddComponent(entry.kind) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(28.dp).background(entry.color, CircleShape))
                    Spacer(Modifier.width(10.dp))
                    Text(entry.label, color = Color.White)
                }
            }
        }
        Text(
            "退回鍵",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
                .padding(16.dp),
        )
    }
}
