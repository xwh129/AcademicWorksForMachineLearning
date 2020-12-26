#第二题：直方图m
import pandas as pd
df=pd.read_csv('./data/data3.csv')

#去除输出省略
pd.set_option('display.max_columns', 1000)
pd.set_option('display.width', 1000)
pd.set_option('display.max_colwidth', 1000)
pd.set_option("display.max_columns",None)
pd.set_option("display.max_rows",None)

print(df)

import matplotlib.pyplot as plt
plt.figure(figsize=(12,5))  # 创建一个图，12是宽度，5是高度
plt.rcParams['font.family'] = 'SimHei'  # 设置中文字体
plt.title("C1直方图")  # 标题
bins=[60,65,70,75,80,85,90,95,100]  # 设置x轴数值，五为间隔
plt.hist(df["C1"],bins=bins,histtype='bar',rwidth=0.8)  # 第一个参数为数据，bins间隔下标，histtype为直方图样式，rwidth为每一条宽度
plt.grid(True)  # 设置网格线
plt.show()