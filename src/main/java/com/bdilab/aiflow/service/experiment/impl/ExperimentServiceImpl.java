package com.bdilab.aiflow.service.experiment.impl;

import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.common.enums.MarkTemplateStatus;
import com.bdilab.aiflow.common.enums.RunningStatus;
import com.bdilab.aiflow.common.utils.JsonUtils;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.*;
import com.bdilab.aiflow.model.*;
import com.bdilab.aiflow.service.experiment.ExperimentRunningService;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import com.bdilab.aiflow.service.run.RunService;
import com.bdilab.aiflow.service.template.TemplateService;
import com.bdilab.aiflow.vo.ExperimentVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/29 12:07
 * @Version 1.0
 **/

@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Resource
    ExperimentMapper experimentMapper;

    @Resource
    ExperimentRunningMapper experimentRunningMapper;

    @Resource
    WorkflowMapper workflowMapper;

    @Autowired
    RunService runService;

    @Resource
    TemplateMapper templateMapper;

    @Autowired
    ExperimentRunningService experimentRunningService;

    @Autowired
    TemplateService templateService;

    @Autowired
    ComponentInfoMapper componentInfoMapper;

    @Resource
    CustomComponentMapper customComponentMapper;

    @Resource
    ModelMapper modelMapper;

    @Resource
    ExperimentRunningJsonResultMapper experimentRunningJsonResultMapper;

    @Value("${web.address}")
    private String webAddress;


    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public Experiment createExperiment(Integer fkWorkflowId, String name, Integer userId,String experimentDesc, String paramJsonString){
        //组装实验
        Experiment experiment=new Experiment();
        experiment.setFkWorkflowId(fkWorkflowId);
        experiment.setName(name);
        experiment.setFkUserId(userId);
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
    public Experiment copyExperiment(Integer userId,Integer experimentId,String name,String experimentDesc){
        Experiment experiment=experimentMapper.selectExperimentById(experimentId);
        Experiment newExperiment = new Experiment();
        newExperiment.setName(name);
        newExperiment.setFkUserId(userId);
        newExperiment.setFkWorkflowId(experiment.getFkWorkflowId());
        newExperiment.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
        newExperiment.setParamJsonString(experiment.getParamJsonString());
        newExperiment.setIsMarkTemplate(MarkTemplateStatus.NOTMARKED.getValue());
        newExperiment.setExperimentDesc(experimentDesc);
        newExperiment.setCreateTime(new Date());
        experimentMapper.insertExperiment(newExperiment);
        return newExperiment;
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
                boolean isSuccess=templateService.setExperimentIdNull(experimentId);
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
                //删除kubeflow上的运行
                runService.deleteRunById(experimentRunning.getRunId());
                //彻底删除运行表中的记录
                boolean isSuccess= experimentRunningMapper.deleteRunningByRunningId(experimentRunning.getId())==1;
                if(!isSuccess){
                    messageMap.put("isSuccess",false);
                    messageMap.put("message","实验运行彻底删除失败");
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
    public Map<String,Object> startRunExperment(Integer experimentId,Integer userId,String conversationId){
        Map<String,Object> messageMap=new HashMap<>(2);
        //封装ExperimentRunning
        ExperimentRunning experimentRunning=new ExperimentRunning();
        experimentRunning.setRunningStatus(RunningStatus.RUNNING.getValue());
        experimentRunning.setFkExperimentId(experimentId);
        experimentRunning.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
        experimentRunning.setStartTime(new Date());
        experimentRunning.setFkUserId(userId);
        experimentRunning.setRunId("");
        experimentRunning.setConversationId(conversationId);
        boolean isSuccess=experimentRunningMapper.insertExperimentRunning(experimentRunning)==1;
        if(!isSuccess){
            messageMap.put("isSuccess",false);
            messageMap.put("message","运行实验失败，创建试验运行失败");
            return messageMap;
        }

        Experiment experiment = experimentMapper.selectExperimentById(experimentId);
        System.out.println(experiment.getParamJsonString());

        Gson gson = new Gson();
        Map<String,String> componentIdName = new LinkedHashMap<>();
        Workflow workflow = workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        String xmlPath = workflow.getWorkflowXmlAddr();
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
        /*if(workflow.getIsMl()==1) {
            config = "{\"endpoint\":\"" + minioHost.replace("http://", "") + "\",\"access_key\":\"" + minioAccessKey + "\",\"secret_key\":\"" + minioSecretKey + "\",\"IP_port\":\"" + webAddress + "\",\"resultPath\":" + "\"user" + userId + "\",\"processInstanceId\":\"" + experimentRunning.getId() + "\",\"conversationId\":\"" + conversationId + "\",";
        }else {*/
            config = "{\"processInstanceId\":\"" + experimentRunning.getId()+ "\",\"conversationId\":\"" + conversationId + "\",";
        //}
        config = config + "\"component\":" +gson1.toJson(componentIdName) +"}";
        //"component":{"mutualInfo":3,"knn":4,"split_data":1,"data_import":5,"classification_test":6}
        map.put("config",config);
        logger.info("gson.config=" + map.get("config"));
        logger.info("paramMap="+map);
        //在Kubeflow上创建运行
        String runId = runService.createRun(workflow.getPipelineId(),workflow.getName(),map);
        //将kubeflow上的runId更新进数据库表中
        experimentRunning.setRunId(runId);
        experimentRunningMapper.updateExperimentRunning(experimentRunning);

        //4.30修改了创建时机
//        //新建对应的experiment_running_json_result表项，用来保存前端图表结果
//        ExperimentRunningJsonResult experimentRunningJsonResult = new ExperimentRunningJsonResult();
//        experimentRunningJsonResult.setFkExperimentRunningId(experimentRunning.getId());
//        experimentRunningJsonResult.setCreateTime(new Date());
//        experimentRunningJsonResultMapper.insertExperimentRunningJsonResult(experimentRunningJsonResult);


        messageMap.put("isSuccess",true);
        messageMap.put("message","运行实验成功");
        messageMap.put("experimentRunningId",experimentRunning.getId());
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
    public List<ExperimentVO> getExperiment( Integer userId, Integer experimentNum){
        List<Experiment> experiments = experimentMapper.selectRecentExperiment(userId, experimentNum);
        List<ExperimentVO> list = new ArrayList<>();
        for(int i=0;i<experiments.size();i++){
            Experiment experiment = experiments.get(i);
            ExperimentVO experimentVO = new ExperimentVO();
            experimentVO.setId(experiment.getId());
            experimentVO.setName(experiment.getName());
            experimentVO.setFkUserId(experiment.getFkUserId());
            experimentVO.setFkWorkflowId(experiment.getFkWorkflowId());
            experimentVO.setIsDeleted(experiment.getIsDeleted());
            experimentVO.setParamJsonString(experiment.getParamJsonString());
            experimentVO.setIsMarkTemplate(experiment.getIsMarkTemplate());
            experimentVO.setExperimentDesc(experiment.getExperimentDesc());
            experimentVO.setCreateTime(experiment.getCreateTime());
            experimentVO.setGgeditorObjectString(workflowMapper.selectWorkflowById(experiment.getFkWorkflowId()).getGgeditorObjectString());
            list.add(experimentVO);
        }
        return list;
    }

    @Override
    public Map<Integer,String> isEdit(Integer experimentId) {
        //判断一个实验是否可编辑，需要判断是否存在与该实验关联的运行和模板（包括回收站中的运行和模板），如果有,则不能修改。
        //原因：1、运行是基于实验中的参数去运行的，如果实验进行了更改，那之前存在的运行就会不一致。
        // 2、模板中的param串是从实验中拷贝过来的，如果实验修改了，那就不统一了。
        Map<Integer,String> result = new HashMap<>();
        //在运行表中查看，是否存在experimentId的运行
        List<ExperimentRunning> runningList = experimentRunningMapper.selectAllExperimentRunningByExperimentId(experimentId);
        //在模板表中查看，是否存在与experimentId关联的模板
        List<Integer> templateIdList = templateMapper.selectTemplateByExperimentId(experimentId);
        int size1 = runningList.size();
        int size2 = templateIdList.size();

        if (size1 == 0 && size2 == 0){
            result.put(0,"该实验不存在与之关联的运行和模板，可以编辑");
        }
        if (size1 != 0 && size2 == 0){
            result.put(1,"该实验存在与之关联的运行，不能编辑");
        }
        if (size1 == 0 && size2 != 0){
            result.put(2,"该实验存在与之关联的模板，不能编辑");
        }
        if (size1 != 0 && size2 != 0){
            result.put(3,"该实验存在与之关联的运行和模板，不能编辑");
        }
        return result;
    }
    @Override
    public Integer getWorkflowId(Integer experimentId){
        return experimentMapper.selectWorkflowByExperimentId(experimentId);
    }

    @Override
    public Experiment getExperimentInfo(Integer experimentId){
        return  experimentMapper.selectExperimentById(experimentId);
    }
}
