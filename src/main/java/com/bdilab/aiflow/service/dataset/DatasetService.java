package com.bdilab.aiflow.service.dataset;


import java.io.File;
import java.util.Map;

/**
 * @author
 * @create 2020-08-28 16:03
 */
public interface DatasetService {

    Map<String, Object> getPublicDataset(int pageNum, int pageSize);

    Map<String, Object> getUserDataset(Integer userId, int pageNum, int pageSize);

    boolean insertUserDataset(Integer userId, String datasetName, String tags, String filePath, String datasetDesc);

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

}
