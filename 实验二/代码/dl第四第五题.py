#第四题：相关矩阵
import pandas as pd
import random
import matplotlib.pyplot as plt
import seaborn
import numpy as np
import math

#阻止省略输出
pd.set_option('display.max_columns', 1000)
pd.set_option('display.width', 1000)
pd.set_option('display.max_colwidth', 1000)
pd.set_option("display.max_columns",None)
pd.set_option("display.max_rows",None)
np.set_printoptions(threshold=np.inf)

# 读取数据
features = ['C1','C2','C3','C4','C5','C6','C7','C8','C9']
dc_listings = pd.read_csv('./data/data3.csv', engine='python')  # 将数据读出
arr_long=len(dc_listings)
constitution=dc_listings["Constitution"].to_list()  # 将体育成绩单取出
dc_listings = dc_listings[features] # 其他成绩放入一起
for i in range(arr_long):   # 统一数据，量化为100
    dc_listings['C6'][i]*=10
    dc_listings['C7'][i] *= 10
    dc_listings['C8'][i] *= 10
    dc_listings['C9'][i] *= 10
dc_listings=dc_listings.values

for x in range(len(constitution)):  # 将体育成绩变为数值数据
    if constitution[x]=="bad":
        constitution[x]=random.randint(0,59)
    elif constitution[x]=="general":
        constitution[x] =random.randint(60,75)
    elif constitution[x] == "good":
        constitution[x] =random.randint(76,85)
    else:
        constitution[x]=random.randint(86,100)

arr=np.c_[dc_listings,constitution]  # 将数据合并
np.round(arr,2) # 取两位小数
def get_average(records):   #平均数
    return sum(records) / len(records)
def get_variance(records):  #方差（此处尝试过使用协方差，发现后续得到数据相关矩阵对角线不为1故改为方差计算）
    average = get_average(records)
    return sum([(x - average) ** 2 for x in records]) / len(records)
def get_standard_deviation(records):    #标准差
    variance = get_variance(records)
    return math.sqrt(variance)
def get_z_score(records):   #规范化
    avg = get_average(records)
    stan = get_standard_deviation(records)
    scores =[round((i-avg)/stan,2) for i in records]
    return scores
def correlation(record1,record2):   #关系计算
    cor=0
    for i in range(len(record1)):
        cor+=(record1[i]*record2[i])
    cor=cor/len(record1)
    return cor  # 得到两个学生之间的关系

arr2=np.zeros((arr_long,10))    # 创建一个数组用于存放得到规范化数据（初始为0）
for i in range(arr_long):
    arr2[i]=get_z_score(arr[i])


relation=np.zeros((arr_long,arr_long))  # 创建关系矩阵，存放各学生关系
for i in range(arr_long):
    for j in range(arr_long):   # 给各点赋值
        if relation[i][j]==0:
            relation[i][j]=correlation(arr2[i],arr2[j]) # 因为两个学生间关系相同，故只需要计算一半即可
            relation[j][i]=relation[i][j]

print(relation)


seaborn.heatmap(relation,annot=False,cmap='Blues') # 第一个参数为数据，annot为设置数据是否显示，cmap为设置颜色
plt.show()

# 第五题：取出每个人最近的三人
out_arr=np.zeros((arr_long,3))  # 建立一个数组用于存放每个学生最近的三个学生

for i in range(arr_long):   # 考虑到时间复杂度，所以使用三次选择排序
    count=3
    xx=[x for x in range(arr_long)] # 存脚标，用于记录学生，便于后面找到学生学号
    for j1 in range(arr_long-1):    # 选择排序
        maxindex = j1;
        if i==maxindex:relation[i][maxindex]=-1 # 此处检验第一个赋值的是不是本身，若是则将数值置为-1
        for j2 in range(j1+1,arr_long):
            if relation[i][j2] > relation[i][maxindex] and i!=j2: maxindex=j2   # 判断是否交换以及是否为对角线
        relation[i][j1],relation[i][maxindex]=relation[i][maxindex],relation[i][j1] # 交换数据
        xx[j1],xx[maxindex]=xx[maxindex],xx[j1]     # 交换脚标顺序
        count-=1
        if count==0:
            break
    for j in range(3):
        out_arr[i][j]=xx[j]
print('得到脚标')
print(out_arr)
dc_listings_2 = pd.read_csv('./data/data3.csv', engine='python')
for i in range(arr_long):   #根据脚标查找数据
    for j in range(3):
        out_arr[i][j]=dc_listings_2['ID'][out_arr[i][j]]
print('得到学号如下')
print(out_arr)
doc=open('data/data5.txt','w')  #写出
print(out_arr,file=doc)
doc.close()