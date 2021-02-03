package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.ExperimentRunning;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExperimentRunningMapper {

    /**
     * 插入一条实验运行记录
     * @param experimentRunning
     * @return
     */
    int insertExperimentRunning(ExperimentRunning experimentRunning);

    /**
     * 获取一个实验下的所有实验运行
     * @param experimentId
     * @return
     */
    List<ExperimentRunning> selectAllExperimentRunningByExperimentId(Integer experimentId);

    /**
     * 通过实验运行id获取实验运行
     * @param runningId
     * @return
     */
    ExperimentRunning selectExperimentRunningByRunningId(Integer runningId);

    /**
     * 通过实验id和删除状态获取实验运行
     * @param experimentId
     * @param isDeleted
     * @return
     */
    List<ExperimentRunning> selectRunningByExperimentIdAndIsDeleted(@Param("experimentId") Integer experimentId, @Param("isDeleted") Integer isDeleted);

    /**
     * 通过删除状态获取该用户的所有的实验运行
     * @param isDeleted
     * @return
     */
    List<ExperimentRunning> selectAllRunningByisDeleted(@Param("userId") Integer userId, @Param("isDeleted") Integer isDeleted);

    /**
     * 更新实验运行
     * @param experimentRunning
     * @return
     */
    int updateExperimentRunning(ExperimentRunning experimentRunning);

    /**
     * 通过实验运行id删除实验运行
     * @param runningId
     * @return
     */
    int deleteRunningByRunningId(Integer runningId);

    /**
     * 获取指定数量的近期创建的实验运行
     *
     */
    List<ExperimentRunning> selectRecentExperimentRunning(@Param("userId") Integer userId, @Param("experimentRunningNum") Integer experimentRunningNum);

    /**
     * @Author Lei junting
     * 根据实验运行id获取实验id
     * @param runningId
     * @return
     */
    Integer selectExperienceId(Integer runningId);
}