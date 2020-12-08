package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.service.model.ModelService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;


@Controller
public class ModelController {


    @Autowired
    ModelService modelService;

    /*创建模型*/
    @ResponseBody
    @ApiOperation("用户调用接口，保存模型，保存之后，用户可以使用该模型")
    @RequestMapping(value = "/model/createModel", method = RequestMethod.POST)
    public ResponseResult createWorkflow(@RequestParam @ApiParam(value = "模型id") Integer modelId,
                                         @RequestParam @ApiParam(value = "模型名") String modelName,
                                         @RequestParam @ApiParam(value = "模型描述") String modelDesc
                                        ){
        boolean isSuccess = modelService.createModel(modelId,modelName,modelDesc);
        if (isSuccess){
            return new ResponseResult(true,"001","创建模型成功");
        }
        return new ResponseResult(false,"002","创建模型失败");
    }

    @ResponseBody
    @ApiOperation("python端调用接口，保存模型至系统")
    @RequestMapping(value = "/model/saveModel",method = RequestMethod.POST)
    public ResponseResult saveModel(@RequestParam @ApiParam(value = "processInstanceId") String processInstanceId,
                                    @RequestParam @ApiParam(value = "componentId") String componentId,
                                    @RequestParam @ApiParam(value = "conversationId") String conversationId,
                                    @RequestParam @ApiParam(value = "modelFileAddr") String modelFileAddr){
        modelService.saveModel(processInstanceId,componentId,conversationId,modelFileAddr);
        return new ResponseResult(true,"001","成功保存模型。");
    }

    /*分页获取模型信息列表*/
    @ResponseBody
    @RequestMapping(value = "/model/getModelList",method = RequestMethod.GET)
    public ResponseResult getModelList(@RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = modelService.getModelByUser(userId,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功分页获取模型信息列表"));
        return responseResult;
    }

    /*编辑模型信息*/
    @ResponseBody
    @RequestMapping(value = "/model/editModel", method = RequestMethod.POST)
    public ResponseResult editModel(@RequestParam Integer modelId,
                                    @RequestParam String modelName,
                                    @RequestParam String modelDesc,
                                    HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = modelService.editModel(modelId,modelName,modelDesc);
        if (isSuccess){
            return new ResponseResult(true,"001","编辑模型成功");
        }
        return new ResponseResult(false,"002","编辑模型失败");
    }

    /*删除模型--移入回收站*/
    @ResponseBody
    @RequestMapping(value = "/model/deleteModel",method = RequestMethod.POST)
    public ResponseResult deleteModelById(@RequestParam Integer modelId,
                                          HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = modelService.deleteModelById(modelId);
        if (isSuccess){
            return new ResponseResult(true,"001","模型移入回收站成功");
        }
        return new ResponseResult(false,"002","模型删除失败");
    }

    /*删除模型--彻底删除*/
    @ResponseBody
    @RequestMapping(value = "/model/deleteModelPermanently",method = RequestMethod.POST)
    public ResponseResult deleteModelCompletelyById(@RequestParam Integer modelId,
                                                    HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = modelService.deleteModelCompletelyById(modelId);
        if (isSuccess){
            return new ResponseResult(true,"001","彻底删除模型成功");
        }
        return new ResponseResult(false,"002","彻底删除模型失败");
    }

    /*分页获取回收站中的模型列表*/
    @ResponseBody
    @RequestMapping(value = "/model/getModelInTrash",method = RequestMethod.GET)
    public ResponseResult getModelInTrash(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = modelService.getModelInTrash(userId,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功分页获取回收站中的模型列表"));
        return responseResult;
    }

    /*从回收站恢复模型*/
    @ResponseBody
    @RequestMapping(value = "/model/restoreModel",method = RequestMethod.GET)
    public ResponseResult restoreModel(@RequestParam Integer modelId,
                                       HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean  isSuccess= modelService.restoreModel(modelId);
        if (isSuccess){
            return new ResponseResult(true,"001","恢复模型成功");
        }
        return new ResponseResult(false,"002","恢复模型失败");
    }

    /*按名称分页搜索模型*/
    @ResponseBody
    @RequestMapping(value = "/model/searchModelByName",method = RequestMethod.GET)
    public ResponseResult searchModelByName(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(defaultValue = "test") String modelName,
                                            HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = modelService.searchModelByName(userId,modelName,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*下载模型*/
    @ResponseBody
    @RequestMapping(value = "/model/downloadModel",method = RequestMethod.GET)
    public ResponseResult downloadDataset(@RequestParam Integer modelId,
                                          HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        File file = modelService.downloadDataset(modelId);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(file);
        responseResult.setMeta(new MetaData(true,"001","成功导出模型"));
        return responseResult;
    }
    /*下载模型*/
    @ResponseBody
    @ApiOperation("下载模型")
    @RequestMapping(value = "/model/downloadModelFromMinio",method = RequestMethod.GET)
    public ResponseResult downloadModelFromMinio(@RequestParam Integer modelId,
                                                 HttpSession httpSession,
                                                 HttpServletResponse response){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
        response = modelService.downloadModelFromMinio(userId,modelId,response);
        if(response.getStatus()==200) {
            return new ResponseResult(true, "001", "下载模型成功");
        }
        return new ResponseResult(true,"002","下载模型失败");
    }
    @ResponseBody
    @ApiOperation("模型封装成组件")
    @RequestMapping(value = "/model/modelToComponent",method = RequestMethod.POST)
    public ResponseResult modelToComponent(@RequestParam @ApiParam(value="模型id")Integer modelId,
                                           @RequestParam @ApiParam(value = "tag") String tag,
                                           @RequestParam @ApiParam(value="组件名") String componentName,
                                           @RequestParam @ApiParam(value = "组件描述") String componentDec,
                                           HttpSession httpSession
     ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(modelService.setModelToComponent(modelId, userId, componentName, componentDec,tag)) {
            return new ResponseResult(true, "001", "成功将模型封装成组件");
        }
        return new ResponseResult(true,"001","模型封装成组件失败");
    }
}
