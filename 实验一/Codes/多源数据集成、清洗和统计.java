
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class test {
    public static int stunum=0;
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

        //连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        String URL="jdbc:mysql://localhost:3306/dl_1?characterEncoding=utf8&serverTimezone=GMT%2B8";
        Connection con = DriverManager.getConnection(URL, "root", "root");
        String sqladd="insert into stu(ID,Name,City,Gender,Height,C1,C2,C3,C4,C5,C6,C7,C8,C9,C10,Constitution) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps=con.prepareStatement(sqladd);

        //扫描file,取得其第一行记为属性
        File file = new File("C:\\Users\\xwh129\\Desktop\\student.txt");
        Scanner scanner = new Scanner(file).useDelimiter("\\n");
        Scanner properties_scanner=new Scanner(scanner.next()).useDelimiter(",");
        String[] properties=new String[16];//共有16列数据
        for(int i=0;i<16;i++) {
            properties[i]=properties_scanner.next();
            //System.out.println(properties[i]);
        }
        //注意，属性的最后一个字符为换行符，这里要把它规范化
        properties[15]=properties[15].substring(0,properties[15].length()-1);
        //buffer一开始用于存储txt文件内容
        String[][] buffer=new String[16][200];
        //int mark=0;
        for(int j=0;scanner.hasNext();/*mark=*/j++) {
            Scanner temp=new Scanner(scanner.next()).useDelimiter(",");
            for(int k=0;temp.hasNext();k++) {
                buffer[k][j]=temp.next();
                //System.out.println(buffer[k][j]);
            }
            stunum++;
        }

        //规范buffer里的id
        for(int index=0;index<stunum;index++){
            buffer[propertiesIndex("ID",properties)][index]=
                    Integer.toString(Integer.parseInt(buffer[propertiesIndex("ID",properties)][index])
                            -202000);
        }

        //把buffer整合进数据库里
        for(int index=0;index<stunum;index++){
            for(int i=0;i<16;i++){
                ps.setString(i+1,buffer[i][index]);
            }
            ps.executeUpdate();
        }

        //执行sql并取得结果集
        Statement statement=con.createStatement();


        //对整个数据库表使用sql语句规范姓名（去空格）性别和身高
        {
            statement.execute("UPDATE stu SET NAME=REPLACE(NAME,' ','');");
            statement.execute("update stu set Gender='male' where Gender='boy';");
            statement.execute("update stu set Gender='female' where Gender='girl';");
            statement.execute("update stu set Height=Height*100 where Height<100 AND Height IS NOT NULL;");
        }

        //漫长的填补空值过程（以名字为索引）
        {
            statement.execute("UPDATE stu a,stu b SET a.ID=b.ID WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.ID='' AND b.ID!='';");
            statement.execute("UPDATE stu a,stu b SET a.City=b.City WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.City='' AND b.City!='';");
            statement.execute("UPDATE stu a,stu b SET a.Gender=b.Gender WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.Gender='' AND b.Gender!='';");
            statement.execute("UPDATE stu a,stu b SET a.Height=b.Height WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.Height=0 AND b.Height !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C1=b.C1 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C1=0 AND b.C1 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C2=b.C2 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C2=0 AND b.C2 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C3=b.C3 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C3=0 AND b.C3 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C4=b.C4 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C4=0 AND b.C4 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C5=b.C5 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C5=0 AND b.C5 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C6=b.C6 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C6=0 AND b.C6 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C7=b.C7 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C7=0 AND b.C7 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C8=b.C8 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C8=0 AND b.C8 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C9=b.C9 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C9=0 AND b.C9 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.C10=b.C10 WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.C10=0 AND b.C10 !=0;");
            statement.execute("UPDATE stu a,stu b SET a.Constitution=b.Constitution WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.Constitution='' AND b.Constitution!='';");
        }
        //模糊查重并去重
        //注意：
        //尚不知道ID和姓名相同，其他项有所不同的情况下是否需要更变其中一项的ID，也即不清楚是否应该将它们视为不同数据项处理
        //这里假设他们是同一人
        {
            //删掉名字不规范的数据项
            statement.execute("DELETE FROM a USING stu a,stu b WHERE a.Name LIKE CONCAT('%',b.Name,'%') AND a.Name!=b.Name;");
            //把名字、城市完全相同但ID不一样的项更变为同一ID
            statement.execute("UPDATE stu a,stu b SET a.ID=b.ID WHERE (a.Name=b.Name AND a.City=b.City  );");
            //解决同ID不同人问题-------未解决，因为不知道重叠的两人或多人真正对应的ID号，可行的解决方法有：
            //1.把其中一人ID设置为 对应ID号_1 加以区分
            //2.把其中一人ID号设置为 表中当前最大ID号+1
            //3.把其中一人ID号设置为 离其ID号最近的无人使用的ID号（比较麻烦，需要多次遍历数据表）
            //4.接3：如果没有离其最近的无人使用的ID号，则取其中一人的ID号自增1，其后数据项ID号顺延（全部自增1）（相当麻烦且耗费更多资源）
            //5.当无事发生，并不影响数据统计<---当前策略
            //
        }

        //舍弃原来的buffer，把数据库数据读出到新的buffer中
        //注意，distinct关键字在jdbc中不起作用,尚不清楚什么原因，这里用分组查询代替
        buffer=new String[16][200];
        //String sqlselect="SELECT * FROM stu GROUP BY ID,NAME,City,Gender,Height,C1,C2,C3,C4,C5,C6,C7,C8,C9,C10,Constitution;";
        //如果把同ID同名同城的数据项视为同一个数据，可以直接使用以下sql
        String sqlselect="SELECT * FROM stu GROUP BY ID,NAME,City;";
        ResultSet rs=statement.executeQuery(sqlselect);
        stunum=0;
        for(;rs.next();) {
            int index=findIndex();
            for (int i = 0; i < 16; i++) {
                buffer[i][index] = rs.getString(i + 1);
            }
        }

        //将buffer的内容转存入一个txt文件中
        {
            String filePath="C:\\Users\\xwh129\\Desktop\\student_finished.txt";
            BufferedWriter out=new BufferedWriter(new FileWriter(filePath));
            for(int i=0;i<16;i++){
                out.write(properties[i]);
                if(i!=15)
                    out.write(",");
            }
            out.write("\n");
            for(int index=0;index<stunum;index++){
                for(int j=0;j<16;j++){
                    out.write(buffer[j][index]);
                    if(j!=15)
                        out.write(",");
                }
                //out.newLine();
                out.write("\n");
            }
            out.close();
        }

        //下面开始做题
        //使用sql语句可以轻松解决以下问题，但这里还是选择使用java编写计算过程
        //1.学生中家乡在Beijing的所有课程的平均成绩。
        {
            //共11门课成绩，C1~C10以及Constitution
            //下面是空缺值视为0的情况
            double[] sum=new double[11];
            int temp=0;
            for(int index=0;index<stunum;index++){
                if(buffer[propertiesIndex("City",properties)][index].equals("Beijing")){
                    for(int j=0;j<11;j++){
                        if(j<10) {
                            if (buffer[propertiesIndex("C" + (j+1), properties)][index].equals("")) ;
                            else
                                sum[j] += Double.parseDouble(buffer[propertiesIndex("C" + (j+1), properties)][index]);
                        }
                        else
                        {
                            switch(buffer[propertiesIndex("Constitution",properties)][index]) {
                                case "bad":
                                    sum[j] += 25;
                                    break;
                                case "general":
                                    sum[j] += 50;
                                    break;
                                case "good":
                                    sum[j] += 75;
                                    break;
                                case "excellent":
                                    sum[j] += 100;
                            }
                        }
                    }
                    temp++;
                }
            }
            //下面是不计空缺值的情况
//            double[] sum=new double[11];
//            int[] temp=new int[11];
//            for(int index=0;index<stunum;index++){
//                if(buffer[propertiesIndex("City",properties)][index].equals("Beijing")){
//                    for(int j=0;j<11;j++){
//                        if(j<10) {
//                            if(buffer[!propertiesIndex("C" + (j+1), properties)][index].equals("")) {
//                                sum[j] += Double.parseDouble(buffer[propertiesIndex("C" + (j+1), properties)][index]);
//                                temp[j]++;
//                            }
//                        }
//                        else
//                        {
//                            switch(buffer[propertiesIndex("Constitution",properties)][index]){
//                                case "bad":sum[j]+=25;break;
//                                case "general":sum[j]+=50;break;
//                                case "good":sum[j]+=75;break;
//                                case "excellent": sum[j]+=100;
//                            }
//                            if(buffer[!propertiesIndex("Constitution", properties)][index].equals(""))
//                                temp[j]++;
//                        }
//                    }
//
//                }
//            }
            //空缺值为0时的输出
            System.out.println("第一题：求学生中家乡在Beijing的所有课程的平均成绩");
            System.out.println("下面输出来自北京的同学的各课程成绩，体能成绩按评价量化为百分制");
            for(int j=0;j<11;j++) {
                if(j<10)
                    System.out.println("C"+(j+1)+"课程的平均成绩为"+sum[j]/temp);
                else
                    System.out.println("体育课课程平均值为"+sum[j]/temp);
            }
            //空缺值不计时的输出
//            System.out.println("下面输出来自北京的同学的各课程成绩，体能成绩取1~4");
//            for(int j=0;j<11;j++) {
//                if(j<10)
//                    System.out.println("C"+(j+1)+"课程的平均成绩为"+sum[j]/temp[j]);
//                else
//                    System.out.println("体育课课程平均值为"+sum[j]/temp[j]);
//            }
        }
        System.out.println("-------------------");
        //2.学生中家乡在广州，课程1在80分以上，且课程10在9分以上的男同学的数量
        //然而课程10在表中没有意义，这里设为计算课程9
        //C10 refers to NULL here, so we calculate C9 instead.
        {
            int count=0;
            for(int index=0;index<stunum;index++) {
                if (buffer[propertiesIndex("City", properties)][index].equals("Guangzhou") &&
                        buffer[propertiesIndex("C1", properties)][index].equals("")&&
                        Double.parseDouble(buffer[propertiesIndex("C1", properties)][index]) >= 80 &&
                        Double.parseDouble(buffer[propertiesIndex("C9", properties)][index]) >= 9 &&
                        buffer[propertiesIndex("Gender", properties)][index].equals("male"))
                    count++;
            }
            System.out.println("第二题：求学生中家乡在广州，课程1在80分以上，且课程10在9分以上的男同学的数量");
            System.out.println("学生中家乡在广州，课程1在80分以上，且课程9在9分以上的男同学的数量为"+count+"人");
        }
        System.out.println("-------------------");
        //3.比较广州和上海两地女生的平均体能测试成绩，哪个地区的更强些？
        //下面的代码块可以定义函数以缩短代码规模，这里是直接编写
        //Though the codes beneath could be done by creating a function, we code them directly instead.
        {
            double totalScore_Guangzhou=0;
            int count_Guangzhou=0;
            double totalScore_Shanghai=0;
            int count_Shanghai=0;
            double temp;
            //下面是空缺值视为0的情况
            for(int index=0;index<stunum;index++){
                if(buffer[propertiesIndex("City", properties)][index].equals("Guangzhou")&&
                   buffer[propertiesIndex("Gender", properties)][index].equals("female")) {
                    switch (buffer[propertiesIndex("Constitution", properties)][index]) {
                        case "bad":
                            totalScore_Guangzhou += 1;
                            break;
                        case "general":
                            totalScore_Guangzhou += 2;
                            break;
                        case "good":
                            totalScore_Guangzhou += 3;
                            break;
                        case "excellent":
                            totalScore_Guangzhou += 4;
                        default:
                            totalScore_Guangzhou += 0;
                    }
                    count_Guangzhou+=1;
                }
                else if(buffer[propertiesIndex("City", properties)][index].equals("Shanghai")&&
                        buffer[propertiesIndex("Gender", properties)][index].equals("female")) {
                    switch (buffer[propertiesIndex("Constitution", properties)][index]) {
                        case "bad":
                            totalScore_Shanghai += 1;
                            break;
                        case "general":
                            totalScore_Shanghai += 2;
                            break;
                        case "good":
                            totalScore_Shanghai += 3;
                            break;
                        case "excellent":
                            totalScore_Shanghai += 4;
                        default:
                            totalScore_Shanghai += 0;
                    }
                    count_Shanghai+=1;
                }
            }
            //下面是空缺值不计的情况
//            for(int index=0;index<stunum;index++){
//                if(buffer[propertiesIndex("City", properties)][index].equals("Guangzhou")&&
//                        buffer[propertiesIndex("Gender", properties)][index].equals("female")&&
//                        !buffer[propertiesIndex("Constitution", properties)][index].equals("")) {
//                    switch (buffer[propertiesIndex("Constitution", properties)][index]) {
//                        case "bad":
//                            totalScore_Guangzhou += 1;
//                            break;
//                        case "general":
//                            totalScore_Guangzhou += 2;
//                            break;
//                        case "good":
//                            totalScore_Guangzhou += 3;
//                            break;
//                        case "excellent":
//                            totalScore_Guangzhou += 4;
//                    }
//                    count_Guangzhou+=1;
//                }
//                else if(buffer[propertiesIndex("City", properties)][index].equals("Shanghai")&&
//                        buffer[propertiesIndex("Gender", properties)][index].equals("female")&&
//                        !buffer[propertiesIndex("Constitution", properties)][index].equals("")) {
//                    switch (buffer[propertiesIndex("Constitution", properties)][index]) {
//                        case "bad":
//                            totalScore_Shanghai += 1;
//                            break;
//                        case "general":
//                            totalScore_Shanghai += 2;
//                            break;
//                        case "good":
//                            totalScore_Shanghai += 3;
//                            break;
//                        case "excellent":
//                            totalScore_Shanghai += 4;
//                    }
//                    count_Shanghai+=1;
//                }
//            }
            System.out.println("第三题：比较广州和上海两地女生的平均体能测试成绩，哪个地区的更强些？");
            System.out.println("假设体能成绩取1~4");
            System.out.println("广州女生的平均体能成绩为"+totalScore_Guangzhou/count_Guangzhou);
            System.out.println("上海女生的平均体能成绩为"+totalScore_Shanghai/count_Shanghai);
            String result=(totalScore_Guangzhou/count_Guangzhou>totalScore_Shanghai/count_Shanghai)?"广州":"上海";
            System.out.println(result+"女生的平均体能更强一些");
        }
        System.out.println("-------------------");
        //4.学习成绩和体能测试成绩，两者的相关性是多少？
        //In order to feed the needs, we're gonna standardise the data at first.
        //为方便计算，将所有空值直接视为0
        {
            double[] sum=new double[11];
            double[] sum_square=new double[11];
            double[] sum_XY=new double[10];
            double[] avg=new double[11];
            double[] avg_square=new double[11];
            double[] avg_XY=new double[10];
            for(int index=0;index<stunum;index++){
                for(int j=propertiesIndex("C1",properties);j<=propertiesIndex("C10",properties);j++){
                    if(buffer[j][index].equals(""))
                        buffer[j][index]="0";
                    //这一步将所有十分制成绩规范化为百分制，做法是直接乘10
                    if(Double.parseDouble(buffer[j][index])<10)
                        buffer[j][index]=Double.toString(Double.parseDouble(buffer[j][index])*10);
                    sum[j-propertiesIndex("C1",properties)]+=Double.parseDouble(buffer[j][index]);
                    sum_square[j-propertiesIndex("C1",properties)]+=Math.pow(Double.parseDouble(buffer[j][index]),2);
                }
                //这里为了规范数据为百分制，根据体能成绩的从坏到好重新赋以数值方便计算
                switch(buffer[propertiesIndex("Constitution",properties)][index]) {
                    case "bad":
                        buffer[propertiesIndex("Constitution", properties)][index] = "25";
                        break;
                    case "general":
                        buffer[propertiesIndex("Constitution", properties)][index] = "50";
                        break;
                    case "good":
                        buffer[propertiesIndex("Constitution", properties)][index] = "75";
                        break;
                    case "excellent":
                        buffer[propertiesIndex("Constitution", properties)][index] = "100";
                    default:
                        buffer[propertiesIndex("Constitution", properties)][index] = "0";
                }
                sum[propertiesIndex("Constitution",properties)-propertiesIndex("C1",properties)]+=
                        Double.parseDouble(buffer[propertiesIndex("Constitution", properties)][index]);
                sum_square[propertiesIndex("Constitution",properties)-propertiesIndex("C1",properties)]+=
                        Math.pow(Double.parseDouble(buffer[propertiesIndex("Constitution", properties)][index]),2);
                for(int j=0;j<10;j++)
                    sum_XY[j]+=Double.parseDouble(buffer[propertiesIndex("Constitution", properties)][index])*
                            Double.parseDouble(buffer[propertiesIndex("C"+(j+1), properties)][index]);
            }
            for(int i=0;i<11;i++) {
                avg[i] = sum[i] / stunum;
                avg_square[i]=sum_square[i]/stunum;
            }
            for(int i=0;i<10;i++)
                avg_XY[i]=sum_XY[i]/stunum;
            //皮尔逊相关系数计算，x、y的相关性=(E(xy)-E(x)E(y))/(根号下(E(x^2)-E(x)^2)*根号下(E(y^2)-E(y)^2))
            //一共有十组相关性需要计算
            double[] corr=new double[10];
            for(int j=0;j<10;j++) {
                corr[j] = (avg_XY[j] - avg[j] * avg[propertiesIndex("Constitution", properties) - propertiesIndex("C1", properties)])
                        / (Math.sqrt(avg_square[j] - Math.pow(avg[j], 2))
                        * Math.sqrt(avg_square[propertiesIndex("Constitution", properties)
                        - propertiesIndex("C1", properties)]
                        - Math.pow(avg[propertiesIndex("Constitution", properties) - propertiesIndex("C1", properties)], 2)));
            }
            System.out.println("第四题：学习成绩和体能测试成绩，两者的相关性是多少？");
            System.out.println("每项理论课程的相关性与体育成绩的相关性共十组，分别是：");
            int mark=0;
            for(int j=0;j<10;j++){
                System.out.print(corr[j]+"  ");
                mark++;
                if(mark==5)
                    System.out.println();
            }

        }

//        测试用输出例
//        for(int k=0;k<stunum;k++) {
//            for(int j=0;j<16;j++) {
//                System.out.print(buffer[j][k]);
//                System.out.print(" ");
//            }
//            System.out.print("\n");
//        }

        //测试用输出例
//        for(;rs.next();){
//            for(int i=0;i<16;i++){
//                System.out.println(rs.getString(i+1));
//                System.out.println(" ");
//            }
//            System.out.println("\n");
//        }

        //旧案
//        for(;rs.next();){
//            int index=isOverlapped(rs.getString("Name"),
//                    buffer[propertiesIndex("Name",properties)]);
//            //如果有重复的信息，用数据库里的信息去覆盖txt文件以求一致性
//            for(int i=0;i<16;i++){
//                buffer[i][index]=rs.getString(i+1);
//            }
//
//        }
//        for(int j=0;j<16;j++) {
//            for(int k=0;k<buffer[j].length;k++) {
//                System.out.println(buffer[j][k]);
//            }
//            System.out.println("\n");
//        }

    }
    //找到下一个索引值
    public static int findIndex(){
        stunum++;
        return stunum-1;
    }

    //旧案
//    public static int isOverlapped(String temp,String[] target){
//        int index;
//        for(index=0;index<stunum;index++){
//            if(temp.equals(target[index])){
//                break;
//            }
//        }
//        if(index==stunum)
//            stunum++;
//        return index;
//    }
    //没什么用的属性下标返回函数
    public static int propertiesIndex(String temp,String[] target){
        for(int index=0;index<target.length;index++){
            if(temp.equals(target[index])){
                return index;
            }
        }
        return -1;
    }

}//