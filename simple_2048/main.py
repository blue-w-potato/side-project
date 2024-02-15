import pygame
import os

#視窗相關設定
class screen_set():
    def __init__(self):
        self.width = 1000
        self.height = 1000
        self.size = [self.width, self.height]
        self.name = '2048' 
        self.back_color = [195, 195, 195]
        self.fps = 1000

screen_sets = screen_set() 

#初始化
pygame.init()

screen = pygame.display.set_mode(screen_sets.size) 
pygame.display.set_caption(screen_sets.name) 
clock = pygame.time.Clock()

#載入圖片

LOAD_PICTURES = (
    pygame.image.load(os.path.join('numbers', 'null.png')).convert(), 
    pygame.image.load(os.path.join('numbers', '2.png')).convert(),
    pygame.image.load(os.path.join('numbers', '4.png')).convert(),
    pygame.image.load(os.path.join('numbers', '8.png')).convert(),
    pygame.image.load(os.path.join('numbers', '16.png')).convert(),
    pygame.image.load(os.path.join('numbers', '32.png')).convert(),
    pygame.image.load(os.path.join('numbers', '64.png')).convert(),
    pygame.image.load(os.path.join('numbers', '128.png')).convert(),
            )

class PICTURE:
    def __init__(self, key, value, next) -> None:
        self.key = key
        self.value = value
        self.next = next

pictures = [ 'null', 2, 4, 8, 16, 32, 64, 128 ]
pictures[0] = PICTURE( None, LOAD_PICTURES[0], None )
for i in range( 1, len(LOAD_PICTURES) ):
    pictures[i] = PICTURE( pictures[i-1], LOAD_PICTURES[i], None )
    pictures[i-1].next = pictures[i]


# 角色圖片設定
class Player(pygame.sprite.Sprite): 
    def __init__(self, size, color, xy):
        pygame.sprite.Sprite.__init__(self) 
        self.image = pictures[0] 
        self.x = screen_sets.width/2
        self.y = screen_sets.height/2

all_sprites = pygame.sprite.Group()

#角色一相關設定
class Player_1_sets():
    def __init__(self):
        self.width = 40
        self.height = 40
        self.size = [self.width, self.height]
        self.color = [0, 0, 0]
        self.xy = [screen_sets.height-1, 0]

player_1_sets = Player_1_sets() 
player_1 = Player(size = player_1_sets.size, color = player_1_sets.color,  xy = player_1_sets.xy)
all_sprites.add(player_1) 

#遊戲主迴圈
p = pygame.image.load(os.path.join('numbers', '2.png')).convert()
running = True
k = 0

while running:
    clock.tick(screen_sets.fps) 
    #取得輸入
    for event in pygame.event.get(): 
        if event.type == pygame.QUIT:
            running = False

    #更新遊戲
    if k < len(pictures)-1:
        k += 1
    else:
        k = 0

    #畫面顯示
    screen.blit(p , ( 0, 0 ))
    screen.fill(screen_sets.back_color)
    all_sprites.update() 
    pygame.display.update()

pygame.quit() 