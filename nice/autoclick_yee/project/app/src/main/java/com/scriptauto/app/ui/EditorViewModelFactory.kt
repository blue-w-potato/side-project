package com.scriptauto.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.scriptauto.app.data.ScriptRepository

class EditorViewModelFactory(
    private val repository: ScriptRepository,
    private val existingScriptId: Long?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return EditorViewModel(repository, existingScriptId) as T
    }
}
