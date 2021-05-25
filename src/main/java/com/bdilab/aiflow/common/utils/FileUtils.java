package com.bdilab.aiflow.common.utils;


import com.alibaba.fastjson.JSONObject;

import com.csvreader.CsvWriter;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileUtils {


    /**
     * 将csv文件转换成二维数组
     */
    public static Map<String,Object> transResultCsvToArray(String filePath) {
        Map<String,Object> result = new HashMap<>();
        try(InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr)) {
            List<List<Double>> array = new ArrayList<>();
            String line=null;
            while((line=reader.readLine())!=null) {
                String[] datas = line.split(",");
                List<Double> list = new ArrayList<>();
                for(String data : datas){
                    list.add(Double.parseDouble(data));
                }
                array.add(list);
            }
            result.put("isSuccess",true);
            result.put("array",array);
        } catch (IOException e) {
            result.put("isSuccess",false);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * todo:3/29
     * @param filePath
     * @param type
     * @return
     */
    public static Map<String,Object> transResultCsvToJson(String filePath,Integer type){
        JSONObject messageMap = new JSONObject();
        try{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
            BufferedReader reader = new BufferedReader(isr);


            JSONObject lineChart = new JSONObject();
            JSONObject xAxis = new JSONObject();
            JSONObject yAxis = new JSONObject();
            JSONObject series = new JSONObject();
            String line=null;
            //type==null或者type==0，默认转换成热力图
            if(type==null||type==0||type==12){
                List<Object[]> sData = new ArrayList<>();
                //在这里更改min可以限定图y轴范围
                int min=0;
                int max=-655355;
                int checkPoint=0;
                List<String> xdata=new ArrayList<String>() ;
                while((line=reader.readLine())!=null) {
                    String[] data = line.split(",");
                    //需要记录x,p数组对，得到最小界和最大界
                    double p1 = Double.parseDouble(data[0]);
                    double p2 = Double.parseDouble(data[2]);
                    double p3 = Double.parseDouble(data[4]);
                    double x1 = Double.parseDouble(data[1]);
                    double x2 = x1 + Double.parseDouble(data[3]);
                    double x3 = x2 + x2 / 2;

                    //得到下界和上界，10为单位
                    min = x1 < min ? (((int) x1) / 10) * 10 : min;
                    max = x3 > max ? ((int) x3 / 10 + 1) * 10 : max;

                    xdata.add(String.valueOf(checkPoint));
                    //测点，y坐标，电阻率
                    Object[] double1 = {checkPoint, (int) x1, p1};
                    Object[] double2 = {checkPoint, (int) x2, p2};
                    Object[] double3 = {checkPoint, (int) x3, p3};
                    sData.add(double1);
                    sData.add(double2);
                    sData.add(double3);

                    checkPoint++;
                }
                //y轴单位长度为10
                List<Integer> ydata=new ArrayList<>();
                for(int h=min;h<=max;h+=10){ ydata.add(h); }

                //转化深度为y轴坐标，如深度23.568，min=20，则y=0
                for(int i=0;i<checkPoint;i++){
                    int m=0;
                    for(int j=0;j<3;j++){
                        //遍历数组，拿到当前深度对应的坐标值
                        int y = ((int)sData.get(i*3+j)[1]-min)/10;
                        sData.get(i*3+j)[1]=y;
                        //补全比当前深度小的坐标值
                        for(;m<y;m+=1){
                            Object[] miss = {i,m,sData.get(i*3+j)[2]};
                            sData.add(miss);
                        }
                        //判断到等于，m+1跳过当前
                        m+=1;
                    }

                }

                xAxis.put("data",xdata);
                yAxis.put("max",max);
                yAxis.put("min",min);
                yAxis.put("data", ydata);
                series.put("data", sData);

                lineChart.put("xAxis",xAxis);
                lineChart.put("yAxis",yAxis);
                lineChart.put("series",series);
                messageMap.put("heatMap",lineChart);
            }
            else if(type==13){
                List sData = new ArrayList<>();
                int min=655355;
                int max=-655355;
                while((line=reader.readLine())!=null){
                    String[] data =line.split(",");

                    double p1 = Double.parseDouble(data[0]);
                    double p2 = Double.parseDouble(data[2]);
                    double p3 = Double.parseDouble(data[4]);
                    double x1=Double.parseDouble(data[1]);
                    double x2=x1+Double.parseDouble(data[3]);
                    double x3=x2+x2/2;

                    min=x1<min ? (int)x1 : min;
                    max=x3>max ? (int)x3+1 : min;
                    double[][] doubles={{x1,p1},{x2,p2},{x3,p3}};
                    sData.add(doubles);
                }
                xAxis.put("max",max);
                xAxis.put("min",min);
                series.put("data", sData);

                lineChart.put("xAxis",xAxis);
                lineChart.put("series",series);
                messageMap.put("lineChart",lineChart);
            }

            return messageMap;
        }
        catch (IOException e) {
            e.printStackTrace();
            messageMap.put("isSuccess",false);
            messageMap.put("message","得到转换result转换图结果失败");
            return messageMap;
        }

    }

    /**
     * 将txt转成csv格式
     * @param oldFilePath
     * @param newFilePath
     */
    public static void txtToCsv(String oldFilePath,String newFilePath){
        File file = new File(oldFilePath);

        try {
            CsvWriter csvWriter = new CsvWriter(newFilePath , ',',  Charset.forName("UTF-8"));
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String string = "";
            while ((string = bufferedReader.readLine()) != null) {
                String[] s = string.split(" | +");
                csvWriter.writeRecord(s);
            }
            csvWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String[]> csvContentPreview(String filePath){
        File file = new File(filePath);
        CsvReader csvReader = new CsvReader();
        List<String[]> previewContent = new ArrayList<>();
        try{
            CsvContainer csv = csvReader.read(file, StandardCharsets.UTF_8);
//            int totalCount = 10;
//            if(csv.getRowCount()<totalCount){
//                totalCount = csv.getRowCount();
//            }
            for(int i = 0;i< csv.getRowCount();i++){
                CsvRow row = csv.getRow(i);
                String[] singleLine = new String[row.getFieldCount()];
                for(int j = 0;j<row.getFieldCount();j++){
                    singleLine[j] = row.getField(j);
                }
                previewContent.add(singleLine);
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return previewContent;
    }
    public static List<String[]> csvContentPreview1(String filePath){
        List<String[]> list = new ArrayList<>();
        try {
            String str = "";
            String[] s ;
            InputStreamReader inputStreamReader = new InputStreamReader( new FileInputStream(filePath),"GBK");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while((str = bufferedReader.readLine())!=null){

                s = str.split(",");
                String []singleLine = new String[s.length+1];
                for(int i = 0;i<s.length;i++)
                {
                    singleLine[i] = s[i];
                }
                singleLine[singleLine.length-1] = "";
                list.add(singleLine);
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return list;
    }
    public static void writeIntoCsv(String buffer,String csvFilePath){
        File saveCsv = new File(csvFilePath);
        try {
            if(!saveCsv.exists()){
                saveCsv.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (saveCsv,true), "gbk"));
            writer.write(buffer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File transferToFile(MultipartFile multipartFile) throws IOException {
        File file = null;
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.",2);
        file = File.createTempFile(fileName[0],fileName[1]);
        multipartFile.transferTo(file);
        file.deleteOnExit();
        return file;
    }

    /**
     * 删除某个目录及目录下的所有子目录和文件
     * @author liran
     * @param file 文件或目录
     * @return 删除结果
     */
    public static boolean delFiles(File file){
        if(!file.exists())return true;

        boolean result = false;
        //目录
        if(file.isDirectory()){
            File[] childrenFiles = file.listFiles();
            for (File childFile:childrenFiles){
                result = delFiles(childFile);
                if(!result){
                    return result;
                }
            }
        }
        //删除 文件、空目录
        result = file.delete();
        return result;
    }

    public static void main(String[] args) {
        List<String[]> strings = csvContentPreview("C:\\Users\\cuishaohui\\Desktop\\c37164e8-a4ce-4fd2-8c92-385265ebdbd9.csv");
        for (String[] s:strings
             ) {
            for (String s1:s
                 ) {
                System.out.println(s1);
            }
        }
    }

    public static  void fieldwriteToCsv(String buffer,String filePath){
        writeIntoCsv(buffer,filePath);
    }


}
