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
     * todo:3/29
     * @param filePath
     * @param type
     * @return
     */
    public static Map<String,Object> transResultCsvToJson(String filePath,Integer type){
        Map<String,Object> messageMap = new HashMap<>(2);
        try{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
            BufferedReader reader = new BufferedReader(isr);


            JSONObject lineChart =new JSONObject();
            JSONObject xAxis=new JSONObject();
            JSONObject yAxis=new JSONObject();
            JSONObject series=new JSONObject();
            String line=null;
            //type==null或者type==0，默认转换成热力图
            if(type==null||type==0||type==12){

                while((line=reader.readLine())!=null){
                    String[] data =line.split(",");

                }
            }
            else if(type==13){
                List sData = new ArrayList<>();
                int min=65535;
                int max=-65535;
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

            }



            lineChart.put("xAxis",xAxis);
            lineChart.put("yAxis",yAxis);
            lineChart.put("series",series);


            messageMap.put("lineChart",lineChart);
            messageMap.put("isSuccess",true);
            messageMap.put("message","得到转换result转换图结果成功");
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
    private static void writeIntoCsv(String buffer,String csvFilePath){
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
}
