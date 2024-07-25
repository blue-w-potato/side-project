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

# 繪製圖表
import get_original_data

# 一排選擇單手、雙手之類的按鈕
Categorys = get_original_data.get_Categorys()
fig_types_button = []
Category = 0

def button_command(selected_category):
    global fig_types_button
    data = get_original_data.get_data(Category=selected_category)  # (times -> list, speeds -> list)
    plt.clf()
    plt.scatter(x=data[0], y=data[1])
    plt.xlabel("Speed")
    plt.ylabel("Time")
    plt.grid( True )
    table.draw()

for i, category in enumerate(Categorys):
    fig_types_button.append(tk.Button(
        screen,
        text=category,
        bg="lightblue",
        width=10,
        command=lambda cat=category: button_command(cat)
    ))
    fig_types_button[-1].place(x=300, y=0 + i * 30)


screen.mainloop()