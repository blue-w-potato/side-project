package com.scriptauto.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scriptauto.app.data.RelativePosition
import com.scriptauto.app.data.ScriptComponent
import com.scriptauto.app.data.ScriptOrientation
import com.scriptauto.app.data.ScriptRepository
import kotlin.math.roundToInt

private fun ScriptComponent.colorAsCompose(): Color =
    Color(android.graphics.Color.parseColor(color.hex))

private fun clamp01(v: Float): Float = v.coerceIn(0f, 1f)

@Composable
fun EditorScreen(
    orientation: ScriptOrientation,
    scriptId: Long?,
    repository: ScriptRepository,
    onLeave: () -> Unit,
    onSaved: () -> Unit,
) {
    val vm: EditorViewModel = viewModel(factory = EditorViewModelFactory(repository, orientation, scriptId))

    BackHandler { vm.requestLeave(onLeave) } // 系統返回鍵與左側欄退回鍵行為一致(Q24)

    if (!vm.isLoaded.value) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val canvasWidthPx = constraints.maxWidth.toFloat()
        val canvasHeightPx = constraints.maxHeight.toFloat()

        // 畫布上的元件圖示(只有 hasCanvasIcon() 的元件會出現,見 CONTEXT.md)
        vm.items.forEach { item ->
            if (!item.component.hasCanvasIcon()) return@forEach
            when (val c = item.component) {
                is ScriptComponent.LoopTap -> CanvasIcon(
                    position = c.position, canvasW = canvasWidthPx, canvasH = canvasHeightPx,
                    color = item.component.colorAsCompose(), label = "${vm.items.indexOf(item) + 1}",
                    onMove = { delta ->
                        val newPos = RelativePosition(
                            clamp01(c.position.xPercent + delta.x / canvasWidthPx),
                            clamp01(c.position.yPercent + delta.y / canvasHeightPx),
                        )
                        vm.updatePosition(item.uiId, null, newPos)
                    },
                    onTap = { vm.openPopup(item.uiId) },
                )
                is ScriptComponent.Hold -> CanvasIcon(
                    position = c.position, canvasW = canvasWidthPx, canvasH = canvasHeightPx,
                    color = item.component.colorAsCompose(), label = "${vm.items.indexOf(item) + 1}",
                    onMove = { delta ->
                        val newPos = RelativePosition(
                            clamp01(c.position.xPercent + delta.x / canvasWidthPx),
                            clamp01(c.position.yPercent + delta.y / canvasHeightPx),
                        )
                        vm.updatePosition(item.uiId, null, newPos)
                    },
                    onTap = { vm.openPopup(item.uiId) },
                )
                is ScriptComponent.Drag -> {
                    CanvasIcon(
                        position = c.start.position, canvasW = canvasWidthPx, canvasH = canvasHeightPx,
                        color = item.component.colorAsCompose(), label = "${vm.items.indexOf(item) + 1}起",
                        onMove = { delta ->
                            val newPos = RelativePosition(
                                clamp01(c.start.position.xPercent + delta.x / canvasWidthPx),
                                clamp01(c.start.position.yPercent + delta.y / canvasHeightPx),
                            )
                            vm.updatePosition(item.uiId, true, newPos)
                        },
                        onTap = { vm.openPopup(item.uiId) },
                    )
                    CanvasIcon(
                        position = c.end.position, canvasW = canvasWidthPx, canvasH = canvasHeightPx,
                        color = item.component.colorAsCompose(), label = "${vm.items.indexOf(item) + 1}終",
                        onMove = { delta ->
                            val newPos = RelativePosition(
                                clamp01(c.end.position.xPercent + delta.x / canvasWidthPx),
                                clamp01(c.end.position.yPercent + delta.y / canvasHeightPx),
                            )
                            vm.updatePosition(item.uiId, false, newPos)
                        },
                        onTap = { vm.openPopup(item.uiId) },
                    )
                }
                else -> Unit
            }
        }

        // 左側三角形(展開左側欄);右側已展開時停用(不能點擊)
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            TriangleToggle(symbol = "▶", enabled = !vm.rightPanelOpen.value, onClick = { vm.toggleLeftPanel() })
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            TriangleToggle(symbol = "◀", enabled = !vm.leftPanelOpen.value, onClick = { vm.toggleRightPanel() })
        }

        AnimatedVisibility(
            visible = vm.leftPanelOpen.value,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it },
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            LeftPanel(
                onAddComponent = { kind ->
                    vm.addComponent(kind, RelativePosition(0.5f, 0.5f))
                },
                onBack = { vm.requestLeave(onLeave) },
            )
        }

        AnimatedVisibility(
            visible = vm.rightPanelOpen.value,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it },
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            SequencePanel(
                items = vm.items,
                onReorder = { from, to -> vm.moveInSequence(from, to) },
                onItemTap = { uiId -> vm.openPopup(uiId) },
                onSave = { vm.requestSave() },
            )
        }
    }

    // 屬性彈出視窗(存在時外層已經阻擋新增/選取其他元件的路徑)
    vm.popupTarget.value?.let { target ->
        PropertyPopup(
            item = target,
            subScriptChoices = vm.subScriptChoices.value,
            onCancel = { vm.closePopupWithoutSaving() },
            onDelete = { vm.deleteFromPopup() },
            onConfirm = { updated -> vm.confirmPopup(updated) },
        )
    }

    // 離開確認(Q13, Q24)
    if (vm.showLeaveConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { vm.cancelLeave() },
            title = { Text("尚未儲存") },
            text = { Text("確定要離開嗎?目前的變更將會遺失。") },
            confirmButton = { TextButton(onClick = { vm.confirmLeave() }) { Text("確定") } },
            dismissButton = { TextButton(onClick = { vm.cancelLeave() }) { Text("取消") } },
        )
    }

    // 儲存腳本第一步:確認視窗
    if (vm.showSaveConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { vm.cancelSaveConfirm() },
            title = { Text("儲存腳本") },
            text = { Text("確定要儲存這個腳本嗎?") },
            confirmButton = { TextButton(onClick = { vm.proceedToNameInput() }) { Text("確定") } },
            dismissButton = { TextButton(onClick = { vm.cancelSaveConfirm() }) { Text("取消") } },
        )
    }

    // 儲存腳本第二步:輸入名稱(編輯既有腳本時預填舊名稱)
    if (vm.showNameInputDialog.value) {
        var name by remember { mutableStateOf(vm.existingName.value) }
        AlertDialog(
            onDismissRequest = { vm.cancelNameInput() },
            title = { Text("設定腳本名稱") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("名稱(1-8 字元,不可重複)") },
                    )
                    vm.nameInputError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.submitName(name) { onSaved() } }) { Text("儲存") }
            },
            dismissButton = { TextButton(onClick = { vm.cancelNameInput() }) { Text("取消") } },
        )
    }
}

@Composable
private fun CanvasIcon(
    position: RelativePosition,
    canvasW: Float,
    canvasH: Float,
    color: Color,
    label: String,
    onMove: (Offset) -> Unit,
    onTap: () -> Unit,
) {
    val xPx = (position.xPercent * canvasW).roundToInt()
    val yPx = (position.yPercent * canvasH).roundToInt()
    Box(modifier = Modifier.offset { IntOffset(xPx - 20.dp.roundToPx(), yPx - 20.dp.roundToPx()) }) {
        DraggableComponentIcon(offsetPx = Offset(xPx.toFloat(), yPx.toFloat()), color = color, label = label, onMove = onMove, onTap = onTap)
    }
}

@Composable
private fun TriangleToggle(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(24.dp)
            .background(if (enabled) Color(0x33FFFFFF) else Color(0x11FFFFFF))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = symbol, color = if (enabled) Color.White else Color.Gray)
    }
}
