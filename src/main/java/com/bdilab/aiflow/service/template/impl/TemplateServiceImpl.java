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
    public Template createTemplate(Template template){
        templateMapper.insertTempalte(template);
        return template;

    }



    /**todo 流程的pipeline尚未获得更新机会
     * type=0，需要新建流程和实验
     * type=1，实验id=null，说明没有父实验，需要新建实验。将fkExperimentId赋新id
     * type=1，实验id!=null，该实验isDeleted=0，说明有未删除父实验，需要定位到原实验
     * type=1，实验id!=null，该实验isDeleted=1，说明父实验被删除，在回收站，需要提示
     * @param template
     * @param userId
     * @param workflowName
     * @param workflowTags
     * @param workflowDesc
     * @param experimentName
     * @param experimentDesc
     * @return
     */
    @Override
    public Map<String,Object> createExperiment(Template template,
                                               Integer userId,
                                               String workflowName,
                                               String workflowTags,
                                               String workflowDesc,
                                               String experimentName,
                                               String experimentDesc){
        Map<String,Object> data = new HashMap<>(3);

        if(template.getType()==0){
            File xmlFile = new File(template.getWorkflowAddr());
            if(!xmlFile.exists())
            {
                data.put("falseReason","wrongWorkflowAddr");
                return data;
            }
            Workflow workflow = new Workflow();
                    //workflowService.createWorkflow(workflowName, workflowTags, workflowDesc, userId);
            workflow.setGgeditorObjectString(template.getGgeditorObjectString());
            boolean isSuccess = workflowService.updateWorkflow(workflow,readFile(template.getWorkflowAddr()));
            if(!isSuccess){
                data.put("falseReason","wrongUpdateWorkflow");
                return data;
            }
            Experiment experiment = new Experiment();
                    //experimentService.createExperiment(workflow.getId(), experimentName, experimentDesc);
            experiment.setParamJsonString(template.getParamJsonString());
            experimentMapper.updateExperiment(experiment);
            data.put("returnType",0);
            data.put("message","Insert workflow and experiment" );
            data.put("newWorkflowId",workflow.getId());
            data.put("newExperimentId",experiment.getId());
        }
        else if (template.getType()==1){
            if(template.getFkExperimentId()==null){
                Experiment experiment = new Experiment();
                        //experimentService.createExperiment(template.getFkWorkflowId(), experimentName, experimentDesc);
                experiment.setParamJsonString(template.getParamJsonString());
                experimentMapper.updateExperiment(experiment);
                template.setFkExperimentId(experiment.getId());
                boolean isSuccess = templateMapper.updateTemplate(template)==1;
                if(!isSuccess){
                    data.put("falseReason","wrongUpdatefkExperimentId");
                    return data;
                }
                data.put("returnType",1);
                data.put("message","Insert experiment" );
                data.put("newExperimentId",experiment.getId());
            }
            else{
                Experiment experiment = experimentMapper.selectExperimentById(template.getFkExperimentId());
                if(experiment.getIsDeleted()==0){
                    data.put("returnType",2);
                    data.put("message","Locate experiment" );
                    data.put("experimentId",experiment.getId());
                }
                else if(experiment.getIsDeleted()==1){
                    data.put("returnType",3);
                    data.put("message","Deleted experiment" );
                    data.put("experimentId",experiment.getId());
                }
                else{
                    data.put("falseReason","wrongExperimentIsDeleted");
                }
            }
        }

        return data;

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


    /**
     * 更新参数表，同时会将fkExperimentId置为null
     * @param template
     * @return
     */
    @Override
    public boolean updateTemplateParamJsonString(Template template){
        return templateMapper.updateTemplateParamJsonString(template)==1;
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
