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
plt.grid( True )
plt.scatter( x = [], y = [] )
table.draw()

# 建立csv檔
import original_to_csv
original_to_csv.main()

# 繪製圖表
import draw_original_table
def button_command( Category ):
    data = draw_original_table.get_data( Category = Category )# ( times -> list, speeds -> list )
    plt.scatter( x = data[0], y = data[1] )
    table.draw()
    
# 一排選擇單手、雙手之類的按鈕
Categorys = draw_original_table.get_Categorys()
fig_types_button = []
for i in range( len(Categorys) ):
    fig_types_button.append( tk.Button( screen, text = Categorys[i], bg = "lightblue", width = 10, command = button_command( Categorys[i] ) ) )
    fig_types_button[-1].place( x = 300, y = 0 + i * 30 )
    


screen.mainloop()