# 作者：張振銓
# github：https://github.com/blue-w-potato

import time
import pyautogui
pyautogui.hotkey( 'alt' , 'tab' )
while True:
    pyautogui.click() 
    time.sleep(5)
    pyautogui.press('right')
    time.sleep(5)
    pyautogui.press('left')