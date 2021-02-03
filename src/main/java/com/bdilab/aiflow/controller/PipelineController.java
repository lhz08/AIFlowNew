package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.service.pipeline.PipelineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;

@Api(value = "pipeline控制器",description = "该类用于测试pipeline相关，不被调用，需要使用时调service")
@Controller
@CrossOrigin
public class PipelineController {
    @Autowired
    PipelineService pipelineService;

    @ResponseBody
    @ApiOperation("生成pipeline")
    @RequestMapping(value = "/pipeline/generatePipeline",method = RequestMethod.POST)
    public String generatePipeline(@RequestParam @ApiParam(value = "xml文件地址") String xmlPath,
                                    @RequestParam @ApiParam(value = "流程名") String workflowName,
                                    HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,String> data = pipelineService.generatePipeline(xmlPath,userId);
        return data.toString();
    }

    @ApiOperation("上传Pipeline")
    @ResponseBody
    @RequestMapping(value = "/uploadPipeline",method = RequestMethod.POST)
    public ResponseResult uploadPipeline(String name, String description, File file){

        String result = pipelineService.uploadPipeline(name,description,file);

        ResponseResult responseResult = new ResponseResult();
        if(result.equals(null)){
            responseResult.setMeta(new MetaData(false,"002","上传pipeline失败"));
        }else{
            responseResult.setData(result);
            responseResult.setMeta(new MetaData(true,"001","上传pipeline成功"));
        }
        return responseResult;
    }

    @ApiOperation("根据pipelineId获取对应的Pipeline")
    @ResponseBody
    @RequestMapping(value = "/getPipelineById",method = RequestMethod.GET)
    public ResponseResult getPipelineById(String pipelineId){
        String result = pipelineService.getPipelineById(pipelineId);

        ResponseResult responseResult = new ResponseResult();
        if(result.equals(null)){
            responseResult.setMeta(new MetaData(false,"002","获取pipeline失败"));
        }else{
            responseResult.setData(result);
            responseResult.setMeta(new MetaData(true,"001","成功获取pipeline"));
        }
        return responseResult;
    }

    @ApiOperation("根据pipelineId删除对应的Pipeline")
    @ResponseBody
    @RequestMapping(value = "/deletePipelineById",method = RequestMethod.GET)
    public ResponseResult deletePipelineById(String pipelineId){

        boolean result = pipelineService.deletePipelineById(pipelineId);

        ResponseResult responseResult = new ResponseResult();
        if(result){
            responseResult.setMeta(new MetaData(true,"001","删除pipeline成功"));
        }else{
            responseResult.setMeta(new MetaData(false,"002","删除pipeline失败"));
        }
        return responseResult;
    }
}
