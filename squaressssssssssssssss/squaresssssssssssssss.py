import turtle as t
t.shape('turtle')
t.speed('slow')
class sin:
    sin75 = (6**0.5 + 2**0.5)/4
def three_edges(long):
    for i in range(3):
        t.forward(long)
        t.left(90)
def main(n):
    while True:
        print(n)
        t.left(15)
        t.forward(n)
        t.left(90)
        n2 = n*0.8/sin.sin75
        three_edges(n2)
        n = n2+0
def start(n):
    three_edges(n)
    t.forward(n)
    t.left(90)
    main(n = n*sin.sin75)
t.penup()
t.goto(-300, -300)
t.pendown()
start(600)
