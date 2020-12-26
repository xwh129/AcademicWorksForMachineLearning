#include<iostream>
#include<algorithm>
#include<cstdlib>
#include<fstream>
#include<stdio.h>
#include <math.h>
#include "Eigen/Dense"
using namespace std;
using namespace Eigen;
int kn;//聚类中心数
float kc[500][10] = { -1 };//聚类中心集
float ktemp[50];//聚类中心临时值
float data0[500][50] = { 0 };//原矩阵
float data1[500][50] = { 0 };//零均值化矩阵
float data2[50][50] = { 0 };//协方差矩阵
float dataT[50][500] = { 0 };//零均值化转置矩阵
float fv[20][500];//特征向量集
float dataeT[20][500];//降维后转置矩阵
float datae[500][20];//降维后矩阵
MatrixXf C(50, 50);
MatrixXf vec, val;
int row = 106;
int columns = 10;

void createkc()//生成聚类中心
{
	int temp;
	int num = 0;
	int work = 0;
	for (int i = 0; i < kn; i++)
	{
		temp = rand() % row;//随机生成数字
		work = 1;
		for(int j=0;j<num;j++)//检查是否重复
		{
			if (kc[j][2] == temp)//重复则重新随机
			{
				i--;
				j = num;
				work = 0;
			}
		}
		if (work == 1)//无重复
		{
			num++;
			for (int k = 0; k < 2; k++)//获取聚类中心数据
			{
				kc[i][k] = datae[temp][k];//拷贝样本数据
			}
			kc[i][2] = temp;//声明聚类中心
			kc[i][3] = 0;
			datae[temp][2]=i;//标记样本所属聚类
		}
	}
}

int foundmin(int kv)//寻找离某项最近的聚类中心
{
	int minkc = 0;
	float dv=10000;//差值
	float temp;
	for (int i = 0; i < kn; i++)
	{
		temp = 0;
		for (int j = 0; j < 2; j++)//计算距离中心距离
		{
			temp += ((kc[i][j] - datae[kv][j])*(kc[i][j] - datae[kv][j]));
		}
	
		if (temp < dv)//判断是否为最小值
		{
			dv = temp;
			minkc = i;
		}
		if (dv == 0)//获取到第一个最小值则返回
		{
			minkc = i;
			i = kn;
		}
	}
	if (kc[minkc][3] < dv)//算法结束前还要求一次最小距离，因此可以在求最小值时记录下离聚类中心最远的距离
	{
		kc[minkc][3] = dv;
	}
	return minkc;
}

void kmeans()//聚类
{
	int work = 1;
	float addnum = 0;
	
	while (work == 1)
	{
		for (int m = 0; m < kn; m++)
		{
			kc[m][3] = 0;
		}
		for (int i = 0; i < row; i++)//更新数据所属聚簇
		{
			datae[i][2] = foundmin(i);
		}
		work = 0;//标记归零
		for (int i = 0; i < kn; i++)//对kn个聚类中心进行计算
		{
			for (int k = 0; k < 2; k++)//初始化
			{
				ktemp[k] = 0;
			}
			addnum = 0;
			for (int j = 0; j < row; j++)//寻找属于第i个聚类中心的数据
			{
				if (datae[j][2] == i)//判断
				{
					addnum++;//记录有多少样本属于该聚类
					for (int k = 0; k < 2; k++)//加入该样本数据
					{
						ktemp[k] += datae[j][k];
					}
				}
			}
			for (int k = 0; k < 2; k++)//获得更新后的聚类中心
			{
				ktemp[k] /= addnum;
			}
			for (int k = 0; k < 2; k++)//判断聚类中心有无变化
			{
				if (ktemp[k] != kc[i][k])
				{
					work = 1;//聚类中心有变化
					k = 2;
				}
			}
			if (work == 1)
			{
				for (int k = 0; k < 2; k++)
				{
					kc[i][k] = ktemp[k];//更新类中心
				}
			}
		}
	}

}

void PCA()//降维
{
	float value;//零均值化
	for (int j = 0; j < columns; j++)
	{
		value = 0;
		for (int i = 0; i < row; i++)//求列总值
		{
			value += data0[i][j];
		}
		value /= row;//均值
		for (int i = 0; i < row; i++)
		{
			dataT[j][i] = data0[i][j] - value;//转置矩阵
			data1[i][j] = dataT[j][i];//零均值化矩阵
		}
	}
	for (int i = 0; i < columns; i++)//求协方差矩阵
		for (int j = 0; j < columns; j++)
			for (int k = 0; k < row; k++)
			{
				data2[i][j] += (dataT[i][k] * data1[k][j]/row);
			}
	
}

