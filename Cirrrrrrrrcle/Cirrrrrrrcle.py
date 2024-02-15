import turtle as t
import time as tm
t.colormode(255)
t.shape('turtle')
t.speed('fastest')
t.pensize(10)


def draw_circles(size):
    #定位初始位置
    t.penup()
    t.goto(0,400)
    #畫

    color = [0, 0, 0]
    t.pencolor(color)

    '''
    up(0.00,400.00)         400**
    right(425.98,-24.22)    425
    down(-23.04,-439.80)    -439
    left(-407.12,-25.94)    -407
    '''

    tm.sleep(0)
    t.pendown()
    while size > 0:
        print(t.pos())
        t.forward(size)
        t.right( 0.81 )
        size -= 0.0005
        

screen = t.Screen()
draw_circles(6)
screen.mainloop()