package com.bdilab.aiflow.mapper;


import com.bdilab.aiflow.model.ExperimentRunningJsonResult;

import java.util.List;

public interface ExperimentRunningJsonResultMapper {

    /**
     * 插入一条实验运行Json结果记录
     * @param experimentRunningJsonResult
     * @return
     */
    int insertExperimentRunningJsonResult(ExperimentRunningJsonResult experimentRunningJsonResult);

    /**
     * 更新实验运行结果Json记录，每次覆盖原结果
     * @param experimentRunningJsonResult
     * @return
     */
    int updateExperimentRunningJsonResult(ExperimentRunningJsonResult experimentRunningJsonResult);

    /**
     * 通过实验运行id查询这次的Json结果(理论上只会查出一个)
     * @param experimentRunningId
     * @return
     */
    List<ExperimentRunningJsonResult> selectExperimentRunningJsonResultByExperimentRunningId(Integer experimentRunningId);

    /**
     * 通过实验运行结果id查询这次的Json结果(唯一)
     * @param experimentRunningResultId
     * @return
     */
    ExperimentRunningJsonResult selectExperimentRunningJsonResultByExperimentRunningJsonResultId(Integer experimentRunningResultId);

    /**
     * 通过实验运行id删除这次实验运行结果的Json字符串
     * @param experimentRunningId
     * @return
     */
    int deleteExperimentRunningJsonResultByExperimentRunningId(Integer experimentRunningId);

    /**
     * 通过实验运行json结果id删除记录
     * @param experimentRunningJsonResultId
     * @return
     */
    int deleteExperimentRunningJsonResultByExperimentRunningJsonResultId(Integer experimentRunningJsonResultId);
}