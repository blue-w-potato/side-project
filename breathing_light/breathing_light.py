import pygame
pygame.init()
color = [0, 0, 0]
FPS = 120
screen = pygame.display.set_mode((1000, 500))
clock = pygame.time.Clock()
running = True
def change_color(array):
    if array[0]!=255 and array[1]==array[2]==0:
        array[0] +=1
        return array
    if array[1]!=255 and array[0]==255 and array[2]==0:
        array[1] +=1
        return array
    if array[2]!=255 and array[0]==255 and array[1]==255:
        array[2] +=1
        return array
    
    if array[0]!=0 and array[1]==array[2]==255:
        array[0] -=1
        return array
    if array[1]!=0 and array[0]==0 and array[2]==255:
        array[1] -=1
        return array
    if array[2]!=0 and array[0]==0 and array[1]==0:
        array[2] -=1
        return array
        
while running:
    clock.tick(FPS)
    for i in pygame.event.get():
        if i.type == pygame.QUIT:
            running = False
    screen.fill(color)
    pygame.display.update()
    color = change_color(color)
pygame.quit()