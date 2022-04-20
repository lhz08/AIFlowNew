package com.bdilab.aiflow.service.job.impl;

import com.bdilab.aiflow.common.enums.JobStatus;
import com.bdilab.aiflow.common.utils.JsonUtils;
import com.bdilab.aiflow.common.utils.RandomStr;
import com.bdilab.aiflow.common.utils.UTCDateUtils;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.*;
import com.bdilab.aiflow.model.*;
import com.bdilab.aiflow.model.job.ApiJobRun;
import com.bdilab.aiflow.model.job.ApiJobRunResourceReference;
import com.bdilab.aiflow.model.job.ApiSchedule;
import com.bdilab.aiflow.model.job.ExperimentJob;
import com.bdilab.aiflow.quartz.QuartzExperimentRunning;
import com.bdilab.aiflow.service.experiment.ExperimentRunningService;
import com.bdilab.aiflow.service.job.ExperimentJobService;
import com.bdilab.aiflow.service.quartz.QuartzService;
import com.bdilab.aiflow.service.run.RunService;
import com.bdilab.aiflow.service.template.TemplateService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExperimentJobServiceImpl implements ExperimentJobService {
    @Resource
    ExperimentJobMapper experimentJobMapper;
    @Resource
    ExperimentMapper experimentMapper;
    @Resource
    ExperimentRunningMapper experimentRunningMapper;
    @Resource
    WorkflowMapper workflowMapper;
    @Resource
    ComponentInfoMapper componentInfoMapper;
    @Resource
    CustomComponentMapper customComponentMapper;
    @Autowired
    RunService runService;

    @Autowired
    ExperimentJobService experimentJobService;

    @Resource
    TemplateMapper templateMapper;

    @Autowired
    ExperimentRunningService experimentRunningService;

    @Autowired
    TemplateService templateService;

    @Autowired
    QuartzService quartzService;
    @Resource
    ModelMapper modelMapper;
    @Override
    public Map<String,Object> createExperimentJob(Integer userId, Integer fkExperimentId, String jobTime) {
        Map<String,Object> messageMap=new HashMap<>(2);
        ExperimentJob experimentJob=new ExperimentJob();
        experimentJob.setFkUserId(userId);
        experimentJob.setFkExperimentId(fkExperimentId);
        experimentJob.setCronJobTime(jobTime);
        experimentJob.setJobName(RandomStr.randomStr(6,false));
        experimentJob.setJobGroupName(RandomStr.randomStr(6,false));
        experimentJob.setStatus(JobStatus.ENABLED.getValue());
        boolean isSuccess=experimentJobMapper.insert(experimentJob)==1;
        if(!isSuccess){
            messageMap.put("isSuccess",false);
            messageMap.put("message","定时任务失败");
            return messageMap;
        }
        messageMap.put("isSuccess",true);
        messageMap.put("message","定时任务成功");
        messageMap.put("experimentJobId", experimentJob.getId());
        messageMap.put("experimentJobName", experimentJob.getJobName());
        messageMap.put("experimentJobGroupName", experimentJob.getJobGroupName());
        return messageMap;
    }
    @Override
    public ExperimentJob stopExperienceJob(Integer jobId){
        ExperimentJob experimentJob=experimentJobMapper.selectById(jobId);
        experimentJob.setStatus(JobStatus.DISENABLED.getValue());
        experimentJobMapper.updateById(experimentJob);
        return experimentJob;
    }

    @Override
    public ExperimentJob startExperienceJob(Integer jobId) {
        ExperimentJob experimentJob=experimentJobMapper.selectById(jobId);
        experimentJob.setStatus(JobStatus.ENABLED.getValue());
        experimentJobMapper.updateById(experimentJob);
        return experimentJob;
    }

    @Override
    public Map<String,Object> startCycleRunExperment(Integer experimentId, Integer userId, String conversationId, ApiSchedule apiSchedule) throws ParseException {
        Map<String,Object> messageMap=new HashMap<>(2);
        ExperimentJob experimentJob=new ExperimentJob();
        experimentJob.setFkUserId(userId);
        experimentJob.setFkExperimentId(experimentId);
        experimentJob.setStartTime(UTCDateUtils.dateInvert(apiSchedule.getStart_time()));
        experimentJob.setEndTime(UTCDateUtils.dateInvert(apiSchedule.getEnd_time()));
        experimentJob.setPeriodicJobTime(apiSchedule.getScheduleTime());
        experimentJob.setJobName(RandomStr.randomStr(6,true));
        experimentJob.setJobGroupName(RandomStr.randomStr(6,true));
        experimentJob.setStatus(JobStatus.ENABLED.getValue());
        experimentJob.setJobId("");
        boolean isSuccess=experimentJobMapper.insert(experimentJob)==1;
        if(!isSuccess){
            messageMap.put("isSuccess",false);
            messageMap.put("message","周期运行失败，创建周期运行失败");
            return messageMap;
        }

        Experiment experiment = experimentMapper.selectExperimentById(experimentId);
        System.out.println(experiment.getParamJsonString());

        Gson gson = new Gson();
        Map<String,String> componentIdName = new LinkedHashMap<>();
        Workflow workflow = workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        String xmlPath = workflow.getWorkflowXmlAddr();//本地测试注意,此文件需要从服务器获取
        //String xmlPath = workflow.getWorkflowXmlAddr().replaceAll("home/", "E:/home/");//本地测试注意,此文件需要从服务器获取
        String json = gson.toJson(XmlUtils.getPythonParametersMap(xmlPath));
        List<String> taskList = JsonUtils.getComponenetByOrder(json);
        for(int i=0;i<taskList.size();i++){
            ComponentInfo componentInfo = componentInfoMapper.selectComponentInfoById(Integer.parseInt(runService.getComponentId(taskList.get(i))));
            if(componentInfo.getIsCustom()==1){
                CustomComponent customComponent = customComponentMapper.selectCustomComponentByFkComponentId(componentInfo.getId());
                if(customComponent.getType()==2){
                    String modelId = customComponent.getSourceId();
                    Model model = modelMapper.selectModelById(Integer.parseInt(modelId));
                    String name = componentInfoMapper.selectComponentInfoById(model.getFkComponentId()).getName();
                    componentInfo.setName(name);
                }
            }
            componentIdName.put(componentInfo.getName(),componentInfo.getId().toString());
        }
        System.out.println("componentIdName:"+componentIdName.toString());
        Gson gson1 = new Gson();
        Map<String,Object> map = gson1.fromJson(experiment.getParamJsonString(),Map.class);
        String config="";
        config = "{\"processInstanceId\":\""
                + experimentJob.getId()
                + "\",\"conversationId\":\""
                + conversationId + "\","
                + "\"component\":"
                + gson1.toJson(componentIdName) +"}";
        map.put("config",config);
        log.info("gson.config=" + map.get("config"));
        log.info("paramMap="+map);
        //在Kubeflow上创建运行
        String jobId = runService.createCycleRun(workflow.getPipelineId(),workflow.getName(),map,apiSchedule);
        //将kubeflow上的runId更新进数据库表中
        experimentJob.setJobId(jobId);
        experimentJobMapper.updateById(experimentJob);
        boolean isSuccess1=experimentJobService.getExperimentJobRunning(46,"JOB", experimentJob);
        if(isSuccess1){
            messageMap.put("quartz(周期定时)",true);
        }
        messageMap.put("isSuccess",true);
        messageMap.put("message","周期运行实验成功");
        messageMap.put("experimentJobId", experimentJob.getId());
        messageMap.put("experimentJobName", experimentJob.getJobName());
        messageMap.put("experimentJobGroupName", experimentJob.getJobGroupName());
        return messageMap;

    }

    @Override
    public boolean getExperimentJobRunning(Integer userId, String type, ExperimentJob experimentJob) {//id:实验id/多任务Id
        JobDataMap jobDataMap = new JobDataMap();
        ApiJobRun apiJobRun=new ApiJobRun();
        ApiJobRunResourceReference apiJobRunResourceReference=new ApiJobRunResourceReference();
        apiJobRunResourceReference.setType(type);
        apiJobRunResourceReference.setId(experimentJob.getJobId());
        apiJobRun.setApiJobRunResourceReference(apiJobRunResourceReference);
        apiJobRun.setPage_size(1000);
        jobDataMap.put("apiJobRun",apiJobRun);
        jobDataMap.put("userId",46);
        jobDataMap.put("experimentId",experimentJob.getFkExperimentId());
        jobDataMap.put("startTime",experimentJob.getStartTime());
        jobDataMap.put("endTime",experimentJob.getEndTime());
        boolean isSuccess=quartzService.addPeriodic(QuartzExperimentRunning.class,
                experimentJob.getJobName(),
                experimentJob.getJobGroupName(),
                jobDataMap,
                Integer.valueOf(experimentJob.getPeriodicJobTime()));
        return isSuccess;
    }

    @Override
    public ExperimentJob selectJobById(Integer id) {
        ExperimentJob experimentJob=experimentJobMapper.selectById(id);
        return experimentJob;
    }

}
