#第三题：z-score归一化矩阵
import pandas as pd
import random
import numpy as np
import math

#取出输出省略
pd.set_option('display.max_columns', 1000)
pd.set_option('display.width', 1000)
pd.set_option('display.max_colwidth', 1000)
pd.set_option("display.max_columns",None)
pd.set_option("display.max_rows",None)
np.set_printoptions(threshold=np.inf)

# 读取数据
features = ['C1','C2','C3','C4','C5','C6','C7','C8','C9']
dc_listings = pd.read_csv('./data/data3.csv', engine='python')
constitution=dc_listings["Constitution"].to_list()
dc_listings = dc_listings[features]

for x in range(len(constitution)):  #将体育成绩数据量化
    if constitution[x]=="bad":
        constitution[x]=random.randint(0,59)
    elif constitution[x]=="general":
        constitution[x] =random.randint(60,75)
    elif constitution[x] == "good":
        constitution[x] =random.randint(76,85)
    else:
        constitution[x]=random.randint(86,100)

def get_average(records):   # 计算平均值
    return sum(records) / len(records)
def get_variance(records):   # 计算协方差
    average = get_average(records)
    return sum([(x - average) ** 2 for x in records]) / (len(records)-1)
def get_standard_deviation(records):    #计算 标准差
    variance = get_variance(records)
    return math.sqrt(variance)
def get_z_score(records):   #计算z-score
    avg = get_average(records)
    stan = get_standard_deviation(records)
    scores = [(i-avg)/stan for i in records]
    return scores

for i in features:
    dc_listings[i]=get_z_score(dc_listings[i])
constitution=get_z_score(constitution)

#print(dc_listings)

dc_listings=dc_listings.values  # 将值取出
#print(dc_listings)


arr=np.c_[dc_listings,constitution] # 合并其他数据与体育成绩
print(arr)


print(arr.shape)    # 输出矩阵的行和列信息
doc=open('data/data4.txt','w')  # 写入文件
print(arr,file=doc)
