package com.bdilab.aiflow.service.workflow.impl;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.enums.DeleteStatus;
import com.bdilab.aiflow.common.utils.DateUtils;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.ExperimentMapper;
import com.bdilab.aiflow.mapper.TemplateMapper;
import com.bdilab.aiflow.mapper.WorkflowComponentMapper;
import com.bdilab.aiflow.mapper.WorkflowMapper;
import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.model.WorkflowComponent;
import com.bdilab.aiflow.service.pipeline.PipelineService;
import com.bdilab.aiflow.service.workflow.WorkflowService;
import com.bdilab.aiflow.service.experiment.ExperimentService;
import com.bdilab.aiflow.vo.WorkflowVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.http.entity.ContentType;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;



@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Resource
    private WorkflowMapper workflowMapper;

    @Resource
    private TemplateMapper templateMapper;

    @Resource
    private ExperimentMapper experimentMapper;

    @Autowired
    private ExperimentService experimentService;

    @Resource
    private PipelineService pipelineService;

    @Autowired
    private WorkflowComponentMapper workflowComponentMapper;

    @Resource
    FilePathConfig filePathConfig;

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
    public Workflow createAndSaveWorkflow(String workflowName, String tagString, String workflowDesc, String workflowXml,String ggeditorObjectString,Integer userId){
        Workflow workflow=new Workflow();

        workflow.setName(workflowName);
        workflow.setFkUserId(userId);
        workflow.setTags(tagString);
        workflow.setIsDeleted(Byte.parseByte("0"));
        workflow.setWorkflowDesc(workflowDesc);
        workflow.setGgeditorObjectString(ggeditorObjectString);
        workflow.setIsCustom(Byte.parseByte("0"));
        workflow.setCreateTime(new Date());
//      workflow.setFkCustomComponentIds("1,2,3");
        workflow.setGeneratePipelineAddr("");
        workflow.setPipelineYamlAddr("");
        workflow.setPipelineId("");

        //将workflowXml存到文件里，将文件地址存入workflowXmlAddr字段
        String fileName = UUID.randomUUID()+".xml";
        String workflowXmlAddr = filePathConfig.getWorkflowXmlFilePath() + File.separatorChar + DateUtils.getCurrentDate()+File.separatorChar+fileName;
        workflow.setWorkflowXmlAddr(XmlUtils.generateXmlFile(workflowXml,workflowXmlAddr));

        //生成pipeline的py文件和yaml文件，存入相应的字段
        Map<String,String> data = pipelineService.generatePipeline(workflowXmlAddr,userId);
        System.out.println(data.get("pipelineYamlAddr"));
        workflow.setPipelineYamlAddr(data.get("pipelineYamlAddr"));
        workflow.setGeneratePipelineAddr(data.get("generatePipelineAddr"));

        //上传pipeline，返回pipelineId
        File file = new File(data.get("pipelineYamlAddr"));
        String pipelineName = UUID.randomUUID()+workflow.getName();
        String pipelineId = pipelineService.uploadPipeline(pipelineName,workflow.getWorkflowDesc(),file);
        workflow.setPipelineId(pipelineId);
        workflowMapper.insertWorkflow(workflow);

        return workflow;
    }

    /**
     * 克隆一个workflow
     * @param workflowId 原workflowId
     * @param workflowName  新workflow名称
     * @param tagString 新的tags标签
     * @param workflowDesc 新的描述
     * @return 得到的新workflow
     */
    @Override
    public Workflow cloneWorkflow(Integer workflowId,String workflowName, String tagString, String workflowDesc, Integer userId){
        Workflow originWorkflow=workflowMapper.selectWorkflowById(workflowId);

        //创建新的workflow
        Workflow workflow=new Workflow();
        workflow.setName(workflowName);
        workflow.setFkUserId(originWorkflow.getFkUserId());
        workflow.setTags(tagString);
        workflow.setIsDeleted(Byte.parseByte("0"));
        workflow.setWorkflowDesc(workflowDesc);
        workflow.setGgeditorObjectString(originWorkflow.getGgeditorObjectString());
        workflow.setIsCustom(Byte.parseByte("0"));
        workflow.setFkCustomComponentIds(originWorkflow.getFkCustomComponentIds());
        workflow.setCreateTime(new Date());

        //设置workflow的xmlAddr
        String workflowXmlAddr = createXmlFile(readFile(originWorkflow.getWorkflowXmlAddr()));
        workflow.setWorkflowXmlAddr(workflowXmlAddr);

        //生成pipeline的py文件和yaml文件
        Map<String,String> data = pipelineService.generatePipeline(workflowXmlAddr,userId);
        System.out.println(data.get("pipelineYamlAddr"));
        workflow.setPipelineYamlAddr(data.get("pipelineYamlAddr"));
        workflow.setGeneratePipelineAddr(data.get("generatePipelineAddr"));

        //在kubeflow上创建新的pipeline，并和克隆生成的workflow绑定
        File file = new File(originWorkflow.getPipelineYamlAddr());
        String pipelineId = pipelineService.uploadPipeline(workflowName,workflowDesc,file);
        workflow.setPipelineId(pipelineId);

        workflowMapper.insertWorkflow(workflow);
        return workflow;
    }

    /**
     * 修改workflow
     * @param workflowId
     * @param workflowXml
     * @param ggeditorObjectString
     * @return true=成功
     */
    @Override
    public boolean updateWorkflow(Integer workflowId, String workflowXml, String ggeditorObjectString, Integer userId){
        Workflow workflow = workflowMapper.selectWorkflowById(workflowId);
        //如果已经有地址了，该地址还不是空的，则将原来的文件删除
        String workflowXmlAddr = workflow.getWorkflowXmlAddr();
        if(workflowXmlAddr!=null) {
            if (!workflowXmlAddr.equals("")) {
                File xmlfile = new File(workflowXmlAddr);
                xmlfile.delete();
            }
        }
        String fileName = UUID.randomUUID()+".xml";
        String workflowXmlAddrNew = filePathConfig.getWorkflowXmlFilePath() + File.separatorChar + DateUtils.getCurrentDate()+File.separatorChar+fileName;
        workflow.setWorkflowXmlAddr(XmlUtils.generateXmlFile(workflowXml,workflowXmlAddrNew));

        workflow.setGgeditorObjectString(ggeditorObjectString);

        //删除修改前的workflow的py文件和yaml文件
        String pipelineYamlAddr = workflow.getPipelineYamlAddr();
        String generatePipelineAddr = workflow.getGeneratePipelineAddr();
        if(pipelineYamlAddr!=null) {
            if (!pipelineYamlAddr.equals("")) {
                File yamlFile = new File(pipelineYamlAddr);
                yamlFile.delete();
            }
        }
        if(generatePipelineAddr!=null) {
            if (!generatePipelineAddr.equals("")) {
                File pipelineFile = new File(generatePipelineAddr);
                pipelineFile.delete();
            }
        }

        //生成pipeline的py文件和yaml文件，存入相应的字段
        Map<String,String> data = pipelineService.generatePipeline(workflowXmlAddrNew,userId);
        System.out.println(data.get("pipelineYamlAddr"));
        workflow.setPipelineYamlAddr(data.get("pipelineYamlAddr"));
        workflow.setGeneratePipelineAddr(data.get("generatePipelineAddr"));

        //删除修改前的workflow的pipeline，重新生成修改后的workflow的pipeline，上传到kubeflow上，并将返回的pipelineId存入相应字段
        boolean isSuccess = pipelineService.deletePipelineById(workflow.getPipelineId());
        if(isSuccess){
            File file = new File(data.get("pipelineYamlAddr"));
            String pipelineId = pipelineService.uploadPipeline(workflow.getName(),workflow.getWorkflowDesc(),file);
            workflow.setPipelineId(pipelineId);
        }
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

    @Override
    public Map<String,Object> searchWorkflowByName(String workflowName,int pageNum, int pageSize, Integer userId){
        PageHelper.startPage(pageNum,pageSize);
        //PageHelper将令后续第一个select分页，每页存pageSize数量内容
        List<Workflow> workflowList = null;
        workflowList= workflowMapper.fuzzySelectWorkflowByName(userId,workflowName);
        List<WorkflowVO> workflowVOList =new ArrayList<>();
        for(Workflow workflow:workflowList){
            workflowVOList.add(buildWorkflowVO(workflow, 0,true, null));
        }
        PageInfo pageInfo = new PageInfo<>(workflowList);

        Map<String,Object> data = new HashMap<>(3);
        data.put("WorkflowVOList",workflowVOList);
        data.put("TotalPageNum",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    @Override
    public Map<String,Object> searchWorkflowByTags(String workflowTags,int pageNum, int pageSize, Integer userId){
        PageHelper.startPage(pageNum,pageSize);
        //PageHelper将令后续第一个select分页，每页存pageSize数量内容
        List<Workflow> workflowList = null;
        workflowList= workflowMapper.fuzzySelectWorkflowByTags(userId,workflowTags);
        List<WorkflowVO> workflowVOList =new ArrayList<>();
        for(Workflow workflow:workflowList){
            workflowVOList.add(buildWorkflowVO(workflow, 0,true, null));
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
     * @param workflowId
     * @return
     */
    @Override
    public boolean deleteWorkflow(Integer workflowId) {
        Workflow workflow = workflowMapper.selectWorkflowById(workflowId);
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
     * @param workflowId
     * @return
     */
    @Override
    public boolean deleteWorkflowTotal(Integer workflowId){
        Workflow workflow = workflowMapper.selectWorkflowById(workflowId);
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
        if(workflow.getPipelineYamlAddr()!=null && !workflow.getPipelineYamlAddr().equals("")){
            File yamlFile = new File(workflow.getPipelineYamlAddr());
            yamlFile.delete();
        }

        //删除kubeflow上的pipeline
        pipelineService.deletePipelineById(workflow.getPipelineId());

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

    @Override
    public boolean restoreWorkflow(Integer fkWorkflowId){
        Workflow workflow=new Workflow();
        workflow.setId(fkWorkflowId);
        workflow.setIsDeleted(Byte.parseByte(DeleteStatus.NOTDELETED.getValue()+""));
        return workflowMapper.updateWorkflow(workflow)==1;
    }

    @Override
    public Map<Integer, String> isEdit(Integer workflowId) {
        //判断一个流程是否可编辑，需要判断是否存在与该流程关联的实验和模板（包括回收站中的实验和模板），如果有,则不能修改。
        //原因：1、实验是在流程的基础上加了参数，如果流程进行了修改，那么实验的参数就对应不上了。
        // 2、模板和流程共用一个xml文件（同一文件路径），如果流程修改了，xml文件发生了变化，参数和流程图就对应不上了。
        Map<Integer,String> result = new HashMap<>();

        //在实验表中查看，是否存在与workflowId关联的实验
        List<Integer> experimentList = experimentMapper.selectExperimentByWorkflowId(workflowId);
        //在模板表中查看，是否存在与workflowId关联的模板
        List<Integer> templateIdList = templateMapper.selectTemplateByWorkflowId(workflowId);
        int size1 = experimentList.size();
        int size2 = templateIdList.size();

        if (size1 == 0 && size2 == 0){
            result.put(0,"该流程不存在与之关联的实验和模板，可以编辑");
        }
        if (size1 != 0 && size2 == 0){
            result.put(1,"该流程存在与之关联的实验，不能编辑");
        }
        if (size1 == 0 && size2 != 0){
            result.put(2,"该流程存在与之关联的模板，不能编辑");
        }
        if (size1 != 0 && size2 != 0){
            result.put(3,"该流程存在与之关联的实验和模板，不能编辑");
        }
        return result;
    }

    @Override
    public Integer workflowToComponent(String componentName, String tagString, String workflowDesc, String ggeditorObjectString, Integer userId) {
        WorkflowComponent workflowComponent = new WorkflowComponent();
        workflowComponent.setName(componentName);
        workflowComponent.setTag(tagString);
        workflowComponent.setWorkflowComponentDesc(workflowDesc);
        workflowComponent.setGgeditorObjectString(ggeditorObjectString);
        workflowComponent.setIsDeleted(Byte.parseByte("0"));
        workflowComponent.setCreateTime(new Date());
        workflowComponent.setFkUserId(userId);
        workflowComponentMapper.insert(workflowComponent);
        return workflowComponent.getId();
    }
}

