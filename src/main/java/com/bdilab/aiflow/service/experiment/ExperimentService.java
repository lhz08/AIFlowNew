package com.bdilab.aiflow.service.experiment;

import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.vo.ExperimentVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/29 12:07
 * @Version 1.0
 **/

public interface ExperimentService {

    /**
     * 创建实验
     * @param fkWorkflowId
     * @param name
     * @param experimentDesc
     */
    Experiment createExperiment(Integer fkWorkflowId, String name,Integer userId, String experimentDesc, String paramJsonString);

    /**
     * 更新实验
     * @param experiment
     */
    boolean updateExperiment(Experiment experiment);

    /**
     * 查看该实验是否有运行记录
     * @param experimentId
     */
    boolean isRunned(Integer experimentId);

    /**
     * 克隆实验
     * @param experimentId
     */
    Experiment copyExperiment(Integer userId,Integer experimentId, String name, String experimentDesc);

    /**
     * 删除实验
     * @param experimentId
     */
    Map<String,Object> deleteExperiment(Integer experimentId) throws Exception;

    /**
     * 开始运行实验，插入实验运行，通知Kubeflow开始运行
     * @param experimentId,userId
     */
    Map<String,Object> startRunExperment(Integer experimentId,Integer userId,String conversationId);

    /**
     * 停止实验
     * @param experimentId
     */
    Map<String,Object> stopExperiment(Integer experimentId);

    /**
     * 还原实验
     * @param experimentId
     */
    boolean restoreExperiment(Integer experimentId);

    /**
     * 回收站获取实验
     * @param isDeleted
     * @param pageNum
     * @param pageSize
     */
    Map<String, Object> getDeletedExperiment(Integer isDeleted,int pageNum,int pageSize);

    /**
     * 获取最近创建的实验
     *
     */
    List<ExperimentVO> getExperiment(Integer userId, Integer experimentNum);

}
