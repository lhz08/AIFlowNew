package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
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
import java.util.Map;

/**
 * @author 鲢鱼QAQ
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

    /**
     * 测试使用
     * @param fkExperimentRunningId
     * @param resultJsonString
     * @param httpSession
     * @return
     * @throws Exception
     */
    @ResponseBody
    @ApiOperation("创建实验结果的前端Json字符串保存记录")
    @RequestMapping(value = "/experimentRunningJsonResult/createExperimentRunningJsonResult", method = RequestMethod.POST)
    public ResponseResult createExperimentRunningJsonResult(@RequestParam @ApiParam(value = "实验运行id") Integer fkExperimentRunningId,
                                                            @RequestParam @ApiParam(value = "Json结果") String resultJsonString,
                                                            HttpSession httpSession) throws Exception{
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data=new HashMap<>(1);
        try {
            ExperimentRunningJsonResult experimentRunningJsonResult =
                    experimentRunningJsonResultService.createExperimentRunningJsonResult(fkExperimentRunningId, resultJsonString);

            ResponseResult responseResult=new ResponseResult(true,"001","创建实验结果Json前端Json字符串成功");
            data.put("experimentRunningJsonResultId", experimentRunningJsonResult.getId());
            responseResult.setData(data);
            return responseResult;

        }catch (Exception e){
            ResponseResult responseResult = new ResponseResult(false,"002","创建实验结果Json前端Json字符串失败,具体信息："+e.getMessage());
            return responseResult;
        }
    }

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
            ExperimentRunningJsonResult experimentRunningJsonResult =
                    experimentRunningJsonResultService.getExperimentRunningJsonResultByRunningId(fkExperimentRunningId);
            experimentRunningJsonResult.setResultJsonString(resultJsonString);
            experimentRunningJsonResult.setCreateTime(new Date());
            if (experimentRunningJsonResultService.updateExperimentRunningJsonResult(experimentRunningJsonResult)) {
                data.put("experimentRunningJsonResultId", experimentRunningJsonResult.getId());
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

    @ResponseBody
    @ApiOperation("通过运行id查询前端Json字符串保存记录")
    @RequestMapping(value = "/experimentRunningJsonResult/getExperimentRunningJsonResultByRunningId", method = RequestMethod.POST)
    public ResponseResult getExperimentRunningJsonResultByRunningId(@RequestParam @ApiParam(value = "实验运行id") Integer fkExperimentRunningId,
                                                                    HttpSession httpSession) throws Exception {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> data = new HashMap<>(1);
        try {
            ExperimentRunningJsonResult experimentRunningJsonResult =
                    experimentRunningJsonResultService.getExperimentRunningJsonResultByRunningId(fkExperimentRunningId);
                data.put("resultJsonString", experimentRunningJsonResult.getResultJsonString());
                responseResult.setCode("001");
                responseResult.setSuccess(true);
                responseResult.setMessage("查询runningid= " + fkExperimentRunningId + " 的字符串成功");
                responseResult.setData(data);

            return responseResult;
        } catch (Exception e) {
            return new ResponseResult(false, "002", "查询实验结果Json前端Json字符串失败,具体信息：" + e.getMessage());

        }
    }

}
