#include<iostream>
#include<algorithm>
#include<cstdlib>
#include<fstream>
#include<stdio.h>
#include <math.h>
#include "Eigen/Dense"
using namespace std;
using namespace Eigen;
int kn;//����������
float kc[500][10] = { -1 };//�������ļ�
float ktemp[50];//����������ʱֵ
float data0[500][50] = { 0 };//ԭ����
float data1[500][50] = { 0 };//���ֵ������
float data2[50][50] = { 0 };//Э�������
float dataT[50][500] = { 0 };//���ֵ��ת�þ���
float fv[20][500];//����������
float dataeT[20][500];//��ά��ת�þ���
float datae[500][20];//��ά�����
MatrixXf C(50, 50);
MatrixXf vec, val;
int row = 106;
int columns = 10;

void createkc()//���ɾ�������
{
	int temp;
	int num = 0;
	int work = 0;
	for (int i = 0; i < kn; i++)
	{
		temp = rand() % row;//�����������
		work = 1;
		for(int j=0;j<num;j++)//����Ƿ��ظ�
		{
			if (kc[j][2] == temp)//�ظ����������
			{
				i--;
				j = num;
				work = 0;
			}
		}
		if (work == 1)//���ظ�
		{
			num++;
			for (int k = 0; k < 2; k++)//��ȡ������������
			{
				kc[i][k] = datae[temp][k];//������������
			}
			kc[i][2] = temp;//������������
			kc[i][3] = 0;
			datae[temp][2]=i;//���������������
		}
	}
}

int foundmin(int kv)//Ѱ����ĳ������ľ�������
{
	int minkc = 0;
	float dv=10000;//��ֵ
	float temp;
	for (int i = 0; i < kn; i++)
	{
		temp = 0;
		for (int j = 0; j < 2; j++)//����������ľ���
		{
			temp += ((kc[i][j] - datae[kv][j])*(kc[i][j] - datae[kv][j]));
		}
	
		if (temp < dv)//�ж��Ƿ�Ϊ��Сֵ
		{
			dv = temp;
			minkc = i;
		}
		if (dv == 0)//��ȡ����һ����Сֵ�򷵻�
		{
			minkc = i;
			i = kn;
		}
	}
	if (kc[minkc][3] < dv)//�㷨����ǰ��Ҫ��һ����С���룬��˿���������Сֵʱ��¼�������������Զ�ľ���
	{
		kc[minkc][3] = dv;
	}
	return minkc;
}

void kmeans()//����
{
	int work = 1;
	float addnum = 0;
	
	while (work == 1)
	{
		for (int m = 0; m < kn; m++)
		{
			kc[m][3] = 0;
		}
		for (int i = 0; i < row; i++)//�������������۴�
		{
			datae[i][2] = foundmin(i);
		}
		work = 0;//��ǹ���
		for (int i = 0; i < kn; i++)//��kn���������Ľ��м���
		{
			for (int k = 0; k < 2; k++)//��ʼ��
			{
				ktemp[k] = 0;
			}
			addnum = 0;
			for (int j = 0; j < row; j++)//Ѱ�����ڵ�i���������ĵ�����
			{
				if (datae[j][2] == i)//�ж�
				{
					addnum++;//��¼�ж����������ڸþ���
					for (int k = 0; k < 2; k++)//�������������
					{
						ktemp[k] += datae[j][k];
					}
				}
			}
			for (int k = 0; k < 2; k++)//��ø��º�ľ�������
			{
				ktemp[k] /= addnum;
			}
			for (int k = 0; k < 2; k++)//�жϾ����������ޱ仯
			{
				if (ktemp[k] != kc[i][k])
				{
					work = 1;//���������б仯
					k = 2;
				}
			}
			if (work == 1)
			{
				for (int k = 0; k < 2; k++)
				{
					kc[i][k] = ktemp[k];//����������
				}
			}
		}
	}

}

void PCA()//��ά
{
	float value;//���ֵ��
	for (int j = 0; j < columns; j++)
	{
		value = 0;
		for (int i = 0; i < row; i++)//������ֵ
		{
			value += data0[i][j];
		}
		value /= row;//��ֵ
		for (int i = 0; i < row; i++)
		{
			dataT[j][i] = data0[i][j] - value;//ת�þ���
			data1[i][j] = dataT[j][i];//���ֵ������
		}
	}
	for (int i = 0; i < columns; i++)//��Э�������
		for (int j = 0; j < columns; j++)
			for (int k = 0; k < row; k++)
			{
				data2[i][j] += (dataT[i][k] * data1[k][j]/row);
			}
	
}

void computeEig(MatrixXf &C, MatrixXf &vec, MatrixXf &val)
{
	//��������ֵ������������ʹ��selfadjont���ն��������㷨ȥ���㣬�����ò�����vec��val������������
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
	ifstream infile("C:\\Users\\MACHENIKE\\Desktop\\data.txt");//���ļ�
	if (!infile)
	{
		cout << "�ļ����ܴ򿪡�" << endl;
	}
	else
	{
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < columns; j++) {
				infile >> data0[i][j];//д������
			}
		}
	}
	infile.close();//�ر��ļ�
	if (columns > 2)
	{
		PCA();
		//��������ֵ����������
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
				datae[i][j]=data0[i][j];//д������
			}
		}
	}
	//2����������
	kn = 2;
	createkc();//���ɾ�������
	kmeans();//����
	ofstream outfile2;
	string InitialFileName2("C:\\Users\\MACHENIKE\\Desktop\\output2.txt");
	outfile2.open(InitialFileName2, ostream::app);
	cout << "���2���������ļ�����뾶��" << endl;
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
	cout << "����������������ࡣ" << endl;
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
	//3����������
	kn = 3;
	createkc();//���ɾ�������
	kmeans();//����
	ofstream outfile3;
	string InitialFileName3("C:\\Users\\MACHENIKE\\Desktop\\output3.txt");
	outfile3.open(InitialFileName3, ostream::app);
	cout << "���3���������ļ�����뾶��" << endl;
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
	cout << "����������������ࡣ" << endl;
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
	
	//4����������
	kn = 4;
	createkc();//���ɾ�������
	kmeans();//����
	ofstream outfile4;
	string InitialFileName4("C:\\Users\\MACHENIKE\\Desktop\\output4.txt");
	outfile4.open(InitialFileName4, ostream::app);
	cout << "���4���������ļ�����뾶��" << endl;
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
	cout << "����������������ࡣ" << endl;
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
	
	//5����������
	kn = 5;
	createkc();//���ɾ�������
	kmeans();//����
	ofstream outfile5;
	string InitialFileName5("C:\\Users\\MACHENIKE\\Desktop\\output5.txt");
	outfile5.open(InitialFileName5, ostream::app);
	cout << "���5���������ļ�����뾶��" << endl;
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
	cout << "����������������ࡣ" << endl;
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