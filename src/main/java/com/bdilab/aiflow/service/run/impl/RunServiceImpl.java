package com.bdilab.aiflow.service.run.impl;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.sse.ProcessSseEmitters;
import com.bdilab.aiflow.common.utils.JsonUtils;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.*;
import com.bdilab.aiflow.model.ComponentOutputStub;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.run.RunService;
import com.google.gson.Gson;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean pushData(String runningId,String taskId,String conversationId,String resultTable,String resultPath){
        Gson gson = new Gson();
        Integer experimentId = experimentRunningMapper.selectExperimentRunningByRunningId(Integer.parseInt(runningId)).getFkExperimentId();
        Experiment experiment = experimentMapper.selectExperimentById(experimentId);
        Workflow workflow = workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        String xmlPath = workflow.getWorkflowXmlAddr();
        String json = gson.toJson(XmlUtils.getPythonParametersMap(xmlPath));
        List<String> taskList = JsonUtils.getComponenetByOrder(json);
        if(!taskId.equals(getComponentId(taskList.get(taskList.size()-1)))) {
            setComponentOutputStub(runningId,taskId,resultPath);
            sendEvent(taskId,conversationId,resultPath);
            return true;
        }
        //将最后一个组件的执行结果存表
        setComponentOutputStub(runningId,taskId,resultPath);
        //推送最后一个组件执行状态
        sendEvent(taskId,conversationId,resultPath);
        logger.info("所有结点执行完毕");
        Map<String,Object> pushFinishData = new HashMap<>();
        pushFinishData.put("processName",workflow.getName());
        pushFinishData.put("status","finished");
        pushFinishData.put("processLogId",runningId);
        ResponseResult responseResult1 = new ResponseResult(true,"004","完成流程："+runningId,pushFinishData);
        //ProcessSseEmitters.sendEvent(conversationId,responseResult1);
        //结束sse会话
        //ProcessSseEmitters.getSseEmitterByKey(conversationId).complete();
        //清除sse对象
        //ProcessSseEmitters.removeSseEmitterByKey(conversationId);
        return true;
    }

    private String getComponentId(String string){
        return string.split("_")[1];
    }

    private void sendEvent(String taskId,String conversationId,String resultPath){
        Map<String,Object> pushData = new HashMap<>();
        String taskName=componentInfoMapper.selectComponentInfoById(Integer.parseInt(taskId)).getName();
        pushData.put("taskName",taskName);
        pushData.put("resultPath",resultPath);
        ResponseResult responseResult = new ResponseResult(true,"001","完成任务："+taskName);
        //ProcessSseEmitters.sendEvent(conversationId,responseResult);
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" 完成任务："+taskName);
    }

    private void setComponentOutputStub(String runningId,String taskId,String resultPath){
        String substring = resultPath.substring(1, resultPath.length() - 1);
        String[] split = substring.split(",");
        for (int i = 0;i<split.length;i++) {
            String substring1 = split[i].trim().substring(1, split[i].length() - 1);
            ComponentOutputStub componentOutputStub = new ComponentOutputStub();
            componentOutputStub.setFkRunningId(Integer.parseInt(runningId));
            componentOutputStub.setFkComponentInfoId(Integer.parseInt(taskId));
            componentOutputStub.setOutputFileAddr(substring1);
            componentOutputStub.setOutputFileType(1);
            componentOutputStub.setOutputTableName(substring1);
            componentOutputStubMapper.insert(componentOutputStub);
        }
    }


}
