package com.bdilab.aiflow.quartz;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import lombok.Data;
import org.mortbay.log.Log;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class QuartzExperimentJob extends QuartzJobBean {
    @Autowired
    ExperimentService experimentService;
    private Integer experimentId;
    private Integer userId;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("myjob execute:==="+new Date());
        String conversationId = UUID.randomUUID().toString();
        Map<String,Object> isSuccess=experimentService.startRunExperment(experimentId,userId,conversationId);
        if(isSuccess.get("isSuccess").equals(true)){
            Map<String,Object> data=new HashMap<>(2);
            data.put("experimentRunningId",isSuccess.get("experimentRunningId"));
            data.put("conversationId",conversationId);
            ResponseResult responseResult = new ResponseResult(true,"001",isSuccess.get("message").toString());
            responseResult.setData(data);
            Log.info(responseResult.getMeta().getCode());
        }
        Log.info(String.valueOf(false),"002",isSuccess.get("message").toString());
    }
}
