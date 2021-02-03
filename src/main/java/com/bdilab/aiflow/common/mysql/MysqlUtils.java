package com.bdilab.aiflow.common.mysql;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.csvreader.CsvWriter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.midi.SoundbankResource;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;

/**
 * @author zhangmin
 * @create 2021-01-23 15:47
 */
public class MysqlUtils {

    @Autowired
    FilePathConfig filePathConfig;
    /*
    * 按表名读取表中数据并转换为.csv文件
    */
    public String readTableToCSV(String tableName, Connection connection,String filePath) throws SQLException, IOException {
        //查询数据库
        Statement statement = connection.createStatement();
        String sql="select * from "+tableName;
        ResultSet rs =statement.executeQuery(sql);
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();   //获得列数
        //设置csv文件写入
        CsvWriter csvWriter = new CsvWriter(filePath , ',',  Charset.forName("UTF-8"));
        while (rs.next()){
            String[] buffer=new String[columnCount];
            int i;
            for (i=1;i<=columnCount;i++){
                buffer[i-1]=rs.getString(i);
            }
            csvWriter.writeRecord(buffer);
        }
        csvWriter.close();
        statement.close();
        return filePath;
    }
}

