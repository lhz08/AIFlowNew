package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.Model;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModelMapper {

    int insertModel(Model model);

    List<Model> getModelByUser(Integer userId);

    Model selectModelById(Integer modelId);

    boolean editModel(Model model);

    boolean deleteModelById(Integer modelId);

    Model selectModelByExperimentRunningId(Integer experimentRunningId);
    boolean deleteModelCompletelyById(Integer modelId);

    List<Model> getModelInTrash(Integer userId);

    boolean restoreModel(Integer modelId);

    List<Model> fuzzySelectModelByName(Integer userId,String modelName);



    /**
     * @Author Jin Lingming
     * 根据实验运行id获取实验信息
     * @param runningId
     * @return
     */
    List<Model> getAllModelByRunningId(Integer runningId);

    /**
     * @Author Jin Lingming
     * 根据模型id获取实验信息
     * @param id
     * @return
     */
    Model getModelById(Integer id);

    /**
     * @Author Jin Lingming
     * 根据实验运行id和删除状态获取实验信息
     * @param runningId
     * @return
     */
    List<Model> getAllModelByRunningIdAndIsDeleted(@Param("runningId") Integer runningId, @Param("isDeleted") Byte isDeleted);

    /**
     * @Author Jin Lingming
     * 更新模型
     * @param model
     * @return
     */
    int updateModel(Model model);



    /**
     * @Author Jin Lingming
     * 通过模型id将实验运行id置null
     * @param modelId
     * @return
     */
    int updateRunningIdNull(Integer modelId);

    /**
     * @Author Lei junting
     * 获取模型的实验id
     * @param modelId
     * @return
     */

    int getExperienceId(Integer modelId);
}