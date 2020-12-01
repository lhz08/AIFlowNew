package com.bdilab.aiflow.service.model;

import com.bdilab.aiflow.model.Model;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

public interface ModelService {

    boolean createModel(Integer modelId,String modelName, String modelDesc);

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
    /**
     * 将模型封装成自定义组件
     *
     */
    boolean setModelToComponent(Integer modelId,Integer userId,String componentName,String componentDesc,String tag);

    /**
     * python端保存模型到本地
     * @param runningId
     * @param componentId
     * @param conversationId
     * @param modelFileAddr
     * @return
     */
    boolean saveModel(String runningId,String componentId,String conversationId,String modelFileAddr);

    HttpServletResponse downloadModelFromMinio(Integer userId,Integer modelId,HttpServletResponse response);

}
