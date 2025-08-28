import pyautogui
import time

pyautogui.hotkey( 'alt' , 'tab' )
pyautogui.click()
pyautogui.hotkey( 'ctrl' , 'space' )
pyautogui.press('e')

for i in range(531):
    
    pyautogui.rightClick()
    time.sleep(0.01)