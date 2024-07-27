import tkinter as tk
import matplotlib as mb
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

# GUI 設定
screen = tk.Tk()
screen.title("魔方速度分析器")
screen.geometry ("1024x768")
fig = plt.figure( figsize = ( 10, 7 ), dpi = 100 )
table = FigureCanvasTkAgg( fig, screen )
table.get_tk_widget().place( x = 200, y = 150 )

# 建立csv檔
import original_to_csv
original_to_csv.main()

# 初始化
import init
init.main( screen, table )

# 繪製圖表

# 標題
import title
title.main( screen )

# 選擇模式
import Category_Buttons
import Canva_type_Buttons
Canva_type_Buttons.main.main( screen, [ [], [] ], table)
Category_Buttons.main.main( screen, table )

screen.mainloop()