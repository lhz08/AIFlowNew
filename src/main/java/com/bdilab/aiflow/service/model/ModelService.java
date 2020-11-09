package com.bdilab.aiflow.service.model;

import com.bdilab.aiflow.model.Model;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ModelService {

    boolean createModel(String modelName, Integer userId, Integer runningId, String modelDesc,String model);

    Map<String, Object> getModelByUser(Integer userId, int pageNum, int pageSize);

    boolean  editModel(Integer modelId, String modelName, String modelDesc);

    boolean deleteModelById(Integer modelId);

    boolean deleteModelCompletelyById(Integer modelId);

    Map<String, Object> getModelInTrash(Integer userId, int pageNum, int pageSize);

    boolean restoreModel(Integer modelId);

    Map<String, Object> searchModelByName(Integer userId, String modelName,int pageNum, int pageSize);

    File downloadDataset(Integer modelId);

    /**
     * 根据运行id和删除状态获取模型
     * @param runningId
     * @param isDeleted
     */
    List<Model> getAllModelByRunningIdAndIsDeleted(Integer runningId, Byte isDeleted);

    /**
     * 将模型的实验运行外键置NULL
     * @param modelId
     */
    boolean setRunningIdNull(Integer modelId);
}
