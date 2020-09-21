package com.bdilab.aiflow.common.utils;


import com.csvreader.CsvWriter;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

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
            int totalCount = 10;
            if(csv.getRowCount()<totalCount){
                totalCount = csv.getRowCount();
            }
            for(int i = 0;i<totalCount;i++){
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


}