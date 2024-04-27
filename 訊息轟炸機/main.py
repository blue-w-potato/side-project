import pyautogui
times = int(input("要輸入幾次\n"))
pyautogui.hotkey("alt","tab")
for i in range(times):
    pyautogui.hotkey("ctrl","v")
    pyautogui.press("enter")

# 瑪卡巴卡