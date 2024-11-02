import tkinter as tk
import matplotlib as mb
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

# 是否重新訓練
retrainning = input("是否要重新訓練") == "y"
# retrainning = False

# GUI 設定
screen = tk.Tk()
screen.title("魔方速度分析器")
screen.geometry ("1800x1000")
screen.resizable(False, False)
fig = plt.figure( figsize = ( 10, 7 ), dpi = 100 )
table = FigureCanvasTkAgg( fig, screen )
table.get_tk_widget().place( x = 200, y = 150 )

# 建立csv檔
import original_to_csv
original_to_csv.main()

# 初始化
import init
init.main( retrainning, screen, table )
import Linear_Regression

Linear_Regression.button.set_button( screen, table )

screen.mainloop()