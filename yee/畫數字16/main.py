import turtle as t

t.shape('turtle')
t.speed('fastest')

t.shapesize(0.001, 0.001, 0.001)
t.left(90)
t.right(60)
for i in range(10):
    t.left(6)
    t.forward(8)

t.left(180)
for i in range(40):
    t.right(0.3)
    t.forward(5)

t.left(180)
for i in range(40):
    t.right(0.4)
    t.forward(5)

t.left(180)
for i in range(30):
    t.left(1)
    t.forward(5)

for i in range(415):
    t.forward(0.5)
    t.left(1)

t.mainloop()