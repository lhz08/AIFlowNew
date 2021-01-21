package com.bdilab.aiflow.service.run;

import com.bdilab.aiflow.model.workflow.EpochInfo;

import java.util.Map;

/**
 * @author smile
 * @data 2020/9/21 10:13
 **/
public interface RunService {


    boolean pushData(String processInstanceId,String taskId,String conversationId,String resultTable);

    public String getComponentId(String string);

    /*
    创建运行
     */
    String createRun(String pipelineId, String pipelineName, Map<String,Object> parameter);

    boolean deleteRunById(String runId);
    void pushEpochInfo(String processLogId, EpochInfo epochInfo, String modelFilePath, String conversionId);
}
