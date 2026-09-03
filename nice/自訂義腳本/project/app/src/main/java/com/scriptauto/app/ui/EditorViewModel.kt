package com.scriptauto.app.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scriptauto.app.data.*
import kotlinx.coroutines.launch

/** 序列中每一項的 UI 包裝:uiId 在編輯過程中保持穩定,不受重新編號 sequenceIndex 影響 */
data class EditorItem(val uiId: Int, val component: ScriptComponent)

enum class ComponentKind { LOOP_TAP, DRAG, WAIT, HOLD, SUB_SCRIPT }

class EditorViewModel(
    private val repository: ScriptRepository,
    val orientation: ScriptOrientation,
    private val existingScriptId: Long?,
) : ViewModel() {

    val items = mutableStateListOf<EditorItem>()
    private var nextUiId = 0

    var leftPanelOpen = mutableStateOf(false)
    var rightPanelOpen = mutableStateOf(false)

    /** 目前彈出屬性視窗要編輯的項目;null = 沒有彈出視窗 */
    var popupTarget = mutableStateOf<EditorItem?>(null)

    var hasUnsavedChanges = mutableStateOf(false)
    var isLoaded = mutableStateOf(existingScriptId == null)

    /** 儲存確認流程狀態 */
    var showSaveConfirmDialog = mutableStateOf(false)
    var showNameInputDialog = mutableStateOf(false)
    var nameInputError = mutableStateOf<String?>(null)
    var existingName = mutableStateOf("") // 編輯既有腳本時預填舊名稱(原始規格)

    var showLeaveConfirmDialog = mutableStateOf(false)
    var pendingLeaveAction: (() -> Unit)? = null

    var subScriptChoices = mutableStateOf<List<ScriptRecord>>(emptyList())

    init {
        if (existingScriptId != null) {
            viewModelScope.launch {
                val record = repository.getScript(existingScriptId)
                record?.components?.forEach { c -> items.add(EditorItem(nextUiId++, c)) }
                existingName.value = record?.name.orEmpty()
                isLoaded.value = true
                hasUnsavedChanges.value = false
            }
        }
        refreshSubScriptChoices()
    }

    private fun refreshSubScriptChoices() {
        viewModelScope.launch {
            subScriptChoices.value = repository.selectableSubScripts(existingScriptId)
        }
    }

    private fun renumber() {
        for (i in items.indices) {
            items[i] = items[i].copy(component = items[i].component.withSequenceIndex(i))
        }
    }

    fun toggleLeftPanel() {
        if (rightPanelOpen.value) return // 兩側欄不可同時展開
        leftPanelOpen.value = !leftPanelOpen.value
    }

    fun toggleRightPanel() {
        if (leftPanelOpen.value) return
        rightPanelOpen.value = !rightPanelOpen.value
    }

    /** 左側欄點擊元件圖示新增元件。新增後面板保持展開(Q27),可連續新增。 */
    fun addComponent(kind: ComponentKind, defaultCenter: RelativePosition) {
        val nextIndex = items.size
        val component: ScriptComponent = when (kind) {
            ComponentKind.LOOP_TAP -> ScriptComponent.LoopTap(sequenceIndex = nextIndex, position = defaultCenter)
            ComponentKind.DRAG -> ScriptComponent.Drag(
                sequenceIndex = nextIndex,
                start = ScriptComponent.DragAnchor(RelativePosition(0.3f, 0.5f)),
                end = ScriptComponent.DragAnchor(RelativePosition(0.7f, 0.5f)),
            )
            ComponentKind.WAIT -> ScriptComponent.Wait(sequenceIndex = nextIndex)
            ComponentKind.HOLD -> ScriptComponent.Hold(sequenceIndex = nextIndex, position = defaultCenter)
            ComponentKind.SUB_SCRIPT -> {
                val first = subScriptChoices.value.firstOrNull() ?: return // 沒有其他腳本可引用時不新增
                ScriptComponent.SubScript(sequenceIndex = nextIndex, referencedScriptId = first.id)
            }
        }
        items.add(EditorItem(nextUiId++, component))
        hasUnsavedChanges.value = true
    }

    /** 拖曳畫布圖示改變位置(只有 LoopTap/Hold/Drag 的起訖點會呼叫這個) */
    fun updatePosition(uiId: Int, isDragStart: Boolean?, newPosition: RelativePosition) {
        val idx = items.indexOfFirst { it.uiId == uiId }
        if (idx == -1) return
        val updated = when (val c = items[idx].component) {
            is ScriptComponent.LoopTap -> c.copy(position = newPosition)
            is ScriptComponent.Hold -> c.copy(position = newPosition)
            is ScriptComponent.Drag -> when (isDragStart) {
                true -> c.copy(start = c.start.copy(position = newPosition))
                false -> c.copy(end = c.end.copy(position = newPosition))
                null -> c
            }
            else -> return
        }
        items[idx] = items[idx].copy(component = updated)
        hasUnsavedChanges.value = true
    }

    /** 右側欄序列重新排序(拖曳積木改變順序) */
    fun moveInSequence(from: Int, to: Int) {
        if (from == to || from !in items.indices || to !in items.indices) return
        val item = items.removeAt(from)
        items.add(to, item)
        renumber()
        hasUnsavedChanges.value = true
    }

    fun openPopup(uiId: Int) {
        if (popupTarget.value != null) return // 彈出視窗存在時不能選取其他元件
        items.firstOrNull { it.uiId == uiId }?.let { popupTarget.value = it }
    }

    fun closePopupWithoutSaving() {
        popupTarget.value = null
    }

    /** 屬性視窗按下確定:驗證通過才套用,見各元件 data class 的 init 檢查 */
    fun confirmPopup(updated: ScriptComponent): String? {
        val idx = items.indexOfFirst { it.uiId == popupTarget.value?.uiId }
        if (idx == -1) return "找不到這個元件"
        return try {
            items[idx] = items[idx].copy(component = updated)
            hasUnsavedChanges.value = true
            popupTarget.value = null
            null
        } catch (e: IllegalArgumentException) {
            e.message ?: "屬性不合規定"
        }
    }

    /** 屬性視窗內按下刪除:不需二次確認(Q14),連帶刪除拖曳元件的另一個錨點 */
    fun deleteFromPopup() {
        val target = popupTarget.value ?: return
        items.removeAll { it.uiId == target.uiId }
        renumber()
        popupTarget.value = null
        hasUnsavedChanges.value = true
    }

    /** 退回鍵 / 系統返回鍵共用邏輯(Q10, Q13, Q24) */
    fun requestLeave(onLeave: () -> Unit) {
        if (!hasUnsavedChanges.value) {
            onLeave()
            return
        }
        pendingLeaveAction = onLeave
        showLeaveConfirmDialog.value = true
    }

    fun confirmLeave() {
        showLeaveConfirmDialog.value = false
        pendingLeaveAction?.invoke()
        pendingLeaveAction = null
    }

    fun cancelLeave() {
        showLeaveConfirmDialog.value = false
        pendingLeaveAction = null
    }

    fun requestSave() {
        showSaveConfirmDialog.value = true
    }

    fun cancelSaveConfirm() {
        showSaveConfirmDialog.value = false
    }

    fun proceedToNameInput() {
        showSaveConfirmDialog.value = false
        nameInputError.value = null
        showNameInputDialog.value = true
    }

    fun cancelNameInput() {
        showNameInputDialog.value = false
    }

    fun submitName(name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val record = ScriptRecordDraft.build(
                id = existingScriptId ?: 0,
                name = name,
                orientation = orientation,
                items = items,
                createdAtEpochMillis = System.currentTimeMillis(),
            )
            if (record == null) {
                nameInputError.value = "腳本名稱長度必須介於 1 到 8 字元"
                return@launch
            }
            when (val result = repository.saveScript(record, excludingId = existingScriptId)) {
                is SaveResult.Rejected -> nameInputError.value = result.reason
                is SaveResult.Success -> {
                    showNameInputDialog.value = false
                    hasUnsavedChanges.value = false
                    onSuccess()
                }
            }
        }
    }
}

private object ScriptRecordDraft {
    fun build(
        id: Long,
        name: String,
        orientation: ScriptOrientation,
        items: List<EditorItem>,
        createdAtEpochMillis: Long,
    ): ScriptRecord? {
        if (name.length !in 1..8) return null
        return ScriptRecord(
            id = id,
            name = name,
            orientation = orientation,
            components = items.mapIndexed { index, item -> item.component.withSequenceIndex(index) },
            createdAtEpochMillis = createdAtEpochMillis,
        )
    }
}
