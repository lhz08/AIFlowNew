package com.bdilab.aiflow.common.utils;


import com.csvreader.CsvWriter;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {
    /**
     * 将txt转成csv格式
     * @param oldFilePath
     * @param newFilePath
     */
    public static void
    txtToCsv(String oldFilePath,String newFilePath){
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
