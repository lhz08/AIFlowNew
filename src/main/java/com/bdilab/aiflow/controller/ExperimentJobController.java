package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.job.ExperimentJob;
import com.bdilab.aiflow.service.job.ExperimentJobService;
import com.bdilab.aiflow.service.quartz.QuartzService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin
@Api(value="实验运行任务Controller")
public class ExperimentJobController {

    @Autowired
    ExperimentJobService experimentJobService;
    @Autowired
    QuartzService quartzService;

    @ResponseBody
    @ApiOperation("停止实验周期任务")
    @RequestMapping(value = "/experimentJob/stopExperimentJob", method = RequestMethod.POST)
    public ResponseResult stopExperimentJob(@RequestParam @ApiParam(value = "实验运行任务id") Integer JobId,
                                                HttpSession httpSession){
       // Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        ExperimentJob experimentJob=experimentJobService.stopExperienceJob(JobId);
        boolean isSuccess=quartzService.pauseJob(experimentJob.getJobName(),experimentJob.getJobGroupName());
        if(isSuccess){
            return new ResponseResult(true,"001","停止job成功");
        }
        System.out.println("");
        return new ResponseResult(false,"002","停止job失败");

    }
    @ResponseBody
    @ApiOperation("启动实验周期任务")
    @RequestMapping(value = "/experimentJob/startExperimentJob", method = RequestMethod.POST)
    public ResponseResult startExperimentJob(@RequestParam @ApiParam(value = "实验运行任务id") Integer JobId,
                                            HttpSession httpSession){
        // Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        ExperimentJob experimentJob=experimentJobService.startExperienceJob(JobId);
        boolean isSuccess=quartzService.resumeJob(experimentJob.getJobName(),experimentJob.getJobGroupName());
        if(isSuccess){
            return new ResponseResult(true,"001","启动job成功");
        }
        return new ResponseResult(false,"002","启动job失败");
    }
}
