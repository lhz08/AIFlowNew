package com.bdilab.aiflow.service.experiment.impl;

import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.mapper.*;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.ExperimentRunning;
import com.bdilab.aiflow.model.Model;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.component.ComponentOutputStubService;
import com.bdilab.aiflow.service.experiment.ExperimentRunningService;
import com.bdilab.aiflow.service.model.ModelService;
import com.bdilab.aiflow.service.run.RunService;
import com.bdilab.aiflow.vo.ExperimentRunningVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExperimentRunningServiceImpl implements ExperimentRunningService {

    @Autowired
    ExperimentRunningMapper experimentRunningMapper;

    @Autowired
    ExperimentMapper experimentMapper;

    @Autowired
    WorkflowMapper workflowMapper;

    @Autowired
    ModelService modelService;

    @Autowired
    ComponentOutputStubService componentOutputStubService;

    @Autowired
    RunService runService;

    @Override
    public boolean updateExperimentRunning(ExperimentRunning experimentRunning){
        return experimentRunningMapper.updateExperimentRunning(experimentRunning)==1;
    }

    @Override
    public Map<String,Object> deleteExperimentRunning(Integer runningId) throws Exception{
        Map<String,Object> messageMap = new HashMap<>(2);
        ExperimentRunning experimentRunning=experimentRunningMapper.selectExperimentRunningByRunningId(runningId);
        if (experimentRunning.getRunningStatus()!=0){
            //查看该实验运行的isDeleted状态
            if(experimentRunning.getIsDeleted()==DeleteStatus.NOTDELETED.getValue()){
                //进行初步删除，即放入回收站中
                //初步删除该实验运行
                ExperimentRunning experimentRunning1=new ExperimentRunning();
                experimentRunning1.setId(runningId);
                experimentRunning1.setIsDeleted(DeleteStatus.DELETED.getValue());
                boolean isSuccess=experimentRunningMapper.updateExperimentRunning(experimentRunning1)==1;
                if(isSuccess){
                    messageMap.put("isSuccess",true);
                    messageMap.put("message","实验运行删除成功,已放入回收站");
                    return messageMap;
                }
                messageMap.put("isSuccess",false);
                messageMap.put("message","实验删除失败，具体信息：删除实验运行出错");
                return messageMap;
            }else{
                //彻底删除
                //查看该实验运行是否有关联的模型
                List<Model> modellist=modelService.getAllModelByRunningIdAndIsDeleted(runningId,
                        Byte.parseByte(DeleteStatus.NOTDELETED.getValue()+""));
                for(Model model:modellist){
                    //将模型的实验运行外键置空
                    boolean isSuccess=modelService.setRunningIdNull(model.getId());
                    if(!isSuccess){
                        messageMap.put("isSuccess",false);
                        messageMap.put("message","实验运行删除失败,具体信息：在处理该实验运行关联的模型的时候失败");
                        return messageMap;
                    }
                }
                //彻底删除该实验运行的输出桩记录
                boolean isSuccess_Output=componentOutputStubService.deleteOutputByRunningId(runningId);
                //彻底删除该实验运行记录
                boolean isSuccess_Running=experimentRunningMapper.deleteRunningByRunningId(runningId)==1;
                // TODO: 2020/9/25 0025  从kubeflow上删除运行，运行表中应该加kubeflow的运行id字段。
                runService.deleteRunById(experimentRunning.getId().toString());

                if(isSuccess_Output&&isSuccess_Running){
                    messageMap.put("isSuccess",true);
                    messageMap.put("message","实验彻底删除成功,已从回收站移除");
                    return messageMap;
                }
                messageMap.put("isSuccess",false);
                messageMap.put("message","实验彻底删除失败");
                return messageMap;
            }
        }else{
            messageMap.put("isSuccess",false);
            messageMap.put("message","实验删除失败,具体信息：该实验正在运行中，无法进行删除操作");
            return messageMap;
        }

    }

    @Override
    public Map<String, Object> selectRunningByExperimentId(Integer experimentId, Integer isDeleted,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<ExperimentRunning> experimentRunningList=experimentRunningMapper.selectRunningByExperimentIdAndIsDeleted(experimentId,isDeleted);
        PageInfo pageInfo=new PageInfo<> (experimentRunningList);
        Map<String,Object> data=new HashMap<>(3);
        data.put("ExperimentRunning List",experimentRunningList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    @Override
    public Map<String, Object> getDeletedRunning(Integer isDeleted,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<ExperimentRunning> experimentRunningList=experimentRunningMapper.selectAllRunningByisDeleted(isDeleted);
        //获取流程id
        List<Map> list=new ArrayList<>();
        for(ExperimentRunning experimentRunning:experimentRunningList){
            //根据实验id到实验表中获取到流程id
            Experiment experiment=experimentMapper.selectExperimentById(experimentRunning.getFkExperimentId());
            Map<String, Object> map=new HashMap<>(2);
            map.put("Workflowid",experiment.getFkWorkflowId());
            map.put("ExperiemntRunning",experimentRunning);
            list.add(map);
        }
        PageInfo pageInfo=new PageInfo<> (experimentRunningList);
        Map<String,Object> data=new HashMap<>(3);
        data.put("ExperimentRunning List",list);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    @Override
    public boolean restoreExperimentRunning(Integer runningId){
        //查看实验是不是存在
        ExperimentRunning experimentRunning=experimentRunningMapper.selectExperimentRunningByRunningId(runningId);
        Experiment experiment=experimentMapper.selectExperimentById(experimentRunning.getFkExperimentId());
        if(experiment.getIsDeleted()==DeleteStatus.NOTDELETED.getValue()){
            //实验存在
            ExperimentRunning experimentRunning1=new ExperimentRunning();
            experimentRunning1.setId(experimentRunning.getId());
            experimentRunning1.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
            boolean isSuccess=experimentRunningMapper.updateExperimentRunning(experimentRunning1)==1;
            if(!isSuccess){
                return false;
            }
            return true;
        }else{
            //实验不存在
            Integer workflowId=experiment.getFkWorkflowId();
            //首先查看流程的状态
            Workflow workflow = workflowMapper.selectWorkflowById(workflowId);
            if(workflow.getIsDeleted()==Byte.parseByte(DeleteStatus.DELETED.getValue()+"")){
                //流程不存在
                Workflow workflow1=new Workflow();
                workflow1.setId(workflowId);
                workflow1.setIsDeleted(Byte.parseByte(DeleteStatus.NOTDELETED.getValue()+""));
                //还原workflow
                boolean isSuccessWorkflow=workflowMapper.updateWorkflow(workflow1)==1;
                if(!isSuccessWorkflow){
                    return false;
                }
                //还原实验
                Experiment experiment1=new Experiment();
                experiment1.setId(experimentRunning.getFkExperimentId());
                experiment1.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
                boolean isSuccessExperiment=experimentMapper.updateExperiment(experiment1)==1;
                if(!isSuccessExperiment){
                    return false;
                }
                //还原实验运行
                ExperimentRunning experimentRunning1=new ExperimentRunning();
                experimentRunning1.setId(runningId);
                experimentRunning1.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
                boolean isSuccessRunning=experimentRunningMapper.updateExperimentRunning(experimentRunning1)==1;
                if(!isSuccessRunning){
                    return false;
                }
                return true;
            }
            //还原实验
            Experiment experiment1=new Experiment();
            experiment1.setId(experimentRunning.getFkExperimentId());
            experiment1.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
            boolean isSuccessExperiment=experimentMapper.updateExperiment(experiment1)==1;
            if(!isSuccessExperiment){
                return false;
            }
            //还原实验运行
            ExperimentRunning experimentRunning1=new ExperimentRunning();
            experimentRunning1.setId(runningId);
            experimentRunning1.setIsDeleted(DeleteStatus.NOTDELETED.getValue());
            boolean isSuccessRunning=experimentRunningMapper.updateExperimentRunning(experimentRunning1)==1;
            if(!isSuccessRunning){
                return false;
            }
            return true;
        }
    }

    @Override
    public Map<String, Object> getRunningLog(Integer runningId){
        Map<String,Object> logMap=new HashMap<>(3);
        ExperimentRunning experimentRunning=experimentRunningMapper.selectExperimentRunningByRunningId(runningId);
        switch (experimentRunning.getRunningStatus()){
            case 0:
                logMap.put("实验运行状态","运行中");
                break;
            case 1:
                logMap.put("实验运行状态","运行成功");
                break;
            case 2:
                logMap.put("实验运行状态","运行失败");
                break;
        }
        Experiment experiment= experimentMapper.selectExperimentById(experimentRunning.getFkExperimentId());
        Workflow workflow=workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        //组件个数和运行进度，目前没写，需要涉及kubeflow
        logMap.put("组件个数","待开发中");
        logMap.put("运行进度","待开发中");
        return logMap;
    }
    @Override
    public List<ExperimentRunningVO> getExperimentRunning(Integer userId, Integer experimentRunningNum){
        List<ExperimentRunningVO> list = new ArrayList<>();
        List<ExperimentRunning> experimentRunnings = experimentRunningMapper.selectRecentExperimentRunning(userId, experimentRunningNum);
        for (ExperimentRunning experimentRunning:
           experimentRunnings ) {
            ExperimentRunningVO experimentRunningVO = new ExperimentRunningVO();
            experimentRunningVO.setId(experimentRunning.getId());
            experimentRunningVO.setFkUserId(experimentRunning.getFkUserId());
            experimentRunningVO.setRunningStatus(experimentRunning.getRunningStatus());
            experimentRunningVO.setIsDeleted(experimentRunning.getIsDeleted());
            experimentRunningVO.setFkExperimentId(experimentRunning.getFkExperimentId());
            experimentRunningVO.setStartTime(experimentRunning.getStartTime());
            experimentRunningVO.setEndTime(experimentRunning.getEndTime());
            experimentRunningVO.setGgeditorObjectString(workflowMapper.selectWorkflowById(experimentMapper.selectExperimentById(experimentRunning.getFkExperimentId()).getFkWorkflowId()).getGgeditorObjectString());
            list.add(experimentRunningVO);
        }
        return list;
    }
}
