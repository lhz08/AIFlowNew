package com.bdilab.aiflow.quartz;

import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.common.enums.RunningStatus;
import com.bdilab.aiflow.mapper.ExperimentRunningMapper;
import com.bdilab.aiflow.model.ExperimentRunning;
import com.bdilab.aiflow.model.job.ApiDifferMap;
import com.bdilab.aiflow.model.job.ApiJobRun;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import com.google.gson.Gson;
import lombok.Data;
import org.mortbay.log.Log;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

@Data
public class QuartzExperimentRunning extends QuartzJobBean {
    @Value("${kubeflow.url}")
    String url;

    @Autowired
    ExperimentService experimentService;

    @Resource
    ApiJobRun apiJobRun;

    @Autowired
    RestTemplate restTemplate;

    @Resource
    ApiDifferMap apiDifferMap;

    @Resource
    ExperimentRunningMapper experimentRunningMapper;



    private Integer experimentId;
    private Integer userId;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("getjobrun execute:==="+new Date());
        String jobId=apiJobRun.getApiJobRunResourceReference().getId();
        String kubeflowUrl = url+"pipeline/apis/v1beta1/runs?page_token=&page_size=10&resource_reference_key.type=JOB&resource_reference_key.id="+jobId+"&filter=";
        System.out.println("kubeflowUrl== "+kubeflowUrl);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(kubeflowUrl,String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue == 200){
            //获取返回结果中的运行id
            Gson gson1 = new Gson();
            Map<String, Map<String,Map<String,String>>> map = gson1.fromJson(responseEntity.getBody(), Map.class);
            ArrayList<Map<String,String>> arrayList = (ArrayList<Map<String,String>>) map.get("runs");
            HashMap<Integer,String> newmap=new HashMap<Integer,String>();
            HashMap<Integer,String> oldmap=new HashMap<Integer,String>();
            System.out.println("arrayList.size()=="+arrayList.size());
            if(arrayList.size()!=0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    newmap.put(i, arrayList.get(i).get("id"));
                }
            }
            newmap.values().removeAll(apiDifferMap.getOldmap().values());
            apiDifferMap.setOldmap(apiDifferMap.getNewmap());
            if(arrayList.size()!=0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    oldmap.put(i, arrayList.get(i).get("id"));
                }
                apiDifferMap.setOldmap(oldmap);
            }
            System.out.println("差集大小"+newmap.size());
            System.out.println("差集"+newmap.size());
            if(newmap.size()!=0)
            for (Map.Entry<Integer, String> entry : newmap.entrySet()) {
                System.out.print("key===="+entry.getKey());
                System.out.print("value===="+entry.getValue());
                String conversationId = UUID.randomUUID().toString();
                ExperimentRunning experimentRunning = new ExperimentRunning();
                experimentRunning.setRunningStatus(RunningStatus.RUNNING.getValue());
                experimentRunning.setFkExperimentId(experimentId);
                experimentRunning.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
                experimentRunning.setStartTime(new Date());
                experimentRunning.setFkUserId(userId);
                experimentRunning.setConversationId(conversationId);
                System.out.println(entry.getValue());
                experimentRunning.setRunId(entry.getValue());
                boolean isSuccess = experimentRunningMapper.insertExperimentRunning(experimentRunning) == 1;
                if(isSuccess){
                    Log.info("插入运行记录成功");
                }
            }
        }

    }
}
