import turtle as t
import time as mm
t.colormode(255)
t.shape('turtle')
t.pencolor(255, 255, 255)
t.speed('fastest')

def square(n):
    for i in range(4):
        t.forward(n)
        t.left(90)

def main(n):
    while True:
        #turn left 15
        t.begin_fill()
        square(n)
        t.end_fill()
        t.penup()
        t.left(90)
        t.forward(round(n*0.08748866352, 20))#tan 5度
        t.right(95)
        t.pendown()
        n = round((n**2 + round(n*0.08748866352, 20)**2)**0.5, 20)

main(300)