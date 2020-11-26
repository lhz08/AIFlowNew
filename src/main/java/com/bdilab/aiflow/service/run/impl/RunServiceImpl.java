package com.bdilab.aiflow.service.run.impl;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.enums.RunningStatus;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.sse.ProcessSseEmitters;
import com.bdilab.aiflow.common.utils.JsonUtils;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.*;
import com.bdilab.aiflow.model.ComponentOutputStub;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.ExperimentRunning;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.model.run.ApiParameter;
import com.bdilab.aiflow.model.run.ApiPipelineSpec;
import com.bdilab.aiflow.model.run.ApiRun;
import com.bdilab.aiflow.service.run.RunService;
import com.google.gson.Gson;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.google.gson.Gson;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

/**
 * @author smile
 * @data 2020/9/21 10:14
 **/
@Service
public class RunServiceImpl implements RunService {

    @Resource
    ComponentInfoMapper componentInfoMapper;
    @Resource
    ExperimentRunningMapper experimentRunningMapper;
    @Resource
    ExperimentMapper experimentMapper;
    @Resource
    WorkflowMapper workflowMapper;
    @Resource
    ComponentOutputStubMapper componentOutputStubMapper;
    @Resource
    FilePathConfig filePathConfig;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RestTemplate restTemplate;

    @Override
    public boolean pushData(String runningId,String taskId,String conversationId,String resultTable) {
        Gson gson = new Gson();
        Integer experimentId = experimentRunningMapper.selectExperimentRunningByRunningId(Integer.parseInt(runningId)).getFkExperimentId();
        Experiment experiment = experimentMapper.selectExperimentById(experimentId);
        Workflow workflow = workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        String xmlPath = workflow.getWorkflowXmlAddr();


        String json = gson.toJson(XmlUtils.getPythonParametersMap(xmlPath));
        List<String> taskList = JsonUtils.getComponenetByOrder(json);

        if(!taskId.equals(getComponentId(taskList.get(taskList.size()-1)))) {
            System.out.println(resultTable);
            setComponentOutputStub(runningId, taskId, resultTable);
            if(!sendEvent(taskId, conversationId,getNextNode(taskId,taskList,json))) {
                return false;
            }
            return true;
        }
        //将最后一个组件的执行结果存表
        setComponentOutputStub(runningId, taskId, resultTable);
        //推送最后一个组件执行状态
        sendEvent(taskId,conversationId,null);
        logger.info("所有结点执行完毕");
        Map<String, Object> pushFinishData = new HashMap<>();
        pushFinishData.put("processName", workflow.getName());
        pushFinishData.put("status", "finished");
        pushFinishData.put("processLogId", runningId);
        ResponseResult responseResult1 = new ResponseResult(true, "004", "完成流程：",pushFinishData);
        ProcessSseEmitters.sendEvent(conversationId,responseResult1);
       // 结束sse会话
        ProcessSseEmitters.getSseEmitterByKey(conversationId).complete();
       // 清除sse对象
        ProcessSseEmitters.removeSseEmitterByKey(conversationId);
        ExperimentRunning experimentRunning = new ExperimentRunning();
        experimentRunning.setId(Integer.parseInt(runningId));
        experimentRunning.setRunningStatus(RunningStatus.RUNNINGSUCCESS.getValue());
        experimentRunning.setEndTime(new Date());
        experimentRunningMapper.updateExperimentRunning(experimentRunning);
        return true;

    }
    public String getComponentId(String string){
        return string.split("_")[1];
    }


    private boolean sendEvent(String taskId,String conversationId,List<String> nextTaskId){
        Map<String,Object> pushData = new HashMap<>();
        String taskName=componentInfoMapper.selectComponentInfoById(Integer.parseInt(taskId)).getName();
        List<String> nextTaskName = null;
        if(nextTaskId!=null) {
            nextTaskName = new ArrayList<>(nextTaskId.size()) ;
            for(int i =0;i<nextTaskId.size();i++) {
                nextTaskName.add(i,componentInfoMapper.selectComponentInfoById(Integer.parseInt(getComponentId(nextTaskId.get(i)))).getComponentDesc());
            }
        }
        pushData.put("taskName",taskName);
        pushData.put("nextTask",nextTaskName);
        ResponseResult responseResult = new ResponseResult(true,"001","完成任务：",pushData);
        ProcessSseEmitters.sendEvent(conversationId,responseResult);
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" 完成任务："+taskName);
        return true;
    }

    private List<String> getNextNode(String curComponeneId,List<String> taskList,String json){
//        for(int i = 0 ;i<taskList.size();i++){
//            if (getComponentId(taskList.get(i)).equals(curComponeneId)) {
//                taskList = taskList.subList(i, taskList.size());
//                System.out.println(componentInfoMapper.selectComponentInfoById(Integer.parseInt(curComponeneId)).getName()+"  asf"+taskList.toString());
//                break;
//            }
//        }
//        return getComponentId(taskList.get(1));
        for (String s:taskList
        ) {
            if (getComponentId(s).equals(curComponeneId)){
                curComponeneId = s;
            }
        }
        return JsonUtils.getRearNodeList(curComponeneId,json);
    }


    private void setComponentOutputStub(String runningId,String taskId,String resultTable){
        Gson gson = new Gson();
        List<Map<String,String>> resultTableList = gson.fromJson(resultTable,List.class);
        for (Map<String,String> map : resultTableList){
            ComponentOutputStub componentOutputStub = new ComponentOutputStub();
            componentOutputStub.setFkRunningId(Integer.parseInt(runningId));
            componentOutputStub.setFkComponentInfoId(Integer.parseInt(taskId));
            componentOutputStub.setOutputFileAddr(map.get("result_path").replace("'",""));
            componentOutputStub.setOutputFileType(map.get("result_type"));
            componentOutputStub.setOutputTableName(map.get("result_path").replace("'",""));
            if(map.containsKey("graph_type")) {
                componentOutputStub.setGraphType(Integer.parseInt(map.get("graph_type").trim()));
            }else {
                componentOutputStub.setGraphType(0);
            }
            componentOutputStubMapper.insert(componentOutputStub);
        }
    }
    @Override
    public String createRun(String pipelineId, String pipelineName, Map<String,Object> parameter) {
        ApiRun apiRun = new ApiRun();
        ApiPipelineSpec apiPipelineSpec = new ApiPipelineSpec();
        int paramLength = parameter.size();
        Object[] parameters = new Object[paramLength];
        int i = 0;
        for(Map.Entry<String,Object> entry:parameter.entrySet()){
            ApiParameter apiParameter = new ApiParameter();
            apiParameter.setName(entry.getKey());
            apiParameter.setValue(entry.getValue().toString());
            parameters[i] = apiParameter;
            i++;
        }
        apiRun.setName("run");
        apiRun.setDescription("desc");
        apiPipelineSpec.setPipelineId(pipelineId);
        apiPipelineSpec.setPipelineName(pipelineName);
        apiPipelineSpec.setParameters(parameters);
        apiRun.setPipeline_spec(apiPipelineSpec);

        Gson gson = new Gson();
        String json = gson.toJson(apiRun);
        System.out.println(json);

        String url = "http://120.27.69.55:31380/pipeline/apis/v1beta1/runs";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ApiRun> request = new HttpEntity<>(apiRun,headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url,request,String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue == 200){
            return responseEntity.getBody();
        }
        return null;
    }

    @Override
    public boolean deleteRunById(String runId) {
        String url = "http://120.27.69.55:31380/pipeline/apis/v1beta1/runs/" + runId;
        restTemplate.delete(url);
        return true;
    }


}
