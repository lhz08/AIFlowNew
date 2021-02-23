package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.workflow.EpochInfo;
import com.bdilab.aiflow.service.deeplearning.workflow.DlWorkflowService;
import com.bdilab.aiflow.service.run.RunService;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author smile
 * @data 2020/9/12 13:37
 **/
@Api(value = "流程运行控制器")
@Controller
@CrossOrigin
public class RunController {

    @Autowired
    RunService runService;

    @Autowired
    DlWorkflowService dlWorkflowService;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @ResponseBody
    @ApiOperation("python调用该接口，报告执行结果以及结果文件地址")
    @RequestMapping(value = "/process/executeTask",method = RequestMethod.POST)
    public ResponseResult executeTask(@RequestParam @ApiParam(value = "processInstanceId") String processInstanceId,
                                      @RequestParam @ApiParam(value = "taskId") String taskId,
                                      @RequestParam @ApiParam(value = "conversationId") String conversationId,
                                      @RequestParam(required = false,defaultValue = "") @ApiParam(value = "IP_port") String IP_port,
                                      @RequestParam(required = false,defaultValue = "") @ApiParam(value = "resultPath")String resultPath,
                                      @RequestParam(required = false,defaultValue = "") @ApiParam(value = "resultTable") String resultTable){
        boolean isInProcess = runService.pushData(processInstanceId,taskId,conversationId,resultTable);
        System.out.println(processInstanceId+" "+taskId+" "+conversationId+resultPath);
        if(isInProcess){
            return new ResponseResult(true,"001","已完成id为"+taskId+"的任务");
        }
        return new ResponseResult(false,"002","执行任务失败，当前流程已被暂停或中止");

    }
    @ResponseBody
    @ApiOperation("python端报告深度学习流程每次迭代信息")
    @RequestMapping(value = "/dlProcess/python/reportEpochInfo",method = RequestMethod.POST)
    public ResponseResult reportEpochInfo(@RequestParam @ApiParam(value = "processLogId") String processLogId,
                                          @RequestParam @ApiParam(value = "epochInfoJsonString") String epochInfoJsonString,
                                          @RequestParam @ApiParam(value = "modelFilePath") String modelFilePath,
                                          @RequestParam @ApiParam(value = "conversationId") String conversationId
                                          ){
        System.out.println("----------");
        System.out.println("epoch: "+processLogId+" "+epochInfoJsonString+" "+modelFilePath+" "+conversationId);
        Gson gson = new Gson();
        logger.info(epochInfoJsonString);
        EpochInfo epochInfo = gson.fromJson(epochInfoJsonString,EpochInfo.class);
        runService.pushEpochInfo(processLogId,epochInfo,modelFilePath,conversationId);
        return new ResponseResult(true,"001","成功报告迭代信息");
    }


    /*测试使用，不用调用*/
    @ApiOperation(value = "创建运行")
    @ResponseBody
    @RequestMapping(value = "/createRun",method = RequestMethod.POST)
    public ResponseResult createRun(String pipelineId,String pipelineName){

        Map<String,Object> parameter = new HashMap<>();
        parameter.put("input_data","dataset/IrisFS.csv");
        parameter.put("val_portion","0.2");
        parameter.put("resultPath","admin");

        String result = runService.createRun(pipelineId,pipelineName,parameter);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(result);
        responseResult.setMeta(new MetaData(true,"001","成功创建运行"));
        return responseResult;
    }

    @ApiOperation(value = "根据运行id删除对应的运行")
    @ResponseBody
    @RequestMapping(value = "/deleteRunById",method = RequestMethod.POST)
    public ResponseResult deleteRunById(String runId){
        boolean result = runService.deleteRunById(runId);
        ResponseResult responseResult = new ResponseResult();
        if (result){
            responseResult.setMeta(new MetaData(true,"001","成功删除运行"));
        }else {
            responseResult.setMeta(new MetaData(true,"001","删除运行失败"));
        }
        return responseResult;
    }
}
