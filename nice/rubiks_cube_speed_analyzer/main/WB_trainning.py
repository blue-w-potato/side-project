import matplotlib.pyplot as plt
import pandas as pd
import tkinter as tk
from get_original_data import get_Categorys, get_data
import numpy as np

def cost_function( x, y, w, b ):
        y_pred = x*w + b
        cost = ( y - y_pred )**2
        cost = cost.sum() / x.size
        return cost


def gradient_descent( Category ):
    data = get_data( Category )
    x = data[0]
    y = data[1]
    
    w, b = 0, y.mean()
    Learning_rate = 1.0e-10
    run_iter =  10
    n_cost = cost_function( x, y, w, b )
    speed = 1

    
    while 1:
        cost = n_cost + 0
        if cost < run_iter:
            return f"{w} {b} {Learning_rate}\n"
        
        
        wm = (2*x*( w*x - y + b )).sum() / len(x)
        
        bm = (2*( w*x - y + b )).sum() / len(x)
        w -= wm*Learning_rate
        b -= bm*Learning_rate
        n_cost = cost_function( x, y, w, b )
        if n_cost > cost:
            Learning_rate *= -0.1
            speed = 1
            w -= wm*Learning_rate
            b -= bm*Learning_rate
        else:
            Learning_rate *= (1.5 * speed)
            speed += 0.5
        
        print( f"cost={cost}\trun_iter = {run_iter}" )
        

def Trainning():
    Categorys = get_Categorys()
    datas = ""
    for Category in Categorys:
        a = gradient_descent( Category )
        print( "finish" )
        datas += a
    with open( file = "C:\\Users\\88690\\Desktop\\side-project\\nice\\rubiks_cube_speed_analyzer\\main\\WBs.txt", mode = "wt", encoding = "utf-8" ) as txtfile:
        print( datas, end = "", file = txtfile )
