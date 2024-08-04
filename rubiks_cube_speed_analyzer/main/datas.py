import tkinter as tk
import numpy as np


def show_datas( screen, data ):
    Lables = []
    Lables.append(tk.Label(screen,
                         text = f"{f"次數：{data.size}":<30}",
                         bg = "yellow"))
    
    Lables.append(tk.Label(screen,
                         text = f"{f"最好成績：{data.min()}":<30}",
                         bg = "yellow"))
    
    Lables.append(tk.Label(screen,
                     text = f"{f"最差成績：{data.max()}":<30}",
                     bg = "yellow"))

    Lables.append(tk.Label(screen,
                         text = f"{f"總平均：{data.mean()}":<30}",
                         bg = "yellow"))
    
    Lables.append(tk.Label(screen,
                         text = f"{f"標準差：{data.std()}":<30}",
                         bg = "yellow"))
    
    for i, lable in enumerate(Lables):
        lable.place( x=1200, y=150 + i * 30 )