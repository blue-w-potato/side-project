import tkinter as tk
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import pandas as pd

# 介面設定
UI = tk.Tk()
UI.title( "不是，哥們" )
UI.geometry( "1000x700" )
fig = plt.figure( figsize = ( 10, 7 ), dpi = 100 )
NoBro = FigureCanvasTkAgg( fig, UI )
NoBro.get_tk_widget().place( x=0, y=0 )

# 布什．戈門函數
def noBro( y, h = 100, k = 50 ):
    x = (y - h)**2*2 + k
    return x

# x、y

y = pd.Series( range( 150, 60, -1 ) ) 
x = noBro( y )

# 畫出來

plt.xlim(40, 6000)
plt.ylim( 20, 170)
plt.plot( x, y, color = "black" )
plt.scatter( noBro(145), 145, color = "black" )
plt.draw()

# 字
Nobro = tk.Label( UI, text="不是，哥們", font=["Arial", 17] , bg = "white")
Nobro.place( x=600, y=110 )
nobro = tk.Label( UI, text="布什．戈門曲線", font=["Arial", 23] , bg = "white")
nobro.place( x=600, y=300 )

UI.mainloop()