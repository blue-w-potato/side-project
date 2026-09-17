package com.scriptauto.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scriptauto.app.data.LoopCount
import com.scriptauto.app.data.ScriptComponent
import com.scriptauto.app.data.ScriptRecord

/**
 * 屬性彈出視窗只編輯「需要使用者輸入資料」的屬性;位置一律透過畫布拖曳決定,這裡不出現。
 * 存在時外層(EditorScreen)已阻擋選取其他元件。
 */
@Composable
fun PropertyPopup(
    item: EditorItem,
    subScriptChoices: List<ScriptRecord>,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onConfirm: (ScriptComponent) -> String?, // 回傳非 null = 驗證失敗訊息
) {
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onCancel) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (val c = item.component) {
                    is ScriptComponent.LoopTap -> LoopTapFields(c) { updated, err ->
                        errorText = err
                        if (err == null) errorText = onConfirm(updated)
                    }
                    is ScriptComponent.Drag -> DragFields(c) { updated, err ->
                        errorText = err
                        if (err == null) errorText = onConfirm(updated)
                    }
                    is ScriptComponent.Wait -> WaitFields(c) { updated, err ->
                        errorText = err
                        if (err == null) errorText = onConfirm(updated)
                    }
                    is ScriptComponent.Hold -> HoldFields(c) { updated, err ->
                        errorText = err
                        if (err == null) errorText = onConfirm(updated)
                    }
                    is ScriptComponent.SubScript -> SubScriptFields(c, subScriptChoices) { updated, err ->
                        errorText = err
                        if (err == null) errorText = onConfirm(updated)
                    }
                }

                errorText?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(onClick = onDelete) { Text("刪除") } // 不需二次確認(Q14)
                    Row {
                        TextButton(onClick = onCancel) { Text("取消") }
                    }
                }
            }
        }
    }
}

@Composable
private fun IntField(label: String, initial: Int, onValueChange: (Int?) -> Unit) {
    var text by remember { mutableStateOf(initial.toString()) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onValueChange(it.toIntOrNull())
        },
        label = { Text(label) },
    )
}

@Composable
private fun LoopTapFields(c: ScriptComponent.LoopTap, submit: (ScriptComponent, String?) -> Unit) {
    var jitter by remember { mutableStateOf(c.jitterRadiusMm) }
    var interval by remember { mutableStateOf(c.intervalMs) }
    var infinite by remember { mutableStateOf(c.loopCount is LoopCount.Infinite) }
    var count by remember { mutableStateOf((c.loopCount as? LoopCount.Fixed)?.count ?: 1) }

    Text("循環點擊")
    IntField("誤差範圍(mm, ≥0)", jitter) { it?.let { v -> jitter = v } }
    IntField("間隔時間(ms, >0)", interval) { it?.let { v -> interval = v } }
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = infinite, onClick = { infinite = true })
        Text("無限")
        Spacer(Modifier.width(12.dp))
        RadioButton(selected = !infinite, onClick = { infinite = false })
        Text("使用者輸入")
    }
    if (!infinite) {
        IntField("循環次數(1..1,000,000)", count) { it?.let { v -> count = v } }
    }
    Button(onClick = {
        val result = try {
            c.copy(
                jitterRadiusMm = jitter,
                intervalMs = interval,
                loopCount = if (infinite) LoopCount.Infinite else LoopCount.Fixed(count),
            ) to null
        } catch (e: IllegalArgumentException) {
            null to (e.message ?: "數值不合規定")
        }
        if (result.first != null) submit(result.first!!, null) else submit(c, result.second)
    }) { Text("確定") }
}

@Composable
private fun DragFields(c: ScriptComponent.Drag, submit: (ScriptComponent, String?) -> Unit) {
    var duration by remember { mutableStateOf(c.durationMs) }
    var startJitter by remember { mutableStateOf(c.start.jitterRadiusMm) }
    var endJitter by remember { mutableStateOf(c.end.jitterRadiusMm) }

    Text("拖曳")
    IntField("拖曳花費的時間(ms, >0)", duration) { it?.let { v -> duration = v } }
    IntField("起始位置誤差距離(mm, ≥0)", startJitter) { it?.let { v -> startJitter = v } }
    IntField("終點位置誤差距離(mm, ≥0)", endJitter) { it?.let { v -> endJitter = v } }
    Button(onClick = {
        val result = try {
            c.copy(
                durationMs = duration,
                start = c.start.copy(jitterRadiusMm = startJitter),
                end = c.end.copy(jitterRadiusMm = endJitter),
            ) to null
        } catch (e: IllegalArgumentException) {
            null to (e.message ?: "數值不合規定")
        }
        if (result.first != null) submit(result.first!!, null) else submit(c, result.second)
    }) { Text("確定") }
}

@Composable
private fun WaitFields(c: ScriptComponent.Wait, submit: (ScriptComponent, String?) -> Unit) {
    var duration by remember { mutableStateOf(c.durationMs) }
    Text("等待")
    IntField("時長(ms, ≥0)", duration) { it?.let { v -> duration = v } }
    Button(onClick = {
        val result = try {
            c.copy(durationMs = duration) to null
        } catch (e: IllegalArgumentException) {
            null to (e.message ?: "數值不合規定")
        }
        if (result.first != null) submit(result.first!!, null) else submit(c, result.second)
    }) { Text("確定") }
}

@Composable
private fun HoldFields(c: ScriptComponent.Hold, submit: (ScriptComponent, String?) -> Unit) {
    var duration by remember { mutableStateOf(c.durationMs) }
    Text("按住")
    IntField("時長(ms, ≥0)", duration) { it?.let { v -> duration = v } }
    Button(onClick = {
        val result = try {
            c.copy(durationMs = duration) to null
        } catch (e: IllegalArgumentException) {
            null to (e.message ?: "數值不合規定")
        }
        if (result.first != null) submit(result.first!!, null) else submit(c, result.second)
    }) { Text("確定") }
}

@Composable
private fun SubScriptFields(
    c: ScriptComponent.SubScript,
    choices: List<ScriptRecord>,
    submit: (ScriptComponent, String?) -> Unit,
) {
    var selectedId by remember { mutableStateOf(c.referencedScriptId) }
    Text("其他腳本")
    if (choices.isEmpty()) {
        Text("目前沒有其他已儲存的腳本可以引用")
    } else {
        choices.forEach { script ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selectedId == script.id, onClick = { selectedId = script.id })
                Text(script.name)
            }
        }
    }
    Button(
        enabled = choices.isNotEmpty(),
        onClick = { submit(c.copy(referencedScriptId = selectedId), null) },
    ) { Text("確定") }
}
