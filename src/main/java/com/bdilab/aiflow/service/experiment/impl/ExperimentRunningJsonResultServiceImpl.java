package com.bdilab.aiflow.service.experiment.impl;

import com.alibaba.fastjson.JSONObject;

import com.bdilab.aiflow.mapper.ExperimentRunningJsonResultMapper;
import com.bdilab.aiflow.model.ExperimentRunningJsonResult;
import com.bdilab.aiflow.service.component.ComponentOutputStubService;
import com.bdilab.aiflow.service.experiment.ExperimentRunningJsonResultService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bdilab.aiflow.common.utils.FileUtils.transResultCsvToJson;

@Service
public class ExperimentRunningJsonResultServiceImpl implements ExperimentRunningJsonResultService {

    @Autowired
    ExperimentRunningJsonResultMapper experimentRunningJsonResultMapper;

    @Autowired
    ComponentOutputStubService componentOutputStubService;

    @Override
    public Map<String,Object> getResultJson(Integer runningId,Integer componentId,Integer type){
        Map<String,Object> messageMap = new HashMap<>(2);
        Map<String,Object> isSuccess=componentOutputStubService.getOutputFileAddr(runningId, componentId,null);

        if(isSuccess.get("isSuccess").equals(false)){
            messageMap=isSuccess;
            return messageMap;
        }
        JSONObject jsonObject=(JSONObject) transResultCsvToJson(isSuccess.get("outputFileAddr").toString(),type);
        messageMap.put("isSuccess", true);
        messageMap.put("data",jsonObject);
        messageMap.put("message", "转换Json结果成功");

        return messageMap;
    }

    @Override
    public ExperimentRunningJsonResult createExperimentRunningJsonResult(Integer fkExperimentRunningId,
                                                                         String resultJsonString){
        ExperimentRunningJsonResult experimentRunningJsonResult = new ExperimentRunningJsonResult();
        experimentRunningJsonResult.setFkExperimentRunningId(fkExperimentRunningId);
        experimentRunningJsonResult.setResultJsonString(resultJsonString);
        experimentRunningJsonResult.setCreateTime(new Date());
        experimentRunningJsonResultMapper.insertExperimentRunningJsonResult(experimentRunningJsonResult);
        return experimentRunningJsonResult;
    }



    @Override
    public List<ExperimentRunningJsonResult> getExperimentRunningJsonResultByRunningId(Integer experimentRunningId){
        return experimentRunningJsonResultMapper.selectExperimentRunningJsonResultByExperimentRunningId(experimentRunningId);
    }


    /*###以下方法勿删###*/
    @Override
    public ExperimentRunningJsonResult createExperimentRunningJsonResult(Integer fkExperimentRunningId, Integer fkComponentInfoId, String mapConfigString ,String resultJsonString) {
        ExperimentRunningJsonResult experimentRunningJsonResult = new ExperimentRunningJsonResult();
        experimentRunningJsonResult.setFkExperimentRunningId(fkExperimentRunningId);
        experimentRunningJsonResult.setFkComponentInfoId(fkComponentInfoId);
        experimentRunningJsonResult.setMapConfigString(mapConfigString);
        experimentRunningJsonResult.setResultJsonString(resultJsonString);
        experimentRunningJsonResult.setCreateTime(new Date());
        experimentRunningJsonResultMapper.insertExperimentRunningJsonResult(experimentRunningJsonResult);
        return experimentRunningJsonResult;
    }
    @Override
    public boolean updateExperimentRunningJsonResult(ExperimentRunningJsonResult experimentRunningJsonResult){
        return experimentRunningJsonResultMapper.updateExperimentRunningJsonResult(experimentRunningJsonResult)==1;
    }

    @Override
    public ExperimentRunningJsonResult getExperimentRunningJsonResultById(Integer experimentRunningJsonResultId){
        return experimentRunningJsonResultMapper.
                selectExperimentRunningJsonResultByExperimentRunningJsonResultId(experimentRunningJsonResultId);
    }

    @Override
    public ExperimentRunningJsonResult selectExperimentRunningJsonResultByExperimentRunningIdAndComponentInfoId(Integer fkExperimentRunningId, Integer fkComponentInfoId) {
        return experimentRunningJsonResultMapper.
                selectExperimentRunningJsonResultByExperimentRunningIdAndComponentInfoId(fkExperimentRunningId,fkComponentInfoId);
    }

    @Override
    public boolean deleteExperimentRunningJsonResult(Integer experimentRunningJsonResultId) {
        return experimentRunningJsonResultMapper.
                deleteExperimentRunningJsonResultByExperimentRunningJsonResultId(experimentRunningJsonResultId) == 1;
    }
}
