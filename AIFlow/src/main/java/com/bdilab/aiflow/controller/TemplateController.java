package com.bdilab.aiflow.controller;


import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import com.bdilab.aiflow.service.template.TemplateService;
import com.bdilab.aiflow.service.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin
public class TemplateController {

    @Autowired
    TemplateService templateService;


    /**
     * 在线保存模板，更新参数列表
     * @param templateId
     * @param paramJsonString
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/template/saveTemplate", method = RequestMethod.POST)
    public ResponseResult saveTemplate(@RequestParam Integer templateId,
                                       @RequestParam String paramJsonString,
                                       HttpSession httpSession){
        Template template = new Template();
        template.setId(templateId);
        template.setParamJsonString(paramJsonString);
        Boolean isSuccess = templateService.updateTemplateParamJsonString(template);
        if(!isSuccess){
            return new ResponseResult(false,"002","在线保存模板参数失败");
        }
        return new ResponseResult(true,"001","在线保存模板参数成功");
    }


    /**
     * 模板另存为，输入名称描述和tags，
     * */
    @ResponseBody
    @RequestMapping(value = "/template/insertTemplate", method = RequestMethod.POST)
    public ResponseResult insertTemplate(@RequestParam Integer userId,
                                         @RequestParam Integer originTemplateId,
                                         @RequestParam String templateName,
                                         @RequestParam String tags,
                                         @RequestParam String paramJsonString,
                                         @RequestParam String templateDesc,
                                         HttpSession httpSession){
        Template originTemplate = templateService.selectTemplateById(originTemplateId);
        Template template = new Template();
        template.setName(templateName);
        template.setType(1);
        template.setFkUserId(userId);
        template.setFkWorkflowId(originTemplate.getFkWorkflowId());
        template.setTags(tags);
        template.setIsDeleted(0);
        template.setWorkflowAddr(originTemplate.getWorkflowAddr());
        template.setParamJsonString(paramJsonString);
        template.setGgeditorObjectString(originTemplate.getGgeditorObjectString());
        template.setTemplateDesc(templateDesc);

        template = templateService.createTemplate(template);
        Map<String, Object> data = new HashMap<>();
        ResponseResult responseResult=new ResponseResult(true,"001","另存模板参数成功");
        data.put("templateId", template.getId());
        responseResult.setData(data);
        return responseResult;
    }



    /**
     * 从流程创建实验
     * @param userId
     * @param templateId
     * @param workflowName
     * @param workflowTags
     * @param workflowDesc
     * @param experimentName
     * @param experimentDesc
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/template/createExperiment", method = RequestMethod.POST)
    public ResponseResult createExperiment(@RequestParam Integer userId,
                                           @RequestParam Integer templateId,
                                           @RequestParam(required = false) String workflowName,
                                           @RequestParam(required = false) String workflowTags,
                                           @RequestParam(required = false) String workflowDesc,
                                           @RequestParam(required = false) String experimentName,
                                           @RequestParam(required = false) String experimentDesc,
                                           HttpSession httpSession){
        Template template = templateService.selectTemplateById(templateId);
        Map<String, Object> data = templateService.createExperiment(template, userId, workflowName, workflowTags, workflowDesc, experimentName, experimentDesc);

        ResponseResult responseResult = new ResponseResult(true,"001","完成");
        responseResult.setData(data);
        return responseResult;
    }


    /**
     * 获得所有模板，首先根据type判断是否公共
     * @param userId
     * @param type
     * @param isDeleted
     * @param pageNum
     * @param pageSize
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/template/getTemplate", method = RequestMethod.POST)
    public ResponseResult selectAllTemplate(@RequestParam Integer userId,
                                            @RequestParam Integer type,
                                            @RequestParam Integer isDeleted,
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            HttpSession httpSession){
        //type=0，系统模板，搜素所有type=0的模板，isDeleted=0为展示，=1为回收站
        Template template = new Template();
        template.setType(type);
        template.setIsDeleted(isDeleted);
        //type=1，用户模板，搜素所有type=1且userId=用户id的模板，isDeleted=0为展示，=1为回收站
        if(type==1)
        { template.setFkUserId(userId); }
        Map<String, Object> data = templateService.selectAllTemplate(template, pageNum, pageSize);

        ResponseResult responseResult = new ResponseResult(true,"001", "模板列表搜索成功");
        responseResult.setData(data);
        return responseResult;
    }


    @ResponseBody
    @RequestMapping(value = "/template/searchTemplateByKeyword", method = RequestMethod.POST)
    public ResponseResult fuzzySearchTemplate(@RequestParam Integer userId,
                                              @RequestParam Integer type,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String tags,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              HttpSession httpSession){
        Template template = new Template();
        template.setType(type);
        template.setIsDeleted(0);
        //type=1，用户模板，搜素所有type=1且userId=用户id的模板，否则展示公共模板
        if(type==1) {
            template.setFkUserId(userId);
        }
        else{
            template.setFkUserId(-1);
        }
        if(name!=null) {
            template.setName(name);
        }
        if(tags!=null){
            template.setTags(tags);
        }

        Map<String, Object> data = templateService.fuzzySelectAllTemplate(template, pageNum, pageSize);

        ResponseResult responseResult = new ResponseResult(true,"001", "模板列表搜索成功");
        responseResult.setData(data);
        return responseResult;
    }





    /**
     * 删除模板，模板只会删除自己的部分
     * @param templateIds
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/template/deleteTemplate", method = RequestMethod.POST)
    public ResponseResult deleteTemplate(@RequestParam Integer[] templateIds,
                                         HttpSession httpSession){
        ResponseResult responseResult=new ResponseResult(true,"001","Success");
        for(Integer templateId:templateIds)
        {
            Template template = templateService.selectTemplateById(templateId);
            if(!templateService.deleteTemplate(template)){
                return new ResponseResult(false,"002","FalseId:"+template.getId());
            }
        }
       return responseResult;
    }


    @ResponseBody
    @RequestMapping(value = "/workflow/restoreTemplate", method = RequestMethod.POST)
    public ResponseResult restoreTemplate(@RequestParam Integer[] templateIds,
                                          HttpSession httpSession
    ){

        for(Integer templateId:templateIds)
        {
            boolean isSuccess=templateService.restoreTemplate(templateId);
            if(!isSuccess){
                return new ResponseResult(false,"002","FalseId:"+templateId);
            }
        }


        return new ResponseResult(true,"001","模板还原成功");
    }





}
