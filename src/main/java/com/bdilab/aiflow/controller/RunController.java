package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.service.run.RunService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @ResponseBody
    @ApiOperation("python调用该接口，报告执行结果以及结果文件地址")
    @RequestMapping(value = "/process/executeTask",method = RequestMethod.POST)
    public ResponseResult executeTask(@RequestParam @ApiParam(value = "processInstanceId") String processInstanceId,
                                      @RequestParam @ApiParam(value = "taskId") String taskId,
                                      @RequestParam @ApiParam(value = "conversationId") String conversationId,
                                      @RequestParam @ApiParam(value = "IP_port") String IP_port,
                                      @RequestParam @ApiParam(value = "resultPath")String resultPath,
                                      @RequestParam(required = false,defaultValue = "") @ApiParam(value = "resultTable") String resultTable){
        boolean isInProcess = runService.pushData(processInstanceId,taskId,conversationId,resultTable,resultPath);
        if(isInProcess){
            return new ResponseResult(true,"001","已完成id为"+taskId+"的任务");
        }
        return new ResponseResult(false,"002","执行任务失败，当前流程已被暂停或中止");
//        System.out.println(processInstanceId+" "+taskId+" "+conversationId+resultPath);
    }

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


}
