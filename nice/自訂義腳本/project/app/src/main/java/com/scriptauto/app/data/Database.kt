package com.scriptauto.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

@Entity(tableName = "scripts")
data class ScriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val orientation: ScriptOrientation,
    /** 序列化後的 List<ScriptComponent>,見 CONTEXT.md 元件定義 */
    val componentsJson: String,
    val createdAtEpochMillis: Long,
) {
    fun toRecord(): ScriptRecord = ScriptRecord(
        id = id,
        name = name,
        orientation = orientation,
        components = json.decodeFromString(componentsJson),
        createdAtEpochMillis = createdAtEpochMillis,
    )

    companion object {
        fun fromRecord(record: ScriptRecord): ScriptEntity = ScriptEntity(
            id = record.id,
            name = record.name,
            orientation = record.orientation,
            componentsJson = json.encodeToString(record.components),
            createdAtEpochMillis = record.createdAtEpochMillis,
        )
    }
}

class Converters {
    @TypeConverter
    fun fromOrientation(value: ScriptOrientation): String = value.name

    @TypeConverter
    fun toOrientation(value: String): ScriptOrientation = ScriptOrientation.valueOf(value)
}

@Dao
interface ScriptDao {
    /** 已建立的腳本列表依建立時間排序,新的在最上面(對話確認 Q16) */
    @Query("SELECT * FROM scripts ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM scripts")
    suspend fun getAllOnce(): List<ScriptEntity>

    @Query("SELECT * FROM scripts WHERE id = :id")
    suspend fun getById(id: Long): ScriptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ScriptEntity): Long

    @Query("DELETE FROM scripts WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Database(entities = [ScriptEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scriptDao(): ScriptDao
}
