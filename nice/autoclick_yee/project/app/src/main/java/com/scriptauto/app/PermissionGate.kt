package com.scriptauto.app

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.scriptauto.app.accessibility.AutomationAccessibilityService

/**
 * App 啟動第一步的權限檢查(原始規格 步驟1)。
 * 兩個權限一起檢查、一起引導使用者授予(Q25):
 *   1. 無障礙服務(ADR-0001,執行手勢的核心)
 *   2. 「顯示在其他應用程式上層」權限(懸浮視窗需要)
 * 只要其中一個未授予,就導去對應設定頁;使用者若拒絕/未完成授權就返回,
 * App 直接結束(Q23),使用者可以自行重新開啟再走一次流程。
 */
object PermissionGate {

    /**
     * 用 AccessibilityManager 官方 API 直接比對已啟用服務清單的 ComponentName,
     * 避免手動解析 Settings.Secure 字串在不同機型/版本上可能比對失敗的問題。
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val expected = ComponentName(context, AutomationAccessibilityService::class.java)
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        return enabledServices.any { info ->
            val serviceInfo = info.resolveInfo?.serviceInfo
            serviceInfo != null &&
                serviceInfo.packageName == expected.packageName &&
                serviceInfo.name == expected.className
        }
    }

    fun canDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun allGranted(context: Context): Boolean =
        isAccessibilityServiceEnabled(context) && canDrawOverlays(context)

    fun accessibilitySettingsIntent(): Intent =
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)

    fun overlaySettingsIntent(context: Context): Intent =
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
}
