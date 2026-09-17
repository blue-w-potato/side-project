package com.scriptauto.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 追蹤目前「使用中」的腳本(對話 Q22):
 * 從懸浮視窗被啟動的那一刻起,到使用者按下「結束」關閉懸浮視窗為止,
 * 無論當下是「執行中」還是「中止」待命,都算使用中,禁止編輯/刪除。
 * 因為同時間全系統只能有一個懸浮視窗(Q9),這裡只需要追蹤單一 id。
 */
object InUseScriptTracker {
    @Volatile
    private var activeScriptId: Long? = null

    fun markInUse(scriptId: Long) {
        activeScriptId = scriptId
    }

    /** 對應懸浮視窗按下「結束」 */
    fun clear() {
        activeScriptId = null
    }

    fun isInUse(scriptId: Long): Boolean = activeScriptId == scriptId

    fun currentActiveId(): Long? = activeScriptId
}

sealed class SaveResult {
    data class Success(val savedId: Long) : SaveResult()
    data class Rejected(val reason: String) : SaveResult()
}

sealed class MutationResult {
    data object Success : MutationResult()
    data class Blocked(val reason: String) : MutationResult()
}

class ScriptRepository(private val dao: ScriptDao) {

    fun observeScripts(): Flow<List<ScriptRecord>> =
        dao.observeAll().map { list -> list.map { it.toRecord() } }

    suspend fun getScript(id: Long): ScriptRecord? = dao.getById(id)?.toRecord()

    /** 供「其他腳本」元件的選取清單使用,已排除自身(見 ScriptValidation) */
    suspend fun selectableSubScripts(currentScriptId: Long?): List<ScriptRecord> =
        ScriptValidation.selectableSubScripts(dao.getAllOnce().map { it.toRecord() }, currentScriptId)

    /** 儲存腳本:先做名稱規則 + 循環引用偵測(ADR-0003),都通過才寫入資料庫 */
    suspend fun saveScript(candidate: ScriptRecord, excludingId: Long?): SaveResult {
        val allScripts = dao.getAllOnce().map { it.toRecord() }
        when (val result = ScriptValidation.validateAll(candidate, allScripts, excludingId)) {
            is ValidationResult.Error -> return SaveResult.Rejected(result.message)
            ValidationResult.Ok -> Unit
        }
        val id = dao.upsert(ScriptEntity.fromRecord(candidate))
        return SaveResult.Success(id)
    }

    /** 刪除腳本:使用中則擋下(Q22) */
    suspend fun deleteScript(id: Long): MutationResult {
        if (InUseScriptTracker.isInUse(id)) {
            return MutationResult.Blocked("這個腳本正在使用中,請先結束懸浮視窗後再刪除")
        }
        dao.deleteById(id)
        return MutationResult.Success
    }

    /** 進入編輯畫面前的檢查:使用中則擋下(Q22) */
    suspend fun canEdit(id: Long): MutationResult {
        if (InUseScriptTracker.isInUse(id)) {
            return MutationResult.Blocked("這個腳本正在使用中,請先結束懸浮視窗後再編輯")
        }
        return MutationResult.Success
    }
}
