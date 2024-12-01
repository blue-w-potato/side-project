#%%
import pandas as pd
import Formulas
import matplotlib.pyplot as plt

# 數值設定

# Q 的最大值
QMAX = 10000

# Q
Q = [0]
while True:
    Q.append(Q[-1]+0.5)
    if Q[-1] == QMAX:
        break
    
Q = pd.Series(Q)

# TFC
TFC = 360000

# MC
MC = Formulas.MC(Q)

# TVC
TVC = Formulas.TVC(MC, Q)

# TC
TC = Formulas.TC(TVC, TFC)

# AVC
AVC = Formulas.AVC(TVC, Q)

# AFC
AFC = Formulas.AFC(TFC, Q)

# AC
AC = Formulas.AC(TC, Q)

# 繪製圖形

# 介面設定
fig, ax = plt.subplots(2,1)

# TC, TVC, TFC
ax[0].plot(TC[:600], label='TC', color='black')
ax[0].plot(TVC[:600], label='TVC', color='blue')
ax[0].plot([TFC for _ in Q][:600], label='TFC', color='red')
ax[0].legend(loc='upper center', bbox_to_anchor=(0.5, -0.1), ncol=3, fontsize=20)
ax[0].grid(True)

# MC, AC, AVC, AFC
ax[1].plot(AC[320:400], label='AC', color='blue')
ax[1].plot(AVC[320:400], label='AVC', color='black')
ax[1].plot(AFC[320:400], label='AFC', color='red')
ax[1].plot(MC[320:400], label='MC', color='green')
ax[1].legend(loc='upper center', bbox_to_anchor=(0.5, -0.1), ncol=3, fontsize=20)
ax[1].grid(True)

plt.tight_layout()
plt.show()

# %%
