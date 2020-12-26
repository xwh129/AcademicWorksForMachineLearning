import matplotlib.pyplot as plt
import numpy as np
output2= np.loadtxt('output2.txt')#原矩阵
output3= np.loadtxt('output3.txt')#原矩阵
output4= np.loadtxt('output4.txt')#原矩阵
output5= np.loadtxt('output5.txt')#原矩阵
row2=len(output2)
row3=len(output3)
row4=len(output4)
row5=len(output5)

c_list=['blue','red','green','orange','black']
for i in range(2):
    x = output2[i,0]
    y = output2[i,1]
    plt.scatter(x, y, s=40, c=c_list[i], alpha=0.5,label=output2[i][2],marker='*')
for i in range(2,row2):
    x=output2[i,0]
    y=output2[i,1]
    color=int(output2[i,2])-1
    plt.scatter(x, y, s=10, c=c_list[color], alpha=0.5)
plt.legend(loc='best')
plt.grid(True)
plt.show()


for i in range(3):
    x = output3[i,0]
    y = output3[i,1]
    plt.scatter(x, y, s=40, c=c_list[i], alpha=0.5,label=output3[i][2],marker='*')
for i in range(3,row3):
    x=output3[i,0]
    y=output3[i,1]
    color=int(output3[i,2])-1
    plt.scatter(x, y, s=10, c=c_list[color], alpha=0.5)
plt.legend(loc='best')
plt.grid(True)
plt.show()



for i in range(4):
    x = output4[i,0]
    y = output4[i,1]
    plt.scatter(x, y, s=40, c=c_list[i], alpha=0.5,label=output4[i][2],marker='*')
for i in range(4,row4):
    x=output4[i,0]
    y=output4[i,1]
    color=int(output4[i,2])-1
    plt.scatter(x, y, s=10, c=c_list[color], alpha=0.5)
plt.legend(loc='best')
plt.grid(True)
plt.show()


for i in range(5):
    x = output5[i,0]
    y = output5[i,1]
    plt.scatter(x, y, s=40, c=c_list[i], alpha=0.5,label=output5[i][2],marker='*')
for i in range(5,row5):
    x=output5[i,0]
    y=output5[i,1]
    color=int(output5[i,2])-1
    plt.scatter(x, y, s=10, c=c_list[color], alpha=0.5)
plt.legend(loc='best')
plt.grid(True)
plt.show()
