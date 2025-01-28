import time
import pyautogui

pyautogui.hotkey( 'alt' , 'tab' )
pyautogui.press('e')

while True:
    pyautogui.rightClick()
    time.sleep(13)