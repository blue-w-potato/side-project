import tkinter as tk
import numpy as np


def show_datas( screen, data:np.ndarray ):
    Lables = []
    
    Lables.append(tk.Label(screen,
                         text = f"次數：{data.size}",
                         bg = "yellow"))
    
    Lables.append(tk.Label(screen,
                     text = f"最差成績：{data.max()}",
                     bg = "yellow"))
    
    Lables.append(tk.Label(screen,
                         text = f"最好成績：{data.min()}",
                         bg = "yellow"))

    Lables.append(tk.Label(screen,
                         text = f"總平均：{data.mean()}",
                         bg = "yellow"))
    
    Lables.append(tk.Label(screen,
                         text = f"標準差：{data.std()}",
                         bg = "yellow"))
    
    for i, lable in enumerate(Lables):
        lable.place( x=1200, y=150 + i * 30 )