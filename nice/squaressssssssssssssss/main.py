import turtle as t
import time as mm
t.colormode(255)
t.shape('turtle')
t.speed('fastest')

def square(n):
    for i in range(4):
        t.forward(n)
        t.left(90)

def main(n):
    while True:
        square(n)
        t.penup()
        t.left(90)
        n2 = round(n*0.08748866352, 20)
        t.forward(n2)
        t.right(95)
        t.pendown()
        n = round(((n - n2)**2 + n2**2)**0.5, 20)

t.penup()
t.goto(-300, -300)
t.pendown()
main(600)