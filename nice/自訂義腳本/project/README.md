# 專案進度

依照 `CONTEXT.md` 與 `docs/adr/` 中確認的決策,**原始規格描述的整條主線流程都已經串起來了**:主頁 → 新建腳本(橫式/直式)→ 編排(拖曳定位、屬性設定、序列排序)→ 儲存 → 已建立的腳本列表(刪除/編輯/啟動)→ 懸浮視窗(顯示/隱藏元件圖示、執行/中止、結束)。

## 已完成

- `data/Models.kt` — 腳本與五種元件的資料模型
- `data/ScriptValidation.kt` — 名稱規則(1–8 字元、不重複)與循環引用偵測(ADR-0003)
- `data/Database.kt` — Room Entity / DAO(ADR-0004),依建立時間新到舊排序(Q16)
- `data/ScriptRepository.kt` — 驗證規則 + `InUseScriptTracker`(Q22)
- `accessibility/AutomationAccessibilityService.kt` — 無障礙服務(ADR-0001)、`CoordinateResolver`(誤差範圍、Q26 方向檢查、`toPixel` 供圖示顯示用)、`ScriptExecutor`
- `service/FloatingControlService.kt` + `FloatingOverlayController.kt` — **懸浮視窗完整實作**:
  - `onStartCommand` 接收啟動請求,查腳本、跑 Q9/Q26 檢查
  - 通過後用 `WindowManager.addView` 畫出固定在右上角的控制列(三個按鍵),文字會隨狀態切換(顯示/隱藏元件圖示、執行/中止)
  - 「顯示元件圖示」會把腳本中所有有位置屬性的元件,依 `CoordinateResolver.toPixel` 換算成裝置實際座標,疊加成一堆不可互動、不攔截觸控的小色塊(`FLAG_NOT_TOUCHABLE`)
  - 「結束」會移除所有 overlay view、清除 `InUseScriptTracker` 的使用中標記、停止 Service
- `PermissionGate.kt` + `MainActivity.kt` — 權限檢查(Q25、Q23)+ Compose Navigation 串起全部畫面
- `ui/HomeScreen.kt`、`ui/NewScriptOrientationScreen.kt`、`ui/EditorScreen.kt` 系列、`ui/ScriptListScreen.kt` 系列 — 全部畫面已組裝並接上導覽

## 已知限制 / 可以再打磨的地方

- `FloatingControlService` 目前是一般 Service,不是 Foreground Service。Android 8+ 對背景 Service 有執行限制,使用者切到別的 App 一段時間後,系統有機率會回收這個 Service,導致懸浮視窗消失。真的要上架,建議之後補上 `startForeground()` + 常駐通知。
- 屬性驗證錯誤訊息目前用簡單文字顯示,可以再美化
- 懸浮視窗控制列目前固定在右上角,沒有做「可拖曳移動位置」(原始規格沒有明確要求,先略過)

整個流程的骨架已經到齊了;接下來如果要往下走,通常是接上面這幾點打磨細節,或是幫個別元件(尤其是循環點擊/拖曳的實際手勢派送)做真機測試調整。



