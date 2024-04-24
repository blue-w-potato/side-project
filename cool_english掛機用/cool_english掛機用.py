# 作者：張振銓
# github：https://github.com/blue-w-potato

import time
import pyautogui
pyautogui . hotkey( 'alt' , 'tab' )
while True:
    time.sleep(180)
    pyautogui.press('right')
    time.sleep(180)
    pyautogui.press('left')