package com.bdilab.aiflow.service.dataset.impl;


import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.mapper.DatasetMapper;
import com.bdilab.aiflow.model.Dataset;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @create 2020-08-28 15:13
 */
@Service
public class DatasetService implements com.bdilab.aiflow.service.dataset.DatasetService {
    @Autowired
    DatasetMapper datasetMapper;

    /*
     * 分页获得公开数据集信息列表
     */
    @Override
    public Map<String, Object> getPublicDataset(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.getPublicDataset();
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*分页获得用户自定义数据集信息列表*/
    @Override
    public Map<String, Object> getUserDataset(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.getUserDataset(userId);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*注册数据集#{name},#{type},#{fkUserId},#{tags},#{isDeleted},#{datasetAddr},#{datasetDesc},#{createTime}*/
    @Override
    public boolean insertUserDataset(Integer userId,String datasetName, String tags, String filePath, String datasetDesc){
        Dataset dataset=new Dataset();
        dataset.setName(datasetName);
        dataset.setType(1);
        dataset.setTags(tags);
        dataset.setFkUserId(userId);
        dataset.setIsDeleted((byte) 0);
        dataset.setDatasetAddr(filePath);
        dataset.setDatasetDesc(datasetDesc);
        Date date = new Date();
        dataset.setCreateTime(date);
        dataset.setIsDeleted((byte)0);
        return datasetMapper.insertDataset(dataset);
    }

    /*删除数据集--移入回收站*/
    @Override
    public boolean deleteDatasetById(Integer datasetId) {
        return datasetMapper.deleteDatasetById(datasetId);
    }

    /*删除数据集--彻底删除*/
    @Override
    public boolean deleteDatasetCompletelyById(Integer datasetId)  {
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        if(dataset!=null) {
            if(dataset.getDatasetAddr()!=null&&!dataset.getDatasetAddr().equals("")) {
                File file = new File(dataset.getDatasetAddr());
                file.delete();
            }
        }
        return datasetMapper.deleteDatasetCompletelyById(datasetId);
    }
/*    @Override
    public boolean deleteDatasetCompletelyById(Integer datasetId)  {
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        Connection connection=HBaseConnection.getConn();
        try{
        HBaseUtils.deleteTable(dataset.getName(),connection);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return datasetMapper.deleteDatasetCompletelyById(datasetId);
    }*/

    /*分页获取回收站中的数据集列表*/
    @Override
    public Map<String, Object> getDatasetInTrash(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.getDatasetInTrash(userId);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }
    /*从回收站恢复数据集*/
    @Override
    public boolean restoreDataset(Integer datasetId) {
        return datasetMapper.restoreDataset(datasetId);
    }

    /*按名称分页搜索公开数据集*/
    @Override
    public Map<String, Object> searchPublicDatasetByName(String datasetName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.fuzzySelectPublicDatasetByName(datasetName);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*按名称分页搜索用户自定义数据集*/
    @Override
    public Map<String, Object> searchUserDatasetByName(Integer userId, String datasetName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.fuzzySelectUserDatasetByName(userId,datasetName);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }
    /*按tags分页搜索公开数据集*/
    @Override
    public Map<String, Object> searchPublicDatasetByTags(String datasetTags, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.fuzzySelectPublicDatasetByTags(datasetTags);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*按tags分页搜索用户自定义数据集*/
    @Override
    public Map<String, Object> searchUserDatasetByTags(Integer userId, String datasetTags, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.fuzzySelectUserDatasetByTags(userId,datasetTags);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    @Override
    public File downloadDataset(Integer datasetId) {
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        File file = new File(dataset.getDatasetAddr());
        return file;
    }

    /*获取前十条记录用作预览*/
    @Override
    public Map<String, Object> getPreviewList(Integer datasetId) {
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        String filePath=dataset.getDatasetAddr();
        Map<String, Object> data = new HashMap<>();
        List<String[]> csvContent = FileUtils.csvContentPreview(filePath);
        data.put("content",csvContent);
        data.put("total",csvContent == null?0:csvContent.size());
        data.put("desc",dataset.getDatasetDesc());
        return data;
    }
  /*  @Override
    public Map<String, Object> getPreviewList(Integer datasetId) {
        Connection connection=HBaseConnection.getConn();
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        Map<String, Object> data = new HashMap<>();
        List<String> hbaseContent = HBaseUtils.getTableDatalimit10(dataset.getName(),connection);
        data.put("content",hbaseContent);
        data.put("total",hbaseContent == null?0:hbaseContent.size());
        data.put("desc",dataset.getDatasetDesc());
        return data;
    }*/


}
