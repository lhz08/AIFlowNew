package com.bdilab.smartanalyseplatform.model;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataSource {

    private Integer id;
    //PK
    private Integer fkUserId;
    //当前用户id

    private String name;
    //数据源名字

    private Boolean type;
    //数据源类型 0是habse 1是mysql

    private String config;
    //配置信息

    private String description;
    //相关描述

    private Date createTime;
    //创建时间

    private String password;

    private String url;

    private String userName;

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getFkUserId() {
        return fkUserId;
    }

    public Integer getId() {
        return id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Boolean getType() {
        return type;
    }

    public String getConfig() {
        return config;
    }



    public void setFkUserId(Integer fkUserId) {
        this.fkUserId = fkUserId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setConfig(String config) {

        try{
            if(!StringUtils.isEmpty(config)){
                Gson gson = new Gson();
                Map<String,String> map = new HashMap<String,String>();
                map = gson.fromJson(config,map.getClass());
                this.password = StringUtils.isEmpty(map.get("password"))?"":map.get("password");
                this.url =  StringUtils.isEmpty(map.get("url"))?"":map.get("url");
                this.userName = StringUtils.isEmpty(map.get("username"))?"":map.get("username");
            }

        }catch (Exception e){

        }

        this.config = config;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(Boolean type) {
        this.type = type;
    }
}
