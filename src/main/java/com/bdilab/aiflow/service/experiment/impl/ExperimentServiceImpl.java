package com.bdilab.aiflow.service.experiment.impl;

import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.common.enums.MarkTemplateStatus;
import com.bdilab.aiflow.common.enums.RunningStatus;
import com.bdilab.aiflow.mapper.ExperimentMapper;
import com.bdilab.aiflow.mapper.ExperimentRunningMapper;
import com.bdilab.aiflow.mapper.TemplateMapper;
import com.bdilab.aiflow.mapper.WorkflowMapper;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.ExperimentRunning;
import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.experiment.ExperimentRunningService;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import com.bdilab.aiflow.service.run.RunService;
import com.bdilab.aiflow.service.template.TemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/29 12:07
 * @Version 1.0
 **/

@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    ExperimentMapper experimentMapper;

    @Autowired
    ExperimentRunningMapper experimentRunningMapper;

    @Autowired
    WorkflowMapper workflowMapper;

    @Autowired
    RunService runService;

    @Autowired
    TemplateMapper templateMapper;

    @Autowired
    ExperimentRunningService experimentRunningService;

    @Autowired
    TemplateService templateService;

    @Override
    public Experiment createExperiment(Integer fkWorkflowId, String name, String experimentDesc, String paramJsonString){
        //组装实验
        Experiment experiment=new Experiment();
        experiment.setFkWorkflowId(fkWorkflowId);
        experiment.setName(name);
        experiment.setExperimentDesc(experimentDesc);
        experiment.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
        experiment.setIsMarkTemplate(MarkTemplateStatus.NOTMARKED.getValue());
        experiment.setParamJsonString(paramJsonString);
        experiment.setCreateTime(new Date());
        //将实验信息存入experiment表
        experimentMapper.insertExperiment(experiment);
        return experiment;
    }

    @Override
    public boolean updateExperiment(Experiment experiment){
        //编辑实验
        return experimentMapper.updateExperiment(experiment)==1;
    }

    @Override
    public boolean isRunned(Integer experimentId){
        //查看该实验是否有运行记录(包括在回收站的运行记录)
        List<ExperimentRunning> experimentRunningList= experimentRunningMapper.selectAllExperimentRunningByExperimentId(experimentId);
        if(experimentRunningList.size()!=0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Experiment copyExperiment(Integer experimentId,String name,String experimentDesc){
        Experiment experiment=experimentMapper.selectExperimentById(experimentId);
        experiment.setName(name);
        experiment.setExperimentDesc(experimentDesc);
        experiment.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
        experiment.setIsMarkTemplate(MarkTemplateStatus.NOTMARKED.getValue());
        experiment.setCreateTime(new Date());
        experimentMapper.insertExperiment(experiment);
        return experiment;
    }

    @Override
    public Map<String, Object> getDeletedExperiment(Integer isDeleted,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Experiment> experimentList=experimentMapper.selectAllExperimentByisDeleted(isDeleted);
        PageInfo pageInfo=new PageInfo<> (experimentList);
        Map<String,Object> data=new HashMap<>(3);
        data.put("ExperimentRunning List",experimentList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    @Override
    public Map<String,Object> deleteExperiment(Integer experimentId) throws Exception{
        //首先要考虑实验是否标记成模板
        Experiment experiment=experimentMapper.selectExperimentById(experimentId);
        //该实验是初步删除还是彻底删除
        Map<String,Object> messageMap=new HashMap<>(2);
        if(experiment.getIsDeleted()==DeleteStatus.NOTDELETED.getValue()){
            //将实验运行都进行初步删除
            //获取到该实验id关联的且isDeleted为0的所有实验运行
            List<ExperimentRunning> experimentRunningList=experimentRunningMapper.
                    selectRunningByExperimentIdAndIsDeleted(experimentId,DeleteStatus.NOTDELETED.getValue());
            for(ExperimentRunning experimentRunning:experimentRunningList){
                boolean isSuccess=experimentRunningService.deleteExperimentRunning(experimentRunning.getId()).get("isSuccess").equals(true);
                if(!isSuccess){
                    messageMap.put("isSuccess",false);
                    messageMap.put("message","实验删除失败，在删除实验允许的时候失败");
                    return messageMap;
                }
            }
            //将实验也进行初步删除
            Experiment experiment1=new Experiment();
            experiment1.setId(experimentId);
            experiment1.setIsDeleted(DeleteStatus.DELETED.getValue());
            boolean isSuccess=experimentMapper.updateExperiment(experiment1)==1;
            if(!isSuccess){
                messageMap.put("isSuccess",false);
                messageMap.put("message","实验删除失败,对实验表操作失败");
                return messageMap;
            }
            messageMap.put("isSuccess",true);
            messageMap.put("message","实验删除成功");
            return messageMap;
        }else{
            //彻底删除
            //判断该实验是否关联了模板
            if (experiment.getIsMarkTemplate()==MarkTemplateStatus.MARKED.getValue()){
                //将关联的模板的实验外键置null
                boolean isSuccess=templateService.setRunningIdNull(experimentId);
                if(!isSuccess){
                    messageMap.put("isSuccess",false);
                    messageMap.put("message","实验彻底删除失败,具体信息：在处理该实验关联的模板的时候失败");
                    return messageMap;
                }
            }
            //获取到所有实验运行
            List<ExperimentRunning> experimentRunningList1=experimentRunningMapper.
                    selectAllExperimentRunningByExperimentId(experimentId);
            //删除实验运行（包括组件输出表和数据存储）
            for(ExperimentRunning experimentRunning:experimentRunningList1){
                boolean isSuccess=experimentRunningService.deleteExperimentRunning(experimentRunning.getId()).get("isSuccess").equals(true);
                if(!isSuccess){
                    messageMap.put("isSuccess",false);
                    messageMap.put("message","实验彻底删除失败");
                    return messageMap;
                }
            }
            boolean isSuccess=experimentMapper.deleteExperimentById(experimentId)==1;
            if(!isSuccess){
                messageMap.put("isSuccess",false);
                messageMap.put("message","实验彻底删除失败");
                return messageMap;
            }
            messageMap.put("isSuccess",true);
            messageMap.put("message","实验彻底删除成功");
            return messageMap;
        }
    }

    @Override
    public Map<String,Object> startRunExperment(Integer experimentId){
        Map<String,Object> messageMap=new HashMap<>(2);
        ExperimentRunning experimentRunning=new ExperimentRunning();
        experimentRunning.setRunningStatus(RunningStatus.RUNNING.getValue());
        experimentRunning.setFkExperimentId(experimentId);
        experimentRunning.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
        experimentRunning.setStartTime(new Date());
        boolean isSuccess=experimentRunningMapper.insertExperimentRunning(experimentRunning)==1;
        if(!isSuccess){
            messageMap.put("isSuccess",false);
            messageMap.put("message","运行实验失败，创建试验运行失败");
            return messageMap;
        }

        Experiment experiment = experimentMapper.selectExperimentById(experimentId);
        System.out.println(experiment.getParamJsonString());
        Gson gson = new Gson();
        Map<String,Object> map = gson.fromJson(experiment.getParamJsonString(),Map.class);
        String config = "{\"endpoint\":\"www.cuishaohui.top:9000\",\"access_key\":\"admin\",\"secret_key\":\"admin123456\",\"IP_port\":\"smile.free.idcfengye.com/\",\"resultPath\":\"user2\",\"processInstanceId\":\"1\",\"conversationId\":\"30\",\"component\":{\"mutualInfo\":3,\"knn\":4,\"split_data\":1,\"data_import\":5,\"classification_test\":6}}";
        map.put("config",config);
        System.out.println("gson.config=" + map.get("config"));

        //通知Kubeflow端运行实验
        Workflow workflow = workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        System.out.println(workflow.getId());
        System.out.println(workflow.getPipelineId());
        runService.createRun(workflow.getPipelineId(),workflow.getName(),map);

        messageMap.put("isSuccess",true);
        messageMap.put("message","运行实验成功");
        messageMap.put("experimentRunningId",experimentRunning.getId());
        return messageMap;
    }

    @Override
    public Map<String,Object> stopExperiment(Integer experimentId){
        List<ExperimentRunning> experimentRunningList=experimentRunningMapper.selectAllExperimentRunningByExperimentId(experimentId);
        boolean isRunning=false;
        Map<String,Object> messageMap=new HashMap<>(2);
        for(ExperimentRunning experimentRunning:experimentRunningList){
            if(experimentRunning.getRunningStatus()==RunningStatus.RUNNING.getValue()){
                isRunning=true;
                ExperimentRunning experimentRunning1=new ExperimentRunning();
                experimentRunning1.setId(experimentRunning.getId());
                experimentRunning1.setRunningStatus(RunningStatus.RUNNINGFAIL.getValue());
                boolean isSuccess=experimentRunningMapper.updateExperimentRunning(experimentRunning1)==1;
                //修改完成实验运行表之后，需要通知Kubeflow暂停此次实验运行
                //...目前完成不了


                if(!isSuccess){
                    messageMap.put("isSuccess",false);
                    messageMap.put("message","停止实验失败");
                    return messageMap;
                }
            }
        }
        if(!isRunning){
            messageMap.put("isSuccess",false);
            messageMap.put("message","停止实验失败,该实验未在运行");
            return messageMap;
        }
        messageMap.put("isSuccess",true);
        messageMap.put("message","停止实验成功,通知kubeflow端停止运行还没实现");
        return messageMap;
    }

    @Override
    public boolean restoreExperiment(Integer experimentId){
        //首先要获取流程id
        Experiment experiment=experimentMapper.selectExperimentById(experimentId);
        Integer workflowId=experiment.getFkWorkflowId();
        //首先查看流程的状态
        Workflow workflow = workflowMapper.selectWorkflowById(workflowId);
        if(workflow.getIsDeleted()==Byte.parseByte(DeleteStatus.DELETED.getValue()+"")){
            Workflow workflow1=new Workflow();
            workflow1.setId(workflowId);
            workflow1.setIsDeleted(Byte.parseByte(DeleteStatus.NOTDELETED.getValue()+""));
            //要先还原workflow
            boolean isSuccess=workflowMapper.updateWorkflow(workflow1)==1;
            if(!isSuccess){
                return false;
            }
        }
        Experiment experiment1=new Experiment();
        experiment1.setId(experimentId);
        experiment1.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
        return experimentMapper.updateExperiment(experiment1)==1;
    }

    @Override
    public Template markTemplate(Integer experimentId, String templateName, String templateDesc,String templateTags){
        //对模板表进行插入
        Experiment experiment=experimentMapper.selectExperimentById(experimentId);
        if(experiment.getIsMarkTemplate()==MarkTemplateStatus.MARKED.getValue()){
            return new Template();
        }
        Workflow workflow=workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        Template template=new Template();
        template.setName(templateName);
        template.setTemplateDesc(templateDesc);
        template.setType(1);
        template.setFkUserId(workflow.getFkUserId());
        template.setFkWorkflowId(experiment.getFkWorkflowId());
        template.setFkExperimentId(experimentId);
        template.setTags(templateTags);
        template.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
        template.setWorkflowAddr(workflow.getWorkflowXmlAddr());
        template.setParamJsonString(experiment.getParamJsonString());
        template.setGgeditorObjectString(workflow.getGgeditorObjectString());
        Template template1=templateService.createTemplate(template);
        //修改实验表的is_mark_template属性
        Experiment experiment1=new Experiment();
        experiment1.setId(experimentId);
        experiment1.setIsMarkTemplate(MarkTemplateStatus.MARKED.getValue());
        experimentMapper.updateExperiment(experiment1);
        return template1;
    }
}
