package com.bdilab.aiflow.service.job;

import com.bdilab.aiflow.model.job.ApiSchedule;
import com.bdilab.aiflow.model.job.ExperimentJob;

import java.text.ParseException;
import java.util.Map;

public interface ExperimentJobService {

    /**
     * 创建多任务 fkExperimentId;
     *     private String jobTime;
     *     private String name;
     *     private String group;
     */
    Map<String,Object> createExperimentJob(Integer fkUserId, Integer fkExperimentId, String jobTime);
    /**
     * @Author Lei junting
     * 根据实验运行任务停止试验任务
     * @param jobId
     * @return
     */

    ExperimentJob stopExperienceJob(Integer jobId);
    /**
     * @Author Lei junting
     * 根据实验运行任务启动试验任务
     * @param jobId
     * @return
     */
    ExperimentJob startExperienceJob(Integer jobId);
    /**
     *     调用kubeflow接口jobs
     *     创建多任务 fkExperimentId;
     *     private String jobTime;
     *     private String name;
     *     private String group;
     */
    Map<String,Object> startCycleRunExperment(Integer experimentId, Integer userId, String conversationId, ApiSchedule apiSchedule) throws ParseException;
    /**
     *     调用kubeflow接口get/runs
     *     定期查询对应多任务下的running
    * */
    boolean getExperimentJobRunning(Integer userId,  String type, ExperimentJob experimentJob);

    ExperimentJob selectJobById(Integer id);
}
