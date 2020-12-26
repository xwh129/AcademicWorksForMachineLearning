
#第一题：散点图（在jupyter notebook中运行，具体见.ipynb文件）
import pandas as pd
import random

df=pd.read_csv('./data/data3.csv')  #读入数据
df.info()   #各列信息

#将数据按照C1进行升序
df.sort_values(by="C1",inplace=True)
df.head()
C1=df["C1"].to_list()   #将C1和体育成绩提出
constitution=df["Constitution"].to_list()
for x in range(len(constitution)):  # 此处将体育成绩量化
    if constitution[x]=="bad":
        constitution[x]=random.randint(0,59)
    elif constitution[x]=="general":
        constitution[x] =random.randint(60,75)
    elif constitution[x] == "good":
        constitution[x] =random.randint(76,85)
    else:
        constitution[x]=random.randint(86,100)


#引入散点图
import pyecharts.options as opts    # 此处使用的jupyter notebook实现散点图可视化
from pyecharts.charts import Scatter

scatter=(
    Scatter()
    .add_xaxis( # x坐标设置为C1成绩
        xaxis_data=C1
    )
    .add_yaxis(
        series_name="", # 设置为空
        y_axis=constitution,    # y轴设置为体育成绩
        symbol_size=4,  # 散点图原点大小
        label_opts=opts.LabelOpts(is_show=False)    # 不显示数字
    )
    .set_global_opts(
        xaxis_opts=opts.AxisOpts(type_="value"),    # 将x，y轴的值做为数字处理
        yaxis_opts=opts.AxisOpts(type_="value"),
        title_opts=opts.TitleOpts(title="C1-体育图",pos_left="center")     #标题设置，位置放于中间
    )
)
scatter.render_notebook()