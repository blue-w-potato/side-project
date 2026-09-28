package com.scriptauto.app.ui

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ScriptList : Screen("script_list")

    /** scriptId 為 null 代表新建腳本(第一次進編排畫面);非 null 代表從列表點「編輯」進來 */
    data object Editor : Screen("editor?scriptId={scriptId}") {
        fun buildRoute(scriptId: Long? = null): String =
            if (scriptId != null) "editor?scriptId=$scriptId" else "editor"
    }
}
