package com.scriptauto.app.ui

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Display
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.scriptauto.app.accessibility.CoordinateResolver
import com.scriptauto.app.data.*
import com.scriptauto.app.service.FloatingControlService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScriptListViewModel(private val repository: ScriptRepository) : ViewModel() {

    val scripts: StateFlow<List<ScriptRecord>> =
        repository.observeScripts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var deleteTarget = androidx.compose.runtime.mutableStateOf<ScriptRecord?>(null)
    var blockedMessage = androidx.compose.runtime.mutableStateOf<String?>(null)

    fun requestDelete(script: ScriptRecord) {
        deleteTarget.value = script
    }

    fun cancelDelete() {
        deleteTarget.value = null
    }

    /** 使用中的腳本禁止刪除(Q22) */
    fun confirmDelete() {
        val target = deleteTarget.value ?: return
        viewModelScope.launch {
            when (val result = repository.deleteScript(target.id)) {
                is MutationResult.Blocked -> blockedMessage.value = result.reason
                MutationResult.Success -> Unit
            }
            deleteTarget.value = null
        }
    }

    /** 使用中的腳本禁止編輯(Q22),通過檢查才真的導去編排畫面 */
    fun tryEdit(script: ScriptRecord, onAllowed: () -> Unit) {
        viewModelScope.launch {
            when (val result = repository.canEdit(script.id)) {
                is MutationResult.Blocked -> blockedMessage.value = result.reason
                MutationResult.Success -> onAllowed()
            }
        }
    }

    /**
     * 啟動前檢查:Q9(全系統唯一懸浮視窗)、Q26(裝置目前方向需與腳本記錄的方向一致)。
     * 都通過才真的啟動 FloatingControlService。
     */
    fun tryStart(script: ScriptRecord, context: Context) {
        if (FloatingControlService.isAnyScriptActive()) {
            blockedMessage.value = "已經有腳本正在使用懸浮視窗,請先結束再啟動其他腳本"
            return
        }
        val resolver = buildResolver(context)
        if (!resolver.matchesOrientation(script.orientation)) {
            val wanted = if (script.orientation == ScriptOrientation.LANDSCAPE) "橫式" else "直式"
            blockedMessage.value = "這個腳本是以「$wanted」建立的,請將裝置切換為$wanted 後再啟動"
            return
        }
        if (com.scriptauto.app.accessibility.AutomationAccessibilityService.instance == null) {
            blockedMessage.value = "無障礙服務尚未啟用或尚未連線,請到系統設定的「協助工具/無障礙」頁面確認「自訂義腳本系統」已經開啟,再回來重新啟動"
            return
        }
        val intent = Intent(context, FloatingControlService::class.java).putExtra("scriptId", script.id)
        context.startService(intent)
    }

    fun dismissBlockedMessage() {
        blockedMessage.value = null
    }

    private fun buildResolver(context: Context): CoordinateResolver {
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getRealMetrics(metrics)
        return CoordinateResolver(metrics.widthPixels, metrics.heightPixels, metrics.xdpi, metrics.ydpi)
    }
}

class ScriptListViewModelFactory(private val repository: ScriptRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return ScriptListViewModel(repository) as T
    }
}
