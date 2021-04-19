package com.bdilab.aiflow.mapper;


import com.bdilab.aiflow.model.ExperimentRunningJsonResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExperimentRunningJsonResultMapper {

    /**
     * 插入一条实验运行Json结果记录
     * @param experimentRunningJsonResult
     * @return
     */
    //勿删
    int insertExperimentRunningJsonResult(ExperimentRunningJsonResult experimentRunningJsonResult);

    /**
     * 更新实验运行结果Json记录，每次覆盖原结果
     * @param experimentRunningJsonResult
     * @return
     */
    //勿删
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
    //勿删
    ExperimentRunningJsonResult selectExperimentRunningJsonResultByExperimentRunningJsonResultId(Integer experimentRunningResultId);


    /**
     * 通过实验运行id和组件id查询Json结果(理论上唯一)
     * @param fkExperimentRunningId 实验运行id
     * @param fkComponentInfoId 组件id
     * @return
     */
    //勿删
    ExperimentRunningJsonResult selectExperimentRunningJsonResultByExperimentRunningIdAndComponentInfoId(@Param("fkExperimentRunningId")Integer fkExperimentRunningId, @Param("fkComponentInfoId")Integer fkComponentInfoId);

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
    //勿删
    int deleteExperimentRunningJsonResultByExperimentRunningJsonResultId(Integer experimentRunningJsonResultId);
}