package com.bdilab.aiflow.service.run;

import java.util.Map;

/**
 * @author smile
 * @data 2020/9/21 10:13
 **/
public interface RunService {


    boolean pushData(String processInstanceId,String taskId,String conversationId,String resultTable,String resultPath);

    /*
    创建运行
     */
    String createRun(String pipelineId, String pipelineName, Map<String,Object> parameter);



}
