package com.scriptauto.app.data

import kotlinx.serialization.Serializable

/**
 * 元件顏色對照(CONTEXT.md):
 * 循環點擊=黃 拖曳=藍 等待=紅 按住=橘 其他腳本=綠
 */
enum class ComponentColor(val hex: String) {
    YELLOW("#FFEB3B"),
    BLUE("#2196F3"),
    RED("#F44336"),
    ORANGE("#FF9800"),
    GREEN("#4CAF50"),
}

/** 相對座標:以裝置螢幕寬高的百分比表示(0f..1f),見 ADR-0002 */
@Serializable
data class RelativePosition(val xPercent: Float, val yPercent: Float) {
    init {
        require(xPercent in 0f..1f && yPercent in 0f..1f) { "相對座標必須介於 0..1 之間" }
    }
}

/** 腳本建立時選定的方向,編排中不可切換;啟動前需與裝置實際方向比對,見 ADR-0002 */
@Serializable
enum class ScriptOrientation { PORTRAIT, LANDSCAPE }

@Serializable
sealed class LoopCount {
    @Serializable
    data object Infinite : LoopCount()

    /** 1 <= count <= 10^6,預設 1(使用者原始規格打錯過兩次,已在對話中修正確認) */
    @Serializable
    data class Fixed(val count: Int) : LoopCount() {
        init {
            require(count in 1..1_000_000) { "循環次數必須介於 1 到 1,000,000 之間" }
        }
    }
}

/**
 * 序列中的元件,以 sealed class 表示五種元件。
 * sequenceIndex:此元件在序列中的第幾步(拖曳的起點與終點共用同一個 sequenceIndex,見 CONTEXT.md)。
 * 只有 LoopTap / Hold / DragAnchor 具有 position,對應「有畫面圖示」的元件;
 * Wait / SubScript 沒有 position,對應「不出現在編排畫面上,只在右側欄序列」的元件。
 */
@Serializable
sealed class ScriptComponent {
    abstract val sequenceIndex: Int
    abstract val color: ComponentColor

    @Serializable
    data class LoopTap(
        override val sequenceIndex: Int,
        val position: RelativePosition,
        val jitterRadiusMm: Int = 0,
        val intervalMs: Int = 1000,
        val loopCount: LoopCount = LoopCount.Infinite,
        override val color: ComponentColor = ComponentColor.YELLOW,
    ) : ScriptComponent() {
        init {
            require(jitterRadiusMm >= 0) { "誤差範圍不可為負" }
            require(intervalMs > 0) { "間隔時間必須大於 0" }
        }
    }

    /** 拖曳元件的一個錨點(起點或終點) */
    @Serializable
    data class DragAnchor(
        val position: RelativePosition,
        val jitterRadiusMm: Int = 0,
    ) {
        init { require(jitterRadiusMm >= 0) { "誤差距離不可為負" } }
    }

    @Serializable
    data class Drag(
        override val sequenceIndex: Int,
        val start: DragAnchor,
        val end: DragAnchor,
        val durationMs: Int = 1000,
        override val color: ComponentColor = ComponentColor.BLUE,
    ) : ScriptComponent() {
        init { require(durationMs > 0) { "拖曳花費時間必須大於 0" } }
    }

    @Serializable
    data class Wait(
        override val sequenceIndex: Int,
        val durationMs: Int = 1000,
        override val color: ComponentColor = ComponentColor.RED,
    ) : ScriptComponent() {
        init { require(durationMs >= 0) { "時長不可為負" } }
    }

    @Serializable
    data class Hold(
        override val sequenceIndex: Int,
        val position: RelativePosition,
        val durationMs: Int = 1000,
        override val color: ComponentColor = ComponentColor.ORANGE,
    ) : ScriptComponent() {
        init { require(durationMs >= 0) { "時長不可為負" } }
    }

    /** 引用另一筆已儲存腳本;選取清單需排除自身,見 CONTEXT.md「其他腳本(元件)」 */
    @Serializable
    data class SubScript(
        override val sequenceIndex: Int,
        val referencedScriptId: Long,
        override val color: ComponentColor = ComponentColor.GREEN,
    ) : ScriptComponent()
}

/** 排序/刪除元件後,重新編號 sequenceIndex 時使用(拖曳元件仍只佔一個編號) */
fun ScriptComponent.withSequenceIndex(newIndex: Int): ScriptComponent = when (this) {
    is ScriptComponent.LoopTap -> copy(sequenceIndex = newIndex)
    is ScriptComponent.Drag -> copy(sequenceIndex = newIndex)
    is ScriptComponent.Wait -> copy(sequenceIndex = newIndex)
    is ScriptComponent.Hold -> copy(sequenceIndex = newIndex)
    is ScriptComponent.SubScript -> copy(sequenceIndex = newIndex)
}

/** 是否為「有位置屬性、會出現在編排畫面上」的元件(Q17):循環點擊/拖曳/按住 */
fun ScriptComponent.hasCanvasIcon(): Boolean = when (this) {
    is ScriptComponent.LoopTap, is ScriptComponent.Drag, is ScriptComponent.Hold -> true
    is ScriptComponent.Wait, is ScriptComponent.SubScript -> false
}
@Serializable
data class ScriptRecord(
    val id: Long = 0,
    val name: String,
    val orientation: ScriptOrientation,
    val components: List<ScriptComponent>,
    val createdAtEpochMillis: Long,
) {
    init {
        require(name.length in 1..8) { "腳本名稱長度必須介於 1 到 8 字元(見對話確認)" }
    }
}
