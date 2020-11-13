package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.service.component.ComponentOutputStubService;
import com.bdilab.aiflow.service.experiment.ExperimentRunningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
/**
 * @Decription ExperimentController，用于处理实验运行相关内容。
 * @Author Jin Lingming
 * @Date 2020/0901 12:07
 * @Version 1.0
 **/

@Controller
@CrossOrigin
@Api(value="实验运行Controller")
public class ExperimentRunningController {

    @Autowired
    ExperimentRunningService experimentRunningService;

    @Autowired
    ComponentOutputStubService componentOutputStubService;


    @ResponseBody
    @ApiOperation("单个或批量删除实验运行")
    @RequestMapping(value = "/experimentRunning/deleteExperimentRunning", method = RequestMethod.POST)
    public  ResponseResult deleteExperimentRunning(@RequestParam @ApiParam(value = "实验运行id") String runningIds,
                                          HttpSession httpSession) throws Exception{
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        String[] ids = runningIds.split(",");
        Map<String,Object> isSuccess;
        for (int i=0;i<ids.length;i++){
            isSuccess = experimentRunningService.deleteExperimentRunning(Integer.parseInt(ids[i]));
            if (isSuccess.get("isSuccess").equals(false)){
                return new ResponseResult(false,"002",isSuccess.get("message").toString());
            }
        }
        return new ResponseResult(true,"001","删除实验运行成功");
    }

    @ResponseBody
    @ApiOperation("管理页面分页获取实验运行")
    @RequestMapping(value = "/experimentRunning/getExperimentRunning", method = RequestMethod.POST)
    public  ResponseResult getExperimentRunning(@RequestParam @ApiParam(value = "实验id") Integer experimentId,
                                                @RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
                                                   HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());

        Map<String,Object> data=experimentRunningService.selectRunningByExperimentId(
                experimentId, DeleteStatus.NOTDELETED.getValue(),pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取所有实验运行列表"));
        return responseResult;

    }

    @ResponseBody
    @ApiOperation("回收站查看实验运行")
    @RequestMapping(value = "/experimentRunning/getDeletedExperimentRunning", method = RequestMethod.POST)
    public  ResponseResult deleteExperimentRunning(@RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                   @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
                                                   HttpSession httpSession) throws Exception{
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data=experimentRunningService.getDeletedRunning(DeleteStatus.DELETED.getValue(),pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取回收站中的所有实验运行列表"));
        return responseResult;

    }

    @ResponseBody
    @ApiOperation("从回收站单个或批量还原实验运行")
    @RequestMapping(value = "/experimentRunning/restoreExperimentRunning", method = RequestMethod.POST)
    public  ResponseResult restoreExperimentRunning(@RequestParam @ApiParam(value = "实验运行id") String runningIds,
                                                   HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        String[] ids = runningIds.split(",");
        boolean isSuccess;
        for (int i=0;i<ids.length;i++){
            isSuccess = experimentRunningService.restoreExperimentRunning(Integer.parseInt(ids[i]));
            if (!isSuccess){
                return new ResponseResult(false,"002","还原实验运行失败");
            }
        }
        return new ResponseResult(true,"001","还原实验运行成功");
    }

    @ResponseBody
    @ApiOperation("查看实验运行日志")
    @RequestMapping(value = "/experimentRunning/getExperimentRunningLog", method = RequestMethod.POST)
    public  ResponseResult getExperimentRunningLog(@RequestParam @ApiParam(value = "实验运行id") Integer runningId,
                                                   HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data=experimentRunningService.getRunningLog(runningId);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取回实验运行日志"));
        return responseResult;
    }

    @ResponseBody
    @ApiOperation("获取最近创建的运行")
    @RequestMapping(value = "/experimentRunning/getExperimentRunning", method = RequestMethod.GET)
    public ResponseResult getExperiment(@RequestParam @ApiParam(value = "实验运行条数") Integer experimentRunningNum,
                                        HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());

        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(experimentRunningService.getExperimentRunning(userId,experimentRunningNum));
        responseResult.setMeta(new MetaData(true,"001","成功获取最近创建实验运行列表"));
        return  responseResult;
    }

}
