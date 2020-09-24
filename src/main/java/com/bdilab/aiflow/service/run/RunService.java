package com.bdilab.aiflow.service.run;

/**
 * @author smile
 * @data 2020/9/21 10:13
 **/
public interface RunService {


    boolean pushData(String processInstanceId,String taskId,String conversationId,String resultTable,String resultPath);


}
