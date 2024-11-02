import turtle as t
t.colormode(255)
t.shape('turtle')
t.speed('fastest')

def square(n):
    for i in range(4):
        t.forward(n)
        t.left(90)

def main(n):
    while True:
        #turn left 15
        square(n)
        t.penup()
        t.left(180)
        t.forward(round(n*0.08748866352, 20))#tan 5度
        t.right(90)
        t.left(15)
        t.pendown()
        n = round(n / 0.99619469809, 20)

main(300)