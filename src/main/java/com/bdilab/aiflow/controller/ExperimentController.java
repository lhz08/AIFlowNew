package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.common.enums.MarkTemplateStatus;
import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @Decription ExperimentController，用于处理实验相关内容。
 * @Author Jin Lingming
 * @Date 2020/08/29 12:07
 * @Version 1.0
 **/

@Controller
@CrossOrigin
@Api(value="实验Controller")
public class ExperimentController {

    @Autowired
    ExperimentService experimentService;

    @ResponseBody
    @ApiOperation("创建实验")
    @RequestMapping(value = "/experiment/createExperiment", method = RequestMethod.POST)
    public ResponseResult createExperiment(@RequestParam @ApiParam(value = "流程id") Integer fkWorkflowId,
                                           @RequestParam @ApiParam(value = "实验名") String name,
                                           @RequestParam @ApiParam(value = "实验描述") String experimentDesc,
                                           @RequestParam @ApiParam(value = "参数json串") String paramJsonString,
                                           HttpSession httpSession) throws Exception{
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        try {
            //处理为空串的参数，修改为NULL
            if(name!=null&&name.length()==0){
                name=null;
            }
            if(experimentDesc!=null&&experimentDesc.length()==0){
                experimentDesc=null;
            }
            //创建实验
            Experiment experiment=experimentService.createExperiment(fkWorkflowId,name,userId,experimentDesc,paramJsonString);
            Map<String,Object> data=new HashMap<>(1);
            data.put("experimentId",experiment.getId());
            ResponseResult responseResult = new ResponseResult(true,"001","实验创建成功");
            responseResult.setData(data);
            return responseResult;
        }catch (Exception e){
            ResponseResult responseResult = new ResponseResult(false,"002","实验创建失败,具体信息："+e.getMessage());
            return responseResult;
        }
    }

    @ResponseBody
    @ApiOperation("编辑实验")
    @RequestMapping(value = "/experiment/editExperiment", method = RequestMethod.POST)
    public ResponseResult editExperiment(@RequestParam @ApiParam(value = "实验id") Integer experimentId,
                                         @RequestParam(required = false)@ApiParam(value = "实验名") String name,
                                         @RequestParam(required = false) @ApiParam(value = "实验描述") String experimentDesc,
                                         @RequestParam(required = false) @ApiParam(value = "实验参数") String paramJsonString,
                                         HttpSession httpSession) throws Exception{
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if (paramJsonString==null){
            //编辑实验信息
            //处理为空串的参数，修改为NULL
            if(name!=null&&name.length()==0){
                name=null;
            }
            Experiment experiment=new Experiment();
            experiment.setId(experimentId);
            experiment.setName(name);
            experiment.setExperimentDesc(experimentDesc);
            boolean isSuccess = experimentService.updateExperiment(experiment);
            if(isSuccess){
                return new ResponseResult(true,"001","实验编辑成功");
            }
            return new ResponseResult(false,"002","实验编辑失败");
        }else{
            //配置实验参数
            if(!experimentService.isRunned(experimentId)){
                Experiment experiment=new Experiment();
                experiment.setId(experimentId);
                experiment.setParamJsonString(paramJsonString);
                boolean isSuccess = experimentService.updateExperiment(experiment);
                if(isSuccess){
                    return new ResponseResult(true,"001","配置实验参数成功");
                }
                return new ResponseResult(false,"002","配置实验参数失败");
            }else{
                return new ResponseResult(false,"002","配置实验参数失败，具体信息：实验已运行过，不再支持修改实验参数！");
            }

        }

    }

    @ResponseBody
    @ApiOperation("克隆实验")
    @RequestMapping(value = "/experiment/copyExperiment", method = RequestMethod.POST)
    public  ResponseResult copyExperiment(@RequestParam @ApiParam(value = "实验id") Integer experimentId,
                                          @RequestParam @ApiParam(value = "实验名称") String name,
                                          @RequestParam(required = false) @ApiParam(value = "实验描述") String experimentDesc,
                                          HttpSession httpSession) throws Exception{
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        try{
            //处理为空串的参数，修改为NULL
            if(name!=null&&name.length()==0){
                name=null;
            }
            if(experimentDesc!=null&&experimentDesc.length()==0){
                experimentDesc=null;
            }
            Experiment experiment=experimentService.copyExperiment(experimentId,name,experimentDesc);
            Map<String,Object> data=new HashMap<>(1);
            data.put("experimentId",experiment.getId());
            ResponseResult responseResult = new ResponseResult(true,"001","实验克隆成功");
            responseResult.setData(data);
            return responseResult;
        }catch (Exception e){
            return new ResponseResult(false,"002","实验克隆失败");
        }
    }

    @ResponseBody
    @ApiOperation("删除实验（初步删除或彻底删除，单个删除或批量删除）")
    @RequestMapping(value = "/experiment/deleteExperiment", method = RequestMethod.POST)
    public  ResponseResult deleteExperiment(@RequestParam @ApiParam(value = "实验id") String experimentIds,
                                            HttpSession httpSession) throws Exception{
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //service中有个删除服务
        String[] ids = experimentIds.split(",");
        Map<String,Object> isSuccess;
        for (int i=0;i<ids.length;i++){
            isSuccess = experimentService.deleteExperiment(Integer.parseInt(ids[i]));
            if (!isSuccess.get("isSuccess").equals(true)){
                return new ResponseResult(false,"002",isSuccess.get("message").toString());
            }
        }
        return new ResponseResult(true,"001","删除实验成功");
    }

    @ResponseBody
    @ApiOperation("运行实验")
    @RequestMapping(value = "/experiment/runExperimentRunning", method = RequestMethod.POST)
    public  ResponseResult runExperiment(@RequestParam @ApiParam(value = "实验id") Integer experimentId,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        String conversationId = UUID.randomUUID().toString();
        Map<String,Object> isSuccess=experimentService.startRunExperment(experimentId,userId,conversationId);
        if(isSuccess.get("isSuccess").equals(true)){
            Map<String,Object> data=new HashMap<>(2);
            data.put("experimentRunningId",isSuccess.get("experimentRunningId"));
            data.put("conversationId",conversationId);
            ResponseResult responseResult = new ResponseResult(true,"001",isSuccess.get("message").toString());
            responseResult.setData(data);
            return responseResult;
        }
        return new ResponseResult(false,"002",isSuccess.get("message").toString());

    }

    @ResponseBody
    @ApiOperation("回收站查看实验")
    @RequestMapping(value = "/experiment/getDeletedExperiment", method = RequestMethod.POST)
    public  ResponseResult deleteExperiment(@RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                            @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
                                            HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data=experimentService.getDeletedExperiment(DeleteStatus.DELETED.getValue(),pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取回收站中的所有实验运行列表"));
        return responseResult;
    }

    @ResponseBody
    @ApiOperation("还原实验")
    @RequestMapping(value = "/experiment/restoreExperiment", method = RequestMethod.POST)
    public  ResponseResult restoreExperiment(@RequestParam @ApiParam(value = "实验id") String experimentIds,
                                             HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        String[] ids = experimentIds.split(",");
        boolean isSuccess;
        for (int i=0;i<ids.length;i++){
            isSuccess = experimentService.restoreExperiment(Integer.parseInt(ids[i]));
            if (!isSuccess){
                return new ResponseResult(false,"002","还原实验失败");
            }
        }
        return new ResponseResult(true,"001","还原实验成功");
    }

    @ResponseBody
    @ApiOperation("获取最近创建的实验")
    @RequestMapping(value = "/experiment/getExperiment", method = RequestMethod.GET)
    public ResponseResult getExperiment(@RequestParam @ApiParam(value = "实验条数") Integer experimentNum,
                                        HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());

        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(experimentService.getExperiment(userId,experimentNum));
        responseResult.setMeta(new MetaData(true,"001","成功获取最近创建实验列表"));
        return  responseResult;
    }



}
