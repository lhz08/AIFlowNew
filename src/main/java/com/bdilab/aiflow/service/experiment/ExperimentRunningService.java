package com.bdilab.aiflow.service.experiment;

import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.ExperimentRunning;
import com.bdilab.aiflow.vo.ExperimentRunningVO;
import io.swagger.models.auth.In;

import java.util.List;
import java.util.Map;


/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/31 12:07
 * @Version 1.0
 **/

public interface ExperimentRunningService {

    /**
     * 更新实验运行
     * @param experimentRunning
     */
    boolean updateExperimentRunning(ExperimentRunning experimentRunning);


    /**
     * 删除实验运行ById
     * @param runningId
     */
    Map<String,Object> deleteExperimentRunning(Integer runningId) throws Exception;

    /**
     * 获取一个实验下的未删除的实验运行
     * @param experimentId
     * @param isDeleted
     * @param pageNum
     * @param pageSize
     */
    Map<String, Object> selectRunningByExperimentId(Integer experimentId, Integer isDeleted, int pageNum, int pageSize);

    /**
     * 获取回收站里所有的实验运行
     * @param userId
     * @param isDeleted
     * @param pageNum
     * @param pageSize
     */
    Map<String, Object> getDeletedRunning(Integer userId, Integer isDeleted, int pageNum, int pageSize);

    /**
     * 获取回收站里所有的实验运行
     * @param runningId
     */
    boolean restoreExperimentRunning(Integer runningId);

    /**
     * 获取回收站里所有的实验运行
     * @param runningId
     */
    Map<String, Object> getRunningLog(Integer runningId);
    /**
     * 获取最近创建的实验运行
     *
     */
    List<ExperimentRunningVO>  getExperimentRunning(Integer userId, Integer experimentRunningNum);

    /**
     * 停止实验运行
     * @param runningId
     */
    Map<String,Object> stopExperimentRunning(Integer runningId);

    /**
     * 根据实验运行id获取实验id
     * @Author Lei junting
     * @param runningId
     * @return
     */
    Integer getExperienceId(Integer runningId);

    /**
     * @Author Lei junting
     * 根据实验运行id获取实验运行
     * @param runningId
     * @return
     */

    ExperimentRunning getExperienceRunningInfo(Integer runningId);
}
