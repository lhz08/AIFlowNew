package com.bdilab.aiflow.service.workflow.impl;

import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.mapper.ExperimentMapper;
import com.bdilab.aiflow.mapper.TemplateMapper;
import com.bdilab.aiflow.mapper.WorkflowMapper;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.pipeline.PipelineService;
import com.bdilab.aiflow.service.workflow.WorkflowService;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import com.bdilab.aiflow.vo.WorkflowVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;



@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired
    private WorkflowMapper workflowMapper;

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private ExperimentMapper experimentMapper;

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private PipelineService pipelineService;

    /**
     * @// TODO: 2020/9/2 尚不知道pipelineaddr怎么获得，是等python端得到后再写入？
     * todo:2 不知道如何获得FkCustomComponentIds
     * @param workflowName 流程名
     * @param tagString tag字符串
     * @param workflowDesc 流程描述
     * @param userId 用户id
     * @return
     */
    @Override
    public Workflow createAndSaveWorkflow(String workflowName, String tagString, String workflowDesc, String workflowXmlAddr,String ggeditorObjectString,Integer userId){
        Workflow workflow=new Workflow();

        workflow.setName(workflowName);
        workflow.setFkUserId(userId);
        workflow.setTags(tagString);
        workflow.setIsDeleted(Byte.parseByte("0"));
        workflow.setWorkflowDesc(workflowDesc);
        workflow.setWorkflowXmlAddr(workflowXmlAddr);
        workflow.setGgeditorObjectString(ggeditorObjectString);
        workflow.setIsCustom(Byte.parseByte("0"));
        workflow.setCreateTime(new Date());
//      workflow.setFkCustomComponentIds("1,2,3");
        workflow.setGeneratePipelineAddr("");
        workflow.setPipelineYamlAddr("");
        workflow.setPipelineId("");

        Map<String,String> data = pipelineService.generatePipeline(workflowXmlAddr,userId);
        workflow.setPipelineYamlAddr(data.get("pipelineYamlAddr"));
        workflow.setGeneratePipelineAddr(data.get("generatePipelineAddr"));

        System.out.println(data);

        File file = new File(data.get("pipelineYamlAddr"));
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            MultipartFile yamlFile = new MockMultipartFile(file.getName(),file.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);
            String pipelineId = pipelineService.uploadPipeline(workflow.getName(),workflow.getWorkflowDesc(),yamlFile);
            workflow.setPipelineId(pipelineId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        workflowMapper.insertWorkflow(workflow);

        return workflow;
    }

    /**
     * 克隆一个workflow
     * @param originWorkflow 原workflow
     * @param workflowName  新workflow名称
     * @param tagString 新的tags标签
     * @param workflowDesc 新的描述
     * @return 得到的新workflow
     */
    @Override
    public Workflow cloneWorkflow(Workflow originWorkflow,String workflowName, String tagString, String workflowDesc){
        Workflow workflow=new Workflow();
        workflow.setName(workflowName);
        workflow.setFkUserId(originWorkflow.getFkUserId());
        workflow.setTags(tagString);
        workflow.setIsDeleted(Byte.parseByte("0"));
        workflow.setWorkflowDesc(workflowDesc);

        if(workflow.getWorkflowXmlAddr()!=null) {
            if (!workflow.getWorkflowXmlAddr().equals("")) {
                workflow.setWorkflowXmlAddr(createXmlFile(readFile(originWorkflow.getWorkflowXmlAddr())));
            }
        }
        else{
            workflow.setWorkflowXmlAddr("");
        }

        //todo 原流程尚未获得pipeline写入机会
        if(workflow.getGeneratePipelineAddr()!=null) {
            if (!workflow.getGeneratePipelineAddr().equals("")) {
                workflow.setGeneratePipelineAddr(createPipelineFile(readFile(originWorkflow.getGeneratePipelineAddr())));
            }
        }
        else{
            workflow.setGeneratePipelineAddr("");
        }

        //todo 尚不知道这样是否可行，不知道ggeditor内容和流程id有无关联，需等待解析结构
        workflow.setGgeditorObjectString(originWorkflow.getGgeditorObjectString());
        workflow.setIsCustom(Byte.parseByte("0"));
        workflow.setFkCustomComponentIds(originWorkflow.getFkCustomComponentIds());
        workflow.setCreateTime(new Date());

        workflowMapper.insertWorkflow(workflow);
        return workflow;
    }

    /**
     * todo 怎么更新pipeline文件地址?
     * 更新已有的workflow
     * @param workflow 基本属性已赋值的workflow
     * @param workflowXml 前端传入的xml字符串(string)
     * @return true=成功
     */
    @Override
    public boolean updateWorkflow(Workflow workflow, String workflowXml){
        //如果已经有地址了，该地址还不是空的，则将原来的文件删除
        if(workflow.getWorkflowXmlAddr()!=null) {
            if (!workflow.getWorkflowXmlAddr().equals("")) {
                File xmlfile = new File(workflow.getWorkflowXmlAddr());
                xmlfile.delete();
            }
        }
        workflow.setWorkflowXmlAddr(createXmlFile(workflowXml));
        return workflowMapper.updateWorkflow(workflow)==1;
    }

    /**
     * 单例查询一个workflow
     * @param workflowId
     * @return
     */
    @Override
    public Workflow selectWorkflowById(Integer workflowId){
        return workflowMapper.selectWorkflowById(workflowId);
    }


    /**
     * 流程管理使用，查询所有符合userid和isdeleted的流程及下属实验
     * @param searchWorkflow
     * @param pageNum
     * @param pageSize
     * @return data中为workflowVOList，是查询结果的结合
     */
    @Override
    public Map<String,Object> selectAllWorkflowByUserIdAndIsDeleted(Workflow searchWorkflow, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        //PageHelper将令后续第一个select分页，每页存pageSize数量内容
        List<Workflow> workflowList = workflowMapper.selectAllWorkflow(searchWorkflow);
        List<WorkflowVO> workflowVOList =new ArrayList<>();
        for(Workflow workflow:workflowList){
            workflowVOList.add(buildWorkflowVO(workflow, (int) searchWorkflow.getIsDeleted(),true,null));
        }
        PageInfo pageInfo = new PageInfo<>(workflowList);

        Map<String,Object> data = new HashMap<>(3);
        data.put("WorkflowVOList",workflowVOList);
        data.put("TotalPageNum",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /**
     * 回收站使用，查询所有符合userid和isdeleted的流程，不含下属实验
     * @param searchWorkflow
     * @param pageNum
     * @param pageSize
     * @return data中为workflowVOList，是查询结果的结合
     */
    @Override
    public Map<String,Object> selectAllWorkflowOnlyByUserIdAndIsDeleted(Workflow searchWorkflow, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        //PageHelper将令后续第一个select分页，每页存pageSize数量内容
        List<Workflow> workflowList = workflowMapper.selectAllWorkflow(searchWorkflow);
        List<WorkflowVO> workflowVOList =new ArrayList<>();
        for(Workflow workflow:workflowList){
            workflowVOList.add(buildWorkflowVO(workflow, (int) searchWorkflow.getIsDeleted(),false,null));
        }
        PageInfo pageInfo = new PageInfo<>(workflowList);

        Map<String,Object> data = new HashMap<>(3);
        data.put("WorkflowVOList",workflowVOList);
        data.put("TotalPageNum",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /**
     * 带检索，通过userId(必有),workflowName(可选),tagstring(可选)检索流程
     * 通过experimentName(可选)检索流程下实验
     * @param searchWorkflow
     * @param experimentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Map<String,Object> searchWorkflow(Workflow searchWorkflow, String experimentName, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        //PageHelper将令后续第一个select分页，每页存pageSize数量内容
        List<Workflow> workflowList = workflowMapper.selectAllWorkflow(searchWorkflow);
        List<WorkflowVO> workflowVOList =new ArrayList<>();
        for(Workflow workflow:workflowList){
            workflowVOList.add(buildWorkflowVO(workflow, (int) searchWorkflow.getIsDeleted(),true, experimentName));
        }
        PageInfo pageInfo = new PageInfo<>(workflowList);

        Map<String,Object> data = new HashMap<>(3);
        data.put("WorkflowVOList",workflowVOList);
        data.put("TotalPageNum",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }


    /**
     * 放入回收站。将isDeleted=0的模板、实验、流程改为1
     * @param workflow
     * @return
     */
    @Override
    public boolean deleteWorkflow(Workflow workflow) {
        //未删除，流程、模板、实验、实验运行都置1；顺序是模板、运行、实验、流程
        Template searchTemplate = new Template();
        searchTemplate.setFkWorkflowId(workflow.getId());
        searchTemplate.setIsDeleted(0);
        List<Template> tList= templateMapper.selectAllTemplate(searchTemplate);

        Experiment searchExperiment = new Experiment();
        searchExperiment.setFkWorkflowId(workflow.getId());
        searchExperiment.setIsDeleted(0);
        List<Experiment> eList = experimentMapper.getAllExperimentByWorkflowIdAndIsDeleted(searchExperiment);
        try {
            for(Template template : tList){
                template.setIsDeleted(1);
                if(templateMapper.updateTemplateIsDeleted(template)!=1){
                    return false;
                }
            }

            for (Experiment experiment : eList) {
                Map<String,Object> isSuccess = experimentService.deleteExperiment(experiment.getId());
                if(isSuccess.get("isSuccess").equals(false)) {
                    return false;
                }
            }
        }
        catch (Exception e){e.printStackTrace();}

        workflow.setIsDeleted(Byte.parseByte("1"));
        if(workflowMapper.updateWorkflowIsDeleted(workflow)!=1){
            return false;
        }
        return true;
    }


    /**
     * 彻底删除流程
     * @param workflow
     * @return
     */
    @Override
    public boolean deleteWorkflowTotal(Workflow workflow){
        //清空顺序是模板、运行、实验、流程
        Template searchTemplate = new Template();
        searchTemplate.setFkWorkflowId(workflow.getId());
        searchTemplate.setIsDeleted(1);
        List<Template> tList= templateMapper.selectAllTemplate(searchTemplate);

        Experiment searchExperiment=new Experiment();
        searchExperiment.setFkWorkflowId(workflow.getId());
        searchExperiment.setIsDeleted(1);
        List<Experiment> eList = experimentMapper.getAllExperimentByWorkflowIdAndIsDeleted(searchExperiment);
        try {
            for(Template template : tList){
                if(templateMapper.deleteTemplateById(template.getId())!=1){
                    return false;
                }
            }


            for (Experiment experiment : eList) {
                Map<String,Object> isSuccess=experimentService.deleteExperiment(experiment.getId());
                if(isSuccess.get("isSuccess").equals(false)) {
                    return false;
                }
            }
        }
        catch (Exception e){e.printStackTrace();}
        //删除流程文件
        if(workflow.getWorkflowXmlAddr()!=null) {
            if(!workflow.getWorkflowXmlAddr().equals("")) {
                File xmlfile = new File(workflow.getWorkflowXmlAddr());
                xmlfile.delete();
            }
        }
        if(workflow.getGeneratePipelineAddr()!=null) {
            if(!workflow.getGeneratePipelineAddr().equals("")) {
                File pipelinefile = new File(workflow.getGeneratePipelineAddr());
                pipelinefile.delete();
            }
        }
        if(workflowMapper.deleteWorkflowById(workflow.getId())!=1){
            return false;
        }
        return true;


    }

    /**
     * 为前端展示的内容构造VO
     * @param workflow
     * @param isDeleted
     * @param withExperiment 是否含有实验
     * @return workflowVO
     */
    private WorkflowVO buildWorkflowVO(Workflow workflow, Integer isDeleted, boolean withExperiment, String experimentName){
        //VO展示的基本属性
        WorkflowVO workflowVO=new WorkflowVO();
        workflowVO.setId(workflow.getId());
        workflowVO.setName(workflow.getName());
        workflowVO.setTags(workflow.getTags());
        workflowVO.setGgeditorObejectString(workflow.getGgeditorObjectString());
        workflowVO.setCreateTime(workflow.getCreateTime());
        workflowVO.setWorkflowDesc(workflow.getWorkflowDesc());
        //xml内容和自定义组件内容需要解析后传递
        if(workflow.getWorkflowXmlAddr()!=null){
            if(!workflow.getWorkflowXmlAddr().equals("")) {
                workflowVO.setWorkflowXml(readFile(workflow.getWorkflowXmlAddr()));
            }
        }
        if(workflow.getFkCustomComponentIds()!=null){
            workflowVO.customComponentIdList=new ArrayList<>();
            String[] componentIds = workflow.getFkCustomComponentIds().split(",");
            for(String componrntId:componentIds){
                workflowVO.customComponentIdList.add(Integer.parseInt(componrntId));
            }
        }
        //每个Workflow的所属Experiment
        if(withExperiment) {
            workflowVO.experimentList=new ArrayList<>();
            List<Experiment> eList = new ArrayList<>();
            Experiment searchExperiment=new Experiment();
            searchExperiment.setFkWorkflowId(workflow.getId());
            searchExperiment.setIsDeleted(isDeleted);

            //如果需要检索名字
            if(experimentName!=null) {
                searchExperiment.setName(experimentName);
                eList = experimentMapper.getAllExperimentByWorkflowIdAndIsDeleted(searchExperiment);
            }
            else { eList = experimentMapper.getAllExperimentByWorkflowIdAndIsDeleted(searchExperiment); }

            for (Experiment experiment : eList) {
                workflowVO.experimentList.add(experiment);
            }
        }
        return workflowVO;
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


    /**
     * 创建xml文件
     * @param workflowXml 传入的xml内容
     * @return xml文件路径
     */
    private String createXmlFile(String workflowXml){

            String xmlFilePath = System.getProperty("user.dir")+File.separatorChar
                    +"xmlfile"+File.separatorChar
                    +new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime())+File.separatorChar
                    +UUID.randomUUID()+".xml";

        try {
            File newFile = new File(xmlFilePath);
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdir();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(xmlFilePath));
            out.write(workflowXml);
            out.close();
        } catch(IOException e)
        { e.printStackTrace(); }
        return xmlFilePath;
    }

    /**
     * todo 未完成 ，尚不知道pipeline如何获得
     * 创建pipeline的yaml文件
     * @param workflowPipeline 传入的Pipeline内容
     * @return Pipeline文件路径
     */
    private String createPipelineFile(String workflowPipeline){

        String yamlFilePath = System.getProperty("user.dir")+File.separatorChar
                +"pipeline"+File.separatorChar
                +new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime())+File.separatorChar
                +UUID.randomUUID()+".yaml";

        try {
            File newFile = new File(yamlFilePath);
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdir();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(yamlFilePath));
            out.write(workflowPipeline);
            out.close();
        } catch(IOException e)
        { e.printStackTrace(); }
        return yamlFilePath;
    }


    @Override
    public boolean restoreWorkflow(Integer fkWorkflowId){
        Workflow workflow=new Workflow();
        workflow.setId(fkWorkflowId);
        workflow.setIsDeleted(Byte.parseByte(DeleteStatus.NOTDELETED.getValue()+""));
        return workflowMapper.updateWorkflow(workflow)==1;
    }
}

