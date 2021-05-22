package com.bdilab.aiflow.service.dataset;


import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.channels.MulticastChannel;
import java.util.List;
import java.util.Map;

/**
 * @author  zhangmin
 * @create 2020-08-28 16:03
 */
public interface DatasetService {

    Map<String, Object> getPublicDataset(int pageNum, int pageSize);

    Map<String, Object> getUserDataset(Integer userId, int pageNum, int pageSize);

    /**
     *
     * 上传数据集文件到minio服务器上
     * @param file
     * @param datasetName
     * @param tags
     * @param datasetDec
     * @param userId
     * @return
     */
    boolean uploadDatasetToMinio(MultipartFile file, String datasetName, String tags, String datasetDec, Integer userId);


    boolean insertUserDataset(Integer userId, String datasetName, String tags, String filePath, String datasetDesc);

    /**
     * 通过字段导入mysql数据
     * @param datasourceId
     * @param tableName
     * @param userId
     * @param datasetName
     * @param datasetDesc
     * @param field
     * @return
     */
    boolean importMySqlDataSourceByField(Integer datasourceId, String tableName, Integer userId, String datasetName, String datasetDesc,String tags, List<String> field);
    /**
     * 通过自定义sql语句导入数据
     * @param datasourceId
     * @param userId
     * @param datasetName
     * @param datasetDesc
     * @param sql
     * @return
     */
    boolean importMysqlDataSourceBySql(Integer datasourceId,Integer userId,String datasetName, String datasetDesc,String tags,String sql);


    boolean deleteDatasetById(Integer datasetId);

    boolean deleteDatasetCompletelyById(Integer datasetId);

    Map<String, Object> getDatasetInTrash(Integer userId, int pageNum, int pageSize);

    boolean restoreDataset(Integer datasetId);

    Map<String, Object> searchPublicDatasetByName(String datasetName, int pageNum, int pageSize);

    Map<String, Object> searchUserDatasetByName(Integer userId, String datasetName, int pageNum, int pageSize);

    Map<String, Object> searchPublicDatasetByTags(String datasetTags, int pageNum, int pageSize);

    Map<String, Object> searchUserDatasetByTags(Integer userId, String datasetTags, int pageNum, int pageSize);

    File downloadDataset(Integer datasetId);

    Map<String, Object> getPreviewList(Integer datasetId);

    HttpServletResponse downloadDatasetFromMinio(Integer userId,Integer datasetId,HttpServletResponse response);

}