void computeEig(MatrixXf &C, MatrixXf &vec, MatrixXf &val)
{
	//计算特征值和特征向量，使用selfadjont按照对阵矩阵的算法去计算，可以让产生的vec和val按照有序排列
	int a1 = 0, a2 = 0;
	EigenSolver<MatrixXf> es(C);
	vec = es.pseudoEigenvalueMatrix();
	val = es.pseudoEigenvectors();
	for (int i = 1; i < columns; i++)
	{
		if (vec(i, i) < vec(a1, a1))
		{
			a1 = i;
		}
	}
	if (a1 == 0)
		a2++;
	for (int i = 1; i < columns; i++)
	{
		if ((vec(i, i) < vec(a2, a2))&&a1!=a2)
		{
			a2 = i;
		}
	}
	for (int i = 0; i < columns; i++)
	{
		fv[a1][i] = val(a1, i);
		fv[a2][i] = val(a2, i);
	}
	for (int i = 0; i < 2; i++)
		for (int j = 0; j < row; j++)
			for (int k = 0; k < columns; k++)
			{
				dataeT[i][j] += (fv[i][k] * dataT[k][j]);
			}
	for (int i = 0; i < 2; i++)
	{
		for (int j = 0; j < row; j++)
			datae[j][i] = dataT[i][j];
	}
}

int main()
{
	ifstream infile("C:\\Users\\MACHENIKE\\Desktop\\data.txt");//打开文件
	if (!infile)
	{
		cout << "文件不能打开。" << endl;
	}
	else
	{
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < columns; j++) {
				infile >> data0[i][j];//写入数据
			}
		}
	}
	infile.close();//关闭文件
	if (columns > 2)
	{
		PCA();
		//计算特征值和特征向量
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < columns; j++) {
				C(i, j) = data2[i][j];
			}
		}
		computeEig(C, vec, val);
	}
	else
	{
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < columns; j++) {
				datae[i][j]=data0[i][j];//写入数据
			}
		}
	}
	//2个聚类中心
	kn = 2;
	createkc();//生成聚类中心
	kmeans();//聚类
	ofstream outfile2;
	string InitialFileName2("C:\\Users\\MACHENIKE\\Desktop\\output2.txt");
	outfile2.open(InitialFileName2, ostream::app);
	cout << "输出2个聚类中心及其类半径。" << endl;
	for (int i = 0; i < kn; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (j == 2)
			{
				outfile2 << sqrt(kc[i][3]) << endl;
			}
			else
			{
				outfile2 << kc[i][j] << "  ";
			}
		}
	}
	cout << "输出样本及其所属类。" << endl;
	for (int i = 0; i < row; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (j == 2)
			{
				outfile2 << datae[i][j] + 1 << endl;
			}
			else
			{
				outfile2 << datae[i][j] << "  ";
			}
		}
	}
	outfile2.close();
	//3个聚类中心
	kn = 3;
	createkc();//生成聚类中心
	kmeans();//聚类
	ofstream outfile3;
	string InitialFileName3("C:\\Users\\MACHENIKE\\Desktop\\output3.txt");
	outfile3.open(InitialFileName3, ostream::app);
	cout << "输出3个聚类中心及其类半径。" << endl;
	for (int i = 0; i < kn; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (j == 2)
			{
				outfile3 << sqrt(kc[i][3]) << endl;
			}
			else
			{
				outfile3 << kc[i][j] << "  ";
			}
		}
	}
	cout << "输出样本及其所属类。" << endl;
	for (int i = 0; i < row; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (j == 2)
			{
				outfile3 << datae[i][j] + 1 << endl;
			}
			else
			{
				outfile3 << datae[i][j] << "  ";
			}
		}
	}
	outfile3.close();
	
	//4个聚类中心
	kn = 4;
	createkc();//生成聚类中心
	kmeans();//聚类
	ofstream outfile4;
	string InitialFileName4("C:\\Users\\MACHENIKE\\Desktop\\output4.txt");
	outfile4.open(InitialFileName4, ostream::app);
	cout << "输出4个聚类中心及其类半径。" << endl;
	for (int i = 0; i < kn; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (j == 2)
			{
				outfile4 << sqrt(kc[i][3]) << endl;
			}
			else
			{
				outfile4 << kc[i][j] << "  ";
			}
		}
	}
	cout << "输出样本及其所属类。" << endl;
	for (int i = 0; i < row; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (j == 2)
			{
				outfile4 << datae[i][j] + 1 << endl;
			}
			else
			{
				outfile4 << datae[i][j] << "  ";
			}
		}
	}
	outfile4.close();
	
	//5个聚类中心
	kn = 5;
	createkc();//生成聚类中心
	kmeans();//聚类
	ofstream outfile5;
	string InitialFileName5("C:\\Users\\MACHENIKE\\Desktop\\output5.txt");
	outfile5.open(InitialFileName5, ostream::app);
	cout << "输出5个聚类中心及其类半径。" << endl;
	for (int i = 0; i < kn; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (j == 2)
			{
				outfile5 << sqrt(kc[i][3]) << endl;
			}
			else
			{
				outfile5 << kc[i][j] << "  ";
			}
		}
	}
	cout << "输出样本及其所属类。" << endl;
	for (int i = 0; i < row; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (j == 2)
			{
				outfile5 << datae[i][j] + 1 << endl;
			}
			else
			{
				outfile5 << datae[i][j] << "  ";
			}
		}
	}
	outfile5.close();
	return 0;
}