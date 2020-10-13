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
     * 将实验标记为模板
     * @param experimentId
     * @param templateName
     * @param templateDesc
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/template/markExperimentToTemplate", method = RequestMethod.POST)
    public ResponseResult markExperimentToTemplate(@RequestParam Integer experimentId,
                                         @RequestParam String templateName,
                                         @RequestParam String tags,
                                         @RequestParam String templateDesc,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        templateService.markExperimentToTemplate(experimentId,templateName,tags,templateDesc,userId);
        return new ResponseResult(true,"001","将实验标记为模板成功");
    }

    /**
     * 从模板创建实验
     * @param templateId
     * @param experimentName
     * @param experimentDesc
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/template/createExperimentFromTemplate", method = RequestMethod.POST)
    public ResponseResult createExperimentFromTemplate(@RequestParam Integer templateId,
                                                       @RequestParam(required = false) String experimentName,
                                                       @RequestParam(required = false) String experimentDesc,
                                                       HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Experiment data = templateService.createExperimentFromTemplate(templateId, experimentName, experimentDesc);

        ResponseResult responseResult = new ResponseResult(true,"001","从实验创建模板成功");
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * 修改模板的名称、描述和标签
     * @param templateId
     * @param tamplateName
     * @param templateTags
     * @param templateDesc
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/template/updateTemplate",method = RequestMethod.POST)
    public ResponseResult updateTemplate(@RequestParam Integer templateId,
                                         @RequestParam String tamplateName,
                                         @RequestParam String templateTags,
                                         @RequestParam String templateDesc,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean b = templateService.updateTemplate(templateId, tamplateName, templateTags, templateDesc);
        ResponseResult responseResult = new ResponseResult(true,"001","修改模板成功");
        return responseResult;
    }

    /**
     * 获得所有模板，首先根据type判断是否公共
     * @param type
     * @param isDeleted
     * @param pageNum
     * @param pageSize
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/template/getTemplate", method = RequestMethod.POST)
    public ResponseResult selectAllTemplate(@RequestParam Integer type,
                                            @RequestParam Integer isDeleted,
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
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
    public ResponseResult fuzzySearchTemplate(@RequestParam Integer type,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String tags,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
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
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
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
                                          HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
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
