package com.bdilab.aiflow.common.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HBaseConnection {
    private static Configuration conf = null;
    private static Connection conn = null;


    static {
        Properties property = new Properties();
        try {
            InputStream file= HBaseConnection.class.getClassLoader().getResourceAsStream("application.properties");
            property.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        conf = HBaseConfiguration.create();
        conf.set("hbase.master", property.getProperty("hbase.zookeeper.quorum"));
        conf.set("hbase.zookeeper.quorum", property.getProperty("hbase.master"));
        conf.set("hbase.zookeeper.property.clientport", property.getProperty("hbase.zookeeper.property.clientport"));
        try {
            conn = ConnectionFactory.createConnection(conf);
            System.out.println("连接成功");
        } catch (IOException e) {
            System.out.println("连接失败");
            e.printStackTrace();
        }
    }

    public static Connection getConn() {
        return conn;
    }
}
