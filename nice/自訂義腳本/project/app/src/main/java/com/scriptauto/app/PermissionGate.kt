package com.scriptauto.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager

/**
 * App 啟動第一步的權限檢查(原始規格 步驟1)。
 * 兩個權限一起檢查、一起引導使用者授予(Q25):
 *   1. 無障礙服務(ADR-0001,執行手勢的核心)
 *   2. 「顯示在其他應用程式上層」權限(懸浮視窗需要)
 * 只要其中一個未授予,就導去對應設定頁;使用者若拒絕/未完成授權就返回,
 * App 直接結束(Q23),使用者可以自行重新開啟再走一次流程。
 */
object PermissionGate {

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false
        val expectedComponent = "${context.packageName}/${context.packageName}.accessibility.AutomationAccessibilityService"
        return TextUtils.SimpleStringSplitter(':').apply { setString(enabledServices) }
            .asSequence().any { it.equals(expectedComponent, ignoreCase = true) }
            && am.isEnabled
    }

    fun canDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun allGranted(context: Context): Boolean =
        isAccessibilityServiceEnabled(context) && canDrawOverlays(context)

    fun accessibilitySettingsIntent(): Intent =
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)

    fun overlaySettingsIntent(context: Context): Intent =
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
}
