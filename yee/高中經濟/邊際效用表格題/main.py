n, m = map(int, input().split())
stuffs = []
all_mu = []
all_p = []

for _ in range(n):
    Input = input().split()
    x = Input[0]
    p = int(Input[1])
    
    stuffs.append(x)
    all_mu.append([int(mu) for mu in Input[2:]])
    all_p.append(p)

MUm, MaxM = map(int, input().split())

pos = [0 for i in range(n)]


quantity = [0 for i in range(n)]

while 1:
    mus = [ all_mu[i][pos[i]] for i in range(n) ]
    Mmu = max(mus)
    for i in range(n):
        if mus[i] == Mmu:
            if MaxM < all_p[i]:
                continue
            quantity[i].append(1)
            MaxM -= all_p[i]
            if MaxM == 0:
                break
    if MaxM == 0:
        break

for i in range(n):
    print(f'{stuffs[i]} : {quantity[i]}')

print(f'剩下 {MaxM} 個')