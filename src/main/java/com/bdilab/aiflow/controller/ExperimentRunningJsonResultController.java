package com.bdilab.aiflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.model.ExperimentRunningJsonResult;
import com.bdilab.aiflow.service.component.ComponentOutputStubService;
import com.bdilab.aiflow.service.experiment.ExperimentRunningJsonResultService;
import com.bdilab.aiflow.service.experiment.ExperimentRunningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Catfish
 * @date 2021/4/2
 */
@Controller
@CrossOrigin
@Api(value="实验运行得到Json结果Controller")
public class ExperimentRunningJsonResultController {

    @Autowired
    ExperimentRunningJsonResultService experimentRunningJsonResultService;

    @Autowired
    ComponentOutputStubService componentOutputStubService;

    @Deprecated
    @ResponseBody
    @ApiOperation("通过实验运行id,组件id和type得到结果，转换成前端可用JSON字符串格式")
    @RequestMapping(value = "/experimentRunningJsonResult/getResultJson", method = RequestMethod.POST)
    public  ResponseResult getResultJson(@RequestParam @ApiParam(value = "实验运行id") Integer runningId,
                                         @RequestParam @ApiParam(value = "组件id") Integer componentId,
                                         @RequestParam @ApiParam(value = "图类型,不填或12热力图，13折线图") Integer type,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> isSuccess = experimentRunningJsonResultService.getResultJson(runningId,componentId,type);
        if(isSuccess.get("isSuccess").equals(false)){
            return new ResponseResult(false,"002",isSuccess.get("message").toString());
        }
        ResponseResult responseResult=new ResponseResult(true,"001",isSuccess.get("message").toString());
        responseResult.setData(isSuccess.get("data"));
        return responseResult;
    }


    @Deprecated
    @ResponseBody
    @ApiOperation("查询实验结果的前端Json字符串保存记录，没有则创建")
    @RequestMapping(value = "/experimentRunningJsonResult/fetchExperimentRunningJsonResultByRunningId", method = RequestMethod.POST)
    public ResponseResult fetchExperimentRunningJsonResultByRunningId(@RequestParam @ApiParam(value = "实验运行id") Integer fkExperimentRunningId,
                                                                      HttpSession httpSession) throws Exception {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> data = new HashMap<>(1);
        try {
            List<ExperimentRunningJsonResult> experimentRunningJsonResultList =
                    experimentRunningJsonResultService.getExperimentRunningJsonResultByRunningId(fkExperimentRunningId);
            if(experimentRunningJsonResultList.size()!=0){
                data.put("resultJsonString", experimentRunningJsonResultList.get(0).getResultJsonString());
                responseResult.setCode("001");
                responseResult.setSuccess(true);
                responseResult.setMessage("查询到 runningid= " + fkExperimentRunningId + " 的字符串");
                responseResult.setData(data);

                return responseResult;
            }

            ExperimentRunningJsonResult experimentRunningJsonResult =
                    experimentRunningJsonResultService.createExperimentRunningJsonResult(fkExperimentRunningId, null);

            responseResult=new ResponseResult(true,"002","没有查询到，创建新结果成功");
            data.put("experimentRunningJsonResultId", experimentRunningJsonResult.getId());
            responseResult.setData(data);

            return responseResult;

        } catch (Exception e) {
            return new ResponseResult(false, "003", "fetch失败,具体信息：" + e.getMessage());
        }
    }


    @Deprecated
    @ResponseBody
    @ApiOperation("更新实验结果的前端Json字符串保存记录")
    @RequestMapping(value = "/experimentRunningJsonResult/updateExperimentRunningJsonResultByRunningId", method = RequestMethod.POST)
    public ResponseResult updateExperimentRunningJsonResultByRunningId(@RequestParam @ApiParam(value = "实验运行id") Integer fkExperimentRunningId,
                                                                    @RequestParam @ApiParam(value = "新的Json结果字符串") String resultJsonString,
                                                                    HttpSession httpSession) throws Exception {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> data = new HashMap<>(1);
        try {
            List<ExperimentRunningJsonResult> experimentRunningJsonResultList =
                    experimentRunningJsonResultService.getExperimentRunningJsonResultByRunningId(fkExperimentRunningId);
            experimentRunningJsonResultList.get(0).setResultJsonString(resultJsonString);
            experimentRunningJsonResultList.get(0).setCreateTime(new Date());
            if (experimentRunningJsonResultService.updateExperimentRunningJsonResult(experimentRunningJsonResultList.get(0))) {
                data.put("experimentRunningJsonResultId", experimentRunningJsonResultList.get(0).getId());
                responseResult.setCode("001");
                responseResult.setSuccess(true);
                responseResult.setMessage("更新runningid= " + fkExperimentRunningId + " 的字符串成功");
                responseResult.setData(data);
            } else {
                responseResult.setCode("002");
                responseResult.setSuccess(false);
                responseResult.setMessage("更新runningid= " + fkExperimentRunningId + " 的字符串失败，执行service.update出错");
            }
            return responseResult;
        } catch (Exception e) {
            return new ResponseResult(false, "003", "更新实验结果Json前端Json字符串失败,具体信息：" + e.getMessage());
        }
    }


    @Deprecated
    @ResponseBody
    @ApiOperation("创建实验结果的前端Json字符串保存记录")
    @RequestMapping(value = "/experimentRunningJsonResult/createExperimentRunningJsonResult", method = RequestMethod.POST)
    public ResponseResult createExperimentRunningJsonResult(@RequestParam @ApiParam(value = "实验运行id") Integer fkExperimentRunningId,
                                                            @RequestParam @ApiParam(value = "Json结果") String resultJsonString,
                                                            HttpSession httpSession) throws Exception{
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data=new HashMap<>(1);
        try {

            List<ExperimentRunningJsonResult> experimentRunningJsonResultList =
                    experimentRunningJsonResultService.getExperimentRunningJsonResultByRunningId(fkExperimentRunningId);
            if(experimentRunningJsonResultList.size()!=0){
                ResponseResult responseResult=new ResponseResult(true,"002","已存在对应Json字符串");
                data.put("experimentRunningJsonResultId", experimentRunningJsonResultList.get(0).getId());
                responseResult.setData(data);
                return responseResult;
            }

            ExperimentRunningJsonResult experimentRunningJsonResult =
                    experimentRunningJsonResultService.createExperimentRunningJsonResult(fkExperimentRunningId, resultJsonString);

            ResponseResult responseResult=new ResponseResult(true,"001","创建实验结果Json前端Json字符串成功");
            data.put("experimentRunningJsonResultId", experimentRunningJsonResult.getId());
            responseResult.setData(data);
            return responseResult;

        }catch (Exception e){
            ResponseResult responseResult = new ResponseResult(false,"003","创建实验结果Json前端Json字符串失败,具体信息："+e.getMessage());
            return responseResult;
        }
    }

    @Deprecated
    @ResponseBody
    @ApiOperation("通过运行id查询前端Json字符串保存记录")
    @RequestMapping(value = "/experimentRunningJsonResult/getExperimentRunningJsonResultByRunningId", method = RequestMethod.POST)
    public ResponseResult getExperimentRunningJsonResultByRunningId(@RequestParam @ApiParam(value = "实验运行id") Integer fkExperimentRunningId,
                                                                    HttpSession httpSession) throws Exception {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> data = new HashMap<>(1);
        try {
            List<ExperimentRunningJsonResult> experimentRunningJsonResultList =
                    experimentRunningJsonResultService.getExperimentRunningJsonResultByRunningId(fkExperimentRunningId);
                data.put("resultJsonString", experimentRunningJsonResultList.get(0).getResultJsonString());
                responseResult.setCode("001");
                responseResult.setSuccess(true);
                responseResult.setMessage("查询runningid= " + fkExperimentRunningId + " 的字符串成功");
                responseResult.setData(data);

            return responseResult;
        } catch (Exception e) {
            return new ResponseResult(false, "002", "查询实验结果Json前端Json字符串失败,具体信息：" + e.getMessage());

        }
    }


    /**
     * @author liran
     * @param runningId
     * @param componentId
     * @param httpSession
     * @return
     */
    @ResponseBody
    @ApiOperation("通过实验运行id,组件id拿到输出结果，转换成二维数组返回")
    @RequestMapping(value = "/experimentRunningJsonResult/getResultArrayJson", method = RequestMethod.POST)
    public  ResponseResult getResultArrayJson(@RequestParam @ApiParam(value = "实验运行id") Integer runningId,
                                         @RequestParam @ApiParam(value = "组件id") Integer componentId,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        ExperimentRunningJsonResult experimentRunningJsonResult = experimentRunningJsonResultService.selectExperimentRunningJsonResultByExperimentRunningIdAndComponentInfoId(runningId, componentId);
        //数据库没有，则将输出结果文件进行转换，返回
        if(experimentRunningJsonResult == null || experimentRunningJsonResult.getResultJsonString() == null || experimentRunningJsonResult.getResultJsonString().equals("")){
            Map<String,Object> fileAddr=componentOutputStubService.getOutputFileAddr(runningId, componentId,null);
            if(fileAddr.get("isSuccess").equals(false)){
                return new ResponseResult(false,"002",fileAddr.get("message").toString());
            }
            Map<String, Object> csvToArrayRes = FileUtils.transResultCsvToArray((String) fileAddr.get("outputFileAddr"));
            if(csvToArrayRes.get("isSuccess").equals(false)){
                return new ResponseResult(false,"003","获取输出结果失败");
            }
            ResponseResult responseResult=new ResponseResult(true,"001","获取输出结果成功");
            Map<String,Object> data = new HashMap<>();
            data.put("type",0);
            data.put("json",JSONObject.toJSONString(csvToArrayRes.get("array")));
            responseResult.setData(data);
            return responseResult;
        }
        //数据库里有，直接获取返回
        ResponseResult responseResult=new ResponseResult(true,"001","获取输出结果成功");
        Map<String,Object> data = new HashMap<>();
        data.put("type",1);
        data.put("json",experimentRunningJsonResult.getResultJsonString());
        data.put("mapConfig", experimentRunningJsonResult.getMapConfigString());
        responseResult.setData(data);
        return responseResult;
    }

    @ResponseBody
    @ApiOperation("通过实验运行id,组件id保存用户编辑过的输出结果的json串")
    @RequestMapping(value = "/experimentRunningJsonResult/saveResultArrayJson", method = RequestMethod.POST)
    public  ResponseResult saveResultArrayJson(@RequestParam @ApiParam(value = "实验运行id") Integer runningId,
                                         @RequestParam @ApiParam(value = "组件id") Integer componentId,
                                         @RequestParam @ApiParam(value = "用户编辑后的输出结果的json数据串") String resultJson,
                                         @RequestParam(required = false) @ApiParam(value = "用户配置过后的图像配置信息Json串") String mapConfigJson,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        ExperimentRunningJsonResult experimentRunningJsonResult = experimentRunningJsonResultService.selectExperimentRunningJsonResultByExperimentRunningIdAndComponentInfoId(runningId, componentId);
        if(experimentRunningJsonResult == null){
            experimentRunningJsonResult = experimentRunningJsonResultService.createExperimentRunningJsonResult(runningId, componentId, mapConfigJson,resultJson);
            if(experimentRunningJsonResult.getId() == null){
                return new ResponseResult(false,"002","保存失败");
            }
        }
        ExperimentRunningJsonResult updateJsonResult = new ExperimentRunningJsonResult();
        updateJsonResult.setId(experimentRunningJsonResult.getId());
        updateJsonResult.setResultJsonString(resultJson);

        //如果有更新图配置
        if (mapConfigJson != null) { updateJsonResult.setMapConfigString(mapConfigJson); }
        else { updateJsonResult.setResultJsonString(experimentRunningJsonResult.getMapConfigString()); }

        if(!experimentRunningJsonResultService.updateExperimentRunningJsonResult(updateJsonResult)){
            return new ResponseResult(false,"003","更新失败");
        }
        return new ResponseResult(true,"001","保存成功");
    }

}
