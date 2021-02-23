package com.bdilab.aiflow.common.mysql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author zhangmin
 * @create 2021-01-23 15:29
 */
public class MysqlConnection {
    public String url;
    private String driver;
    private String user;
    private String password;
    public Connection connection;


    public Connection getConn() throws SQLException, ClassNotFoundException {
        Class.forName(this.driver);
        connection= DriverManager.getConnection(url,user,password);
        System.out.println("连接成功");
        return this.connection;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
