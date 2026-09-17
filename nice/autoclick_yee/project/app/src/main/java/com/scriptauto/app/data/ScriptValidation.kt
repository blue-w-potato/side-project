package com.scriptauto.app.data

sealed class ValidationResult {
    data object Ok : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object ScriptValidation {

    /**
     * 名稱規則(對話確認):1..8 字元,不允許與其他既有腳本重複。
     * excludingId:編輯既有腳本時排除自己,避免跟自己比對出「重複」。
     */
    fun validateName(name: String, allScripts: List<ScriptRecord>, excludingId: Long?): ValidationResult {
        if (name.length !in 1..8) {
            return ValidationResult.Error("腳本名稱長度必須介於 1 到 8 字元")
        }
        val duplicate = allScripts.any { it.name == name && it.id != excludingId }
        if (duplicate) {
            return ValidationResult.Error("已經有腳本使用這個名稱,請換一個")
        }
        return ValidationResult.Ok
    }

    /**
     * 循環引用偵測(ADR-0003):遍歷「其他腳本」元件形成的呼叫圖,偵測是否會形成循環。
     * candidate:準備儲存的腳本(可能是新腳本或編輯後的版本,尚未真正寫入 allScripts)。
     * allScripts:目前資料庫中其他已儲存的腳本(不含 candidate 本身的舊版本)。
     */
    fun detectCircularReference(candidate: ScriptRecord, allScripts: List<ScriptRecord>): ValidationResult {
        val byId = (allScripts + candidate).associateBy { it.id }

        fun dfs(currentId: Long, visiting: MutableSet<Long>, visited: MutableSet<Long>): Boolean {
            if (currentId in visiting) return true // 找到循環
            if (currentId in visited) return false
            visiting += currentId
            val script = byId[currentId]
            script?.components.orEmpty()
                .filterIsInstance<ScriptComponent.SubScript>()
                .forEach { sub ->
                    if (dfs(sub.referencedScriptId, visiting, visited)) return true
                }
            visiting -= currentId
            visited += currentId
            return false
        }

        val hasCycle = dfs(candidate.id, mutableSetOf(), mutableSetOf())
        return if (hasCycle) {
            ValidationResult.Error("這個腳本會與其他腳本形成循環引用,無法儲存")
        } else {
            ValidationResult.Ok
        }
    }

    /** 供「其他腳本」元件的選取清單使用:排除自己,見 CONTEXT.md */
    fun selectableSubScripts(allScripts: List<ScriptRecord>, currentScriptId: Long?): List<ScriptRecord> =
        allScripts.filter { it.id != currentScriptId }

    fun validateAll(candidate: ScriptRecord, allScripts: List<ScriptRecord>, excludingId: Long?): ValidationResult {
        val nameResult = validateName(candidate.name, allScripts, excludingId)
        if (nameResult is ValidationResult.Error) return nameResult
        return detectCircularReference(candidate, allScripts.filter { it.id != excludingId })
    }
}
