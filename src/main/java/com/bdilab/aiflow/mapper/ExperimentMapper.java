package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.Experiment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExperimentMapper {

    /**
     * 插入一条实验记录
     * @param experiment
     * @return
     */
    int insertExperiment(Experiment experiment);

    /**
     * 更新实验
     * @param experiment
     * @return
     */
    int updateExperiment(Experiment experiment);

    /**
     * 更新isMarkTemplate
     * @param experiment
     * @return
     */
    int updateExperimentIsMarkTemplate(Experiment experiment);

    /**
     * 根据实验id获取实验信息
     * @param experimentId
     * @return
     */
    Experiment selectExperimentById(Integer experimentId);


    /**
     * 根据流程id和删除状态获取所有实验
     * @param experiment
     * @return
     */
    List<Experiment> getAllExperimentByWorkflowIdAndIsDeleted(Experiment experiment);


    /**
     * 通过实验id删除实验
     * @param experimentId
     * @return
     */
    int deleteExperimentById(Integer experimentId);

    /**
     * 根据删除状态获取所有实验
     * @param isDeleted
     */
    List<Experiment> selectAllExperimentByisDeleted(Integer isDeleted);
    /**
     * 获取指定数量的近期创建的实验
     *
     */
    List<Experiment> selectRecentExperiment(@Param("userId") Integer userId, @Param("experimentNum") Integer experimentNum);

    /**
     * 根据流程id查找模板
     * @param workflowId
     * @return
     */
    List<Integer> selectExperimentByWorkflowId(Integer workflowId);
}