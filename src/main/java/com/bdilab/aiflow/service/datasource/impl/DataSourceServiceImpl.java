package com.bdilab.aiflow.service.datasource.impl;



import com.bdilab.aiflow.mapper.DataSourceMapper;
import com.bdilab.aiflow.model.DataSource;
import com.bdilab.aiflow.service.datasource.DataSourceService;

import net.sf.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Decription TODO
 * @Author lucienslei
 * @Date 2020/3/24 11:20
 * @Version 4.1
 **/
@Service
public class DataSourceServiceImpl implements DataSourceService {
    @Autowired
    DataSourceMapper dataSourceMapper;


    @Override
    public boolean importDataSource( Integer userId, String name, boolean type, String url,String username,String password, String description, Date createTime){

        Map<String, String> map = new HashMap<String, String>();
        map.put("url", url);
        map.put("username", username);
        map.put("password", password);
        JSONObject jsonObject = JSONObject.fromObject(map);
        String config = jsonObject.toString();
        DataSource datasource =new DataSource();

        datasource.setFkUserId(userId);
        datasource.setName(name);
        datasource.setType(type);
        datasource.setDescription(description);
        datasource.setConfig(config);
        datasource.setCreateTime(new Date());
        dataSourceMapper.insertDataSource(datasource);
        return true;


    }

    @Override
    public boolean deleteDataSource(int datasourceId){
        dataSourceMapper.deleteDataSourceById(datasourceId);
        return true;
    }

    @Override
    public boolean updateDataSource( int id,Integer userId, String name, boolean type, String url, String username, String password, String description, Date createTime){
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", url);
        map.put("username", username);
        map.put("password", password);
        JSONObject jsonObject = JSONObject.fromObject(map);
        String config = jsonObject.toString();

        DataSource datasource=dataSourceMapper.selectDataSourceById(id);
        if(datasource==null){
            return false;
        }
        datasource.setFkUserId(userId);
        datasource.setName(name);
        datasource.setType(type);
        datasource.setDescription(description);
        datasource.setConfig(config);
        datasource.setCreateTime(new Date());
        dataSourceMapper.updateDataSource(datasource);


        return true;
    }

    @Override
    public boolean addHBaseDatasource(Integer userId, Map<String, String> config, String name, String description) {
        DataSource datasource = new DataSource();
        datasource.setCreateTime(new Date());
        datasource.setDescription(description);
        datasource.setFkUserId(userId);
        datasource.setName(name);
        Gson gson = new Gson();
        datasource.setConfig(gson.toJson(config));
        datasource.setType(false);

        return dataSourceMapper.insertDataSource(datasource);
    }

    @Override
    public boolean updateHbaseDataSource(Integer userId,Integer id, Map<String, String> config, String name, String description) {
        DataSource datasource = dataSourceMapper.selectDataSourceById(id);
        Gson gson = new Gson();
        Map<String,String> originConfig = gson.fromJson(datasource.getConfig(),new TypeToken<HashMap<String,String>>(){}.getType());
        for(String key:originConfig.keySet()){
            if(config.get(key)!=null){
                originConfig.replace(key,config.get(key));
            }
        }

        datasource.setCreateTime(new Date());
        datasource.setDescription(description);
        datasource.setFkUserId(userId);
        datasource.setName(name);
        datasource.setType(false);

        datasource.setConfig(gson.toJson(originConfig));
        return dataSourceMapper.updateDataSource(datasource);
    }

    @Override
    public Map<String, Object> generatePreview(Integer fkUserId) {
        List<DataSource> datasourceList = dataSourceMapper.selectAllDataSource(fkUserId);
        Map<String,Object> data = new HashMap<>(3);

        data.put("dateSouce list",datasourceList);

        return data;
    }
}
