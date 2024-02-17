import turtle as t
t.colormode(255)
t.shape('turtle')

def draw_quarter_circle(center, radius):
    t.penup()
    t.goto(center)
    t.pendown()
    t.circle(radius, 90)

#頭部主體
    
#上面
t.penup()
t.goto(-300, 200)
t.pendown()
t.goto(300, 200)
t.penup()

#下面
t.goto(-300, -200)
t.pendown()
t.goto(300, -200)
t.penup()

#右邊
t.penup()
t.right(0)
draw_quarter_circle((300, -200), 100)
t.penup()
t.goto(400, -100)
t.pendown()
t.goto(400, 100)
draw_quarter_circle((400, 100), 100)
t.penup()

#左邊
draw_quarter_circle((-300, 200), 100)
t.penup()
t.goto(-400, 100)
t.pendown()
t.goto(-400, -100)
t.penup()
draw_quarter_circle((-400, -100), 100)
t.penup()

#左眼
t.goto(-200, 50)
t.pendown()
t.circle(50, 360)
t.penup()

#左瞳孔

t.goto( -200, 75 )
t.pendown()
t.begin_fill()
t.circle(25, 360)
t.end_fill()
t.penup()

#右眼
t.goto(200, 50)
t.pendown()
t.circle(50, 360)
t.penup()

#右瞳孔
t.goto( 200, 75 )
t.pendown()
t.begin_fill()
t.circle(25, 360)
t.end_fill()
t.penup()

#左鼻恐
t.goto(-50, 0)
t.pendown()
t.begin_fill()
t.circle(10, 360)
t.end_fill()
t.penup()

#右鼻恐
t.goto(50, 0)
t.pendown()
t.begin_fill()
t.circle(10, 360)
t.end_fill()
t.penup()

#嘴巴
a = ( 10 ) / (200**2)
t.goto(-200, 0)
t.pendown()
x, y = -200, 0
while t.pos()!= (200, 0):
    x = round(x+0.5, 4)
    y = round( a* (x**2)-10, 4)
    t.goto((x,y))
t.penup()
a = ( 110 ) / (200**2)
t.goto(-200, 0)
t.pendown()
x, y = -200, 0
while t.pos()!= (200, 0):
    x = round(x+0.5, 4)
    y = round( a* (x**2)-110, 4)
    t.goto((x,y))
