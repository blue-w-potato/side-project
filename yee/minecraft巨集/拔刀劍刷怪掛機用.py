import pyautogui
import time

pyautogui.hotkey( 'alt' , 'tab' )
pyautogui.click()
pyautogui.press('e')

while True:
    
    pyautogui.rightClick()
    time.sleep(0.5)
    pyautogui.rightClick()
    time.sleep(0.5)
    pyautogui.rightClick()
    time.sleep(2)