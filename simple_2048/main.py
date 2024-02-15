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
class PICTURES:
    null = pygame.image.load(os.path.join('numbers', 'null.png')).convert()
    num_2 = pygame.image.load(os.path.join('numbers', '2.png')).convert()
    num_4 = pygame.image.load(os.path.join('numbers', '4.png')).convert()
    num_8 = pygame.image.load(os.path.join('numbers', '8.png')).convert()
    num_16 = pygame.image.load(os.path.join('numbers', '16.png')).convert()
    num_32 = pygame.image.load(os.path.join('numbers', '32.png')).convert()
    num_64 = pygame.image.load(os.path.join('numbers', '64.png')).convert()
    num_128 = pygame.image.load(os.path.join('numbers', '128.png')).convert()

# 角色圖片設定
class Player(pygame.sprite.Sprite): 
    def __init__(self, size, color, xy):
        pygame.sprite.Sprite.__init__(self) 
        self.image = pygame.Surface(size) 
        self.image.fill(color) 
        self.rect = self.image.get_rect()
        self.x = screen_sets.width/2
        self.y = screen_sets.height/2
        self.rect.center = [self.x, self.y]
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

running = True

while running:
    clock.tick(screen_sets.fps) 
    #取得輸入
    for event in pygame.event.get(): 
        if event.type == pygame.QUIT:
            running = False

    #更新遊戲
    
    #畫面顯示
    screen.fill(screen_sets.back_color) 
    all_sprites.draw(screen) 
    pygame.display.update()

pygame.quit() 