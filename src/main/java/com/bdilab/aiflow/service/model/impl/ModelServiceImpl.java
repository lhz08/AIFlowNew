package com.bdilab.aiflow.service.model.impl;

import com.bdilab.aiflow.mapper.ModelMapper;
import com.bdilab.aiflow.model.Dataset;
import com.bdilab.aiflow.model.Model;
import com.bdilab.aiflow.service.model.ModelService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public boolean createModel(String modelName, Integer userId, Integer runningId, String modelDesc) {

        Model model=new Model();
        model.setName(modelName);
        model.setFkUserId(userId);
        model.setFkRunningId(runningId);
        model.setIsDeleted((byte) 0);
        model.setModelFileAddr(".");
        model.setModelDesc(modelDesc);
        Date date = new Date();
        model.setCreateTime(date);
        return modelMapper.insertModel(model);
    }


    /*分页获得模型信息列表*/
    @Override
    public Map<String, Object> getModelByUser(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Model> datasetList = modelMapper.getModelByUser(userId);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Model List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*编辑模型信息*/
    @Override
    public boolean editModel(Integer modelId, String modelName, String modelDesc) {
        Model model=modelMapper.selectModelById(modelId);
        if(model.getName().equals(modelName) && model.getModelDesc().equals(modelDesc)){
            return true;
        }
        model.setName(modelName);
        model.setModelDesc(modelDesc);
        return modelMapper.editModel(model);
    }

    /*删除模型--移入回收站*/
    @Override
    public boolean deleteModelById(Integer modelId) {
        return modelMapper.deleteModelById(modelId);
    }

    /*删除模型--彻底删除*/
    @Override
    public boolean deleteModelCompletelyById(Integer modelId)  {
        Model model = modelMapper.selectModelById(modelId);
        if(model.getModelFileAddr()!=null) {
            if(!model.getModelFileAddr().equals("")) {
                File file = new File(model.getModelFileAddr());
                file.delete();
            }
        }
        return modelMapper.deleteModelCompletelyById(modelId);
    }

    /*分页获取回收站中的模型列表*/
    @Override
    public Map<String, Object> getModelInTrash(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Model> modelList = modelMapper.getModelInTrash(userId);
        System.out.println(modelList);
        PageInfo pageInfo = new PageInfo<>(modelList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Model List",modelList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*从回收站恢复模型*/
    @Override
    public boolean restoreModel(Integer modelId) {
        return modelMapper.restoreModel(modelId);
    }


    /*按名称分页搜索模型*/
    @Override
    public Map<String, Object> searchModelByName(Integer userId, String modelName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Model> modelList = modelMapper.fuzzySelectModelByName(userId,modelName);
        PageInfo pageInfo = new PageInfo<>(modelList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("model List",modelList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*下载模型*/
    @Override
    public File downloadDataset(Integer modelId) {
        Model model = modelMapper.selectModelById(modelId);
        File file = new File(model.getModelFileAddr());
        return file;
    }

    @Override
    public List<Model> getAllModelByRunningIdAndIsDeleted(Integer runningId,Byte isDeleted){

        return modelMapper.getAllModelByRunningIdAndIsDeleted(runningId,isDeleted);
    }

    @Override
    public boolean setRunningIdNull(Integer modelId){
        boolean isSuccess=modelMapper.updateRunningIdNull(modelId)==1;
        return isSuccess;
    }
}
