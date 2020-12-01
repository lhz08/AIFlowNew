package com.bdilab.aiflow.service.template.impl;


import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.mapper.ExperimentMapper;
import com.bdilab.aiflow.mapper.TemplateMapper;
import com.bdilab.aiflow.mapper.WorkflowMapper;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.experiment.ExperimentRunningService;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import com.bdilab.aiflow.service.template.TemplateService;
import com.bdilab.aiflow.service.workflow.WorkflowService;
import com.bdilab.aiflow.service.workflow.impl.WorkflowServiceImpl;
import com.bdilab.aiflow.vo.TemplateVO;
import com.bdilab.aiflow.vo.WorkflowVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    TemplateMapper templateMapper;

    @Autowired
    WorkflowMapper workflowMapper;

    @Autowired
    ExperimentMapper experimentMapper;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    ExperimentService experimentService;


    @Override
    public boolean markExperimentToTemplate(Integer experimentId, String templateName, String tags, String templateDesc, Integer userId) {
        //从实验创建的模板和实验共用一份流程文件（包括xml、py、yaml）
        Experiment experiment = experimentMapper.selectExperimentById(experimentId);
        //若该实验已经被标记为模板，则不能再从该实验创建模板
        if (experiment.getIsMarkTemplate() == 1){
            return false;
        }
        Workflow workflow = workflowService.selectWorkflowById(experiment.getFkWorkflowId());

        Template template = new Template();
        template.setName(templateName);
        template.setType(1);
        template.setFkUserId(userId);
        template.setFkWorkflowId(experiment.getFkWorkflowId());
        template.setFkExperimentId(experimentId);
        template.setTags(tags);
        template.setIsDeleted(0);
        template.setWorkflowAddr(workflow.getWorkflowXmlAddr());
        template.setParamJsonString(experiment.getParamJsonString());
        template.setGgeditorObjectString(workflow.getGgeditorObjectString());
        template.setTemplateDesc(templateDesc);
        //往template表中添加一条记录
        templateMapper.insertTempalte(template);
        //将实验的isMarkTemplate字段标记为1
        experiment.setIsMarkTemplate(1);
        experimentMapper.updateExperimentIsMarkTemplate(experiment);
        return true;
    }

    /**
     * 从模板创建实验
     * type=0，需要新建流程和实验
     * @param templateId
     * @param experimentName
     * @param experimentDesc
     * @return
     */
    @Override
    public Experiment createExperimentFromTemplate(Integer userId,Integer templateId, String experimentName, String experimentDesc){
        Template template = templateMapper.selectTemplateById(templateId);
        Experiment experiment = experimentService.copyExperiment(userId,template.getFkExperimentId(), experimentName, experimentDesc);
        return experiment;

    }

    @Override
    public boolean updateTemplate(Integer templateId, String tamplateName, String templateTags, String templateDesc) {
        Template template = templateMapper.selectTemplateById(templateId);
        template.setName(tamplateName);
        template.setTags(templateTags);
        template.setTemplateDesc(templateDesc);
        templateMapper.updateTemplate(template);
        return true;
    }

    /**
     * 单例查询一个template
     * @param templateId
     * @return
     */
    @Override
    public Template selectTemplateById(Integer templateId){
        return templateMapper.selectTemplateById(templateId);
    }

    @Override
    public Map<String,Object> selectAllTemplate(Template searchTemplate, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);

        List<Template> templateList = templateMapper.selectAllTemplate(searchTemplate);
        List<TemplateVO> templateVOList=new ArrayList<>();
        for(Template template:templateList){
            templateVOList.add(buildTemplateVO(template));
        }
        PageInfo pageInfo = new PageInfo<>(templateList);

        Map<String,Object> data = new HashMap<>(3);
        data.put("templateVOList",templateVOList);
        data.put("TotalPageNum",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }


    @Override
    public Map<String,Object> fuzzySelectAllTemplate(Template searchTemplate, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);

        List<Template> templateList = templateMapper.fuzzySelectAllTemplate(searchTemplate);
        List<TemplateVO> templateVOList=new ArrayList<>();
        for(Template template:templateList){
            templateVOList.add(buildTemplateVO(template));
        }
        PageInfo pageInfo = new PageInfo<>(templateList);

        Map<String,Object> data = new HashMap<>(3);
        data.put("templateVOList",templateVOList);
        data.put("TotalPageNum",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    @Override
    public boolean setRunningIdNull(Integer experimentId){
        boolean isSuccess=templateMapper.updateRunningIdNull(experimentId)==1;
        return isSuccess;
    }


    /**
     * 将一个isDeleted=0的模板置1，放入回收站
     * isDeleted=1，直接deleted掉，更新其父实验isMarkTemplate=0
     * @param template
     * @return
     */
    @Override
    public boolean deleteTemplate(Template template){
        if(template.getIsDeleted()==0){
            template.setIsDeleted(1);
            return templateMapper.updateTemplateIsDeleted(template)==1;
        }
        else if(template.getIsDeleted()==1)
        {
            if(template.getFkExperimentId()!=null){
                Experiment experiment=new Experiment();
                experiment.setId(template.getFkExperimentId());
                experiment.setIsMarkTemplate(0);
                experimentMapper.updateExperimentIsMarkTemplate(experiment);
            }
            return templateMapper.deleteTemplateById(template.getId())==1;
        }


        return false;
    }



    private TemplateVO buildTemplateVO(Template template){
        //templateVO展示的基本属性
        TemplateVO templateVO = new TemplateVO();
        templateVO.setId(template.getId());
        templateVO.setName(template.getName());
        templateVO.setType(template.getType());
        templateVO.setFkUserId(template.getFkUserId());
        templateVO.setWorkflow(workflowMapper.selectWorkflowById(template.getFkWorkflowId()));
        templateVO.setFkExperimentId(template.getFkExperimentId());
        templateVO.setTags(template.getTags());
        templateVO.setIsDeleted(template.getIsDeleted());
        templateVO.setParamJsonString(template.getParamJsonString());
        templateVO.setGgeditorObjectString(template.getGgeditorObjectString());
        templateVO.setTemplateDesc(template.getTemplateDesc());
        //如果有父实验，获取其是否在回收站中
        if(template.getFkExperimentId()!=null) {
            Experiment fatherExperiment = experimentMapper.selectExperimentById(template.getFkExperimentId());
            templateVO.setExperimentIsDeleted(fatherExperiment.getIsDeleted());
        }
        else{
            templateVO.setExperimentIsDeleted(0);
        }
        //xml内容内容需要解析后传递
        if(template.getWorkflowAddr()!=null){
           if(!template.getWorkflowAddr().equals("")) {
                templateVO.setWorkflowXml(readFile(template.getWorkflowAddr()));
           }
        }
        return templateVO;
    }


    /**
     * @Author Humphery
     * 读xml内容
     * @param filePath
     * @return xml文件内容的字符串形式
     */
    private String readFile(String filePath){
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];

        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            String xmlContent = new String(fileContent, StandardCharsets.UTF_8);
            in.close();
            return xmlContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public boolean restoreTemplate(Integer templateId){

        Template template = templateMapper.selectTemplateById(templateId);
        Workflow workflow=new Workflow();
        workflow.setId(template.getFkWorkflowId());
        workflow.setIsDeleted(Byte.parseByte(DeleteStatus.NOTDELETED.getValue()+""));
        if(workflowMapper.updateWorkflow(workflow)!=1){
            return false;
        }

        template.setIsDeleted(0);
        return templateMapper.updateTemplateIsDeleted(template)==1;
    }



}
