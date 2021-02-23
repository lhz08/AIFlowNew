package com.bdilab.aiflow.service.dataset.impl;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.mapper.DlDatasetMapper;
import com.bdilab.aiflow.model.DlDataset;
import com.bdilab.aiflow.service.dataset.DlDatasetService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Service
public class DlDatasetServiceImpl implements DlDatasetService {
    @Resource
    DlDatasetMapper dlDatasetMapper;

    @Autowired
    FilePathConfig filePathConfig;

    /*注册深度学习数据集*/
    @Override
    public boolean insertUserDlDataset(Integer userId, String datasetName, String tags, String datasetAddr, String datasetDesc, Integer isAnnotation, Integer originFileType) {
        DlDataset dlDataset = new DlDataset();
        dlDataset.setName(datasetName);
        dlDataset.setType((byte)1);
        dlDataset.setFkUserId(userId);
        dlDataset.setTags(tags);
        dlDataset.setIsDeleted((byte)0);
        dlDataset.setDatasetAddr(datasetAddr);
        dlDataset.setDatasetDesc(datasetDesc);
        dlDataset.setCreateTime(new Date());
        dlDataset.setOriginFileType(originFileType.byteValue());
        dlDataset.setIsAnnotation(isAnnotation.byteValue());
        return dlDatasetMapper.insertSelective(dlDataset);
    }

    /*按名称分页搜索公开深度学习数据集*/
    @Override
    public Map<String, Object> searchPublicDlDatasetByName(String datasetName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<DlDataset> dlDatasetList = dlDatasetMapper.fuzzySelectPublicDlDatasetByName(datasetName);
        PageInfo pageInfo = new PageInfo<>(dlDatasetList);
        Map<String, Object> data = new HashMap<>(3);
        data.put("Dataset List", dlDatasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }


    /*按名称分页搜索用户自定义深度学习数据集*/
    @Override
    public Map<String, Object> searchUserDlDatasetByName(Integer userId, String datasetName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<DlDataset> dlDatasetList = dlDatasetMapper.fuzzySelectUserDlDatasetByName(userId, datasetName);
        PageInfo pageInfo = new PageInfo<>(dlDatasetList);
        Map<String, Object> data = new HashMap<>(3);
        data.put("Dataset List", dlDatasetList);
        data.put("Total Page Num", pageInfo.getPages());
        data.put("Total", pageInfo.getTotal());
        return data;
    }
    /*
     * 分页获得公开dl数据集信息列表
     */
    @Override
    public Map<String, Object> getPublicDldataset(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<DlDataset> datasetList = dlDatasetMapper.getPublicDldataset();
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }
    /*分页获取用户自定义dl数据集信息列表*/
    @Override
    public Map<String, Object> getUserDldataset(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<DlDataset> datasetList = dlDatasetMapper.getUserDldataset(userId);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*按tags分页搜索公开深度学习数据集*/
    @Override
    public Map<String, Object> searchPublicDlDatasetByTags(String datasetTags, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<DlDataset> dlDatasetList = dlDatasetMapper.fuzzySelectPublicDlDatasetByTags(datasetTags);
        PageInfo pageInfo = new PageInfo<>(dlDatasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",dlDatasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*按tags分页搜索用户自定义深度学习数据集*/
    @Override
    public Map<String, Object> searchUserDlDatasetByTags(Integer userId, String datasetTags, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<DlDataset> dlDatasetList = dlDatasetMapper.fuzzySelectUserDlDatasetByTags(userId,datasetTags);
        PageInfo pageInfo = new PageInfo<>(dlDatasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",dlDatasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*根据id获取深度学习数据集*/
    @Override
    public DlDataset getDlDatasetByPrimaryId(Integer id) {
        return dlDatasetMapper.selectByPrimaryKey(id);
    }

    /**
     * 向流中写入文件
     * @param file 要传输的文件
     * @param response 输出流
     * @throws IOException
     */
    @Override
    public HttpServletResponse downloadDlDataset(File file, HttpServletResponse response) throws IOException {
        int BUFFER_SIZE = 2 << 11;
        byte[] buffer = new byte[BUFFER_SIZE];
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ServletOutputStream outputStream = response.getOutputStream()) {
            int i = bis.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            throw new IOException("IO流写入失败",e);
        }
        return response;
    }

    /*删除数据集--移入回收站*/
    @Override
    public boolean deleteDldatasetById(Integer datasetId) {
        return dlDatasetMapper.deleteDldatasetById(datasetId);
    }

    /*从回收站单个或批量彻底删除数据集*/
    @Override
    public boolean deleteDldatasetCompletelyById(int datasetId) {
        DlDataset dataset = dlDatasetMapper.selectByPrimaryKey(datasetId);
        File file = new File(dataset.getDatasetAddr());
        FileUtils.delFiles(file);
        return dlDatasetMapper.deleteDldatasetCompletelyById(datasetId);
    }

    /*分页获取回收站中的数据集列表*/
    @Override
    public Map<String, Object> getDldatasetInTrash(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<DlDataset> datasetList = dlDatasetMapper.getDldatasetInTrash(userId);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;    }

        /*从回收站单个或批量恢复数据集*/
    @Override
    public boolean restoreDldataset(int datasetId) {
        return dlDatasetMapper.restoreDldataset(datasetId);
    }

    /*获得深度学习数据集-所有照片的相对路径的集合*/
    @Override
    public ArrayList<String> getImagePaths(File file, String prePath) {
        ArrayList<String> list = new ArrayList<>();
        getImagePaths(file,prePath,list);
        return list;
    }

    private void getImagePaths(File file, String prePath, ArrayList<String> list){
        if(file.isDirectory()){
            for(File subFile : file.listFiles()){
                getImagePaths(subFile,prePath,list);
            }
        }else {
            String path = file.getAbsolutePath().substring(prePath.length()+1);
            //如果是windows操作系统，将'\'转换为'/'
            if(File.separatorChar == '\\'){path = path.replaceAll("\\\\","/");}
            list.add(path);
        }
    }
}
