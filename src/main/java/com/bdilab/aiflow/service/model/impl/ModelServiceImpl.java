package com.bdilab.aiflow.service.model.impl;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.sse.ProcessSseEmitters;
import com.bdilab.aiflow.common.utils.RandomNum;
import com.bdilab.aiflow.mapper.*;
import com.bdilab.aiflow.model.*;
import com.bdilab.aiflow.service.model.ModelService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Service
public class ModelServiceImpl implements ModelService {

    @Resource
    ModelMapper modelMapper;
    @Resource
    ExperimentRunningMapper experimentRunningMapper;
    @Resource
    ExperimentMapper experimentMapper;
    @Resource
    WorkflowMapper workflowMapper;
    @Resource
    ComponentInfoMapper componentInfoMapper;
    @Resource
    CustomComponentMapper customComponentMapper;
    @Resource
    ComponentParameterMapper componentParameterMapper;

   /* @Value("${minio.host}")
    private String host;

    @Value("${minio.access_key}")
    private String username;

    @Value("${minio.secret_key}")
    private String password;*/
    @Override
    public boolean createModel(Integer modelId,String modelName, String modelDesc) {
        Model model=new Model();
        model.setId(modelId);
        model.setName(modelName);
        model.setIsSaved(1);
        model.setIsDeleted((byte) 0);
        model.setModelDesc(modelDesc);
        return modelMapper.updateModel(model)==1;
    }


    /*分页获得模型信息列表*/
    @Override
    public Map<String, Object> getModelByUser(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Model> modelList = modelMapper.getModelByUser(userId);
        PageInfo pageInfo = new PageInfo<>(modelList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Model List",modelList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*编辑模型信息*/
    @Override
    public boolean editModel(Integer modelId, String modelName, String modelDesc) {
        Model model=modelMapper.selectModelById(modelId);
        if(model.getName().equals(modelName) && model.getModelDesc().equals(modelDesc)){
            return true;
        }
        model.setName(modelName);
        model.setModelDesc(modelDesc);
        return modelMapper.editModel(model);
    }

    /*删除模型--移入回收站*/
    @Override
    public boolean deleteModelById(Integer modelId) {
        return modelMapper.deleteModelById(modelId);
    }

    /*删除模型--彻底删除*/
    @Override
    public boolean deleteModelCompletelyById(Integer modelId)  {
        Model model = modelMapper.selectModelById(modelId);
        if(model.getModelFileAddr()!=null) {
            if(!model.getModelFileAddr().equals("")) {
                File file = new File(model.getModelFileAddr());
                file.delete();
            }
        }
        return modelMapper.deleteModelCompletelyById(modelId);
    }

    /*分页获取回收站中的模型列表*/
    @Override
    public Map<String, Object> getModelInTrash(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Model> modelList = modelMapper.getModelInTrash(userId);
        System.out.println(modelList);
        PageInfo pageInfo = new PageInfo<>(modelList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Model List",modelList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*从回收站恢复模型*/
    @Override
    public boolean restoreModel(Integer modelId) {
        return modelMapper.restoreModel(modelId);
    }


    /*按名称分页搜索模型*/
    @Override
    public Map<String, Object> searchModelByName(Integer userId, String modelName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Model> modelList = modelMapper.fuzzySelectModelByName(userId,modelName);
        PageInfo pageInfo = new PageInfo<>(modelList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("model List",modelList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*下载模型*/
    @Override
    public File downloadDataset(Integer modelId) {
        Model model = modelMapper.selectModelById(modelId);
        File file = new File(model.getModelFileAddr());
        return file;
    }

    @Override
    public List<Model> getAllModelByRunningIdAndIsDeleted(Integer runningId,Byte isDeleted){

        return modelMapper.getAllModelByRunningIdAndIsDeleted(runningId,isDeleted);
    }

    @Override
    public boolean setRunningIdNull(Integer modelId){
        boolean isSuccess=modelMapper.updateRunningIdNull(modelId)==1;
        return isSuccess;
    }

    /**
     * 根据模型查到生成该模型的组件，该组件的镜像包括训练和测试两部分，
     * 将模型封装成组件实际上用的还是生成该模型的组件的镜像，封装的组件除了id，名称，描述等字段和原组件不同，其他都和原组件相同。
     * @param modelId
     * @param userId
     * @param componentName
     * @param componentDesc
     * @return
     */
    @Override
    public boolean setModelToComponent(Integer modelId,Integer userId,String componentName,String componentDesc,String tag){
        ComponentInfo componentInfo = new ComponentInfo();
        Model model = modelMapper.selectModelById(modelId);
        ComponentInfo componentInfo1 = componentInfoMapper.selectComponentInfoById(model.getFkComponentId());
        CustomComponent customComponent = new CustomComponent();
        componentInfo.setName(componentInfo1.getName()+ RandomNum.generateRandomNum());
        componentInfo.setComponentDesc(componentName);
        componentInfo.setTags(tag);
        componentInfo.setIsCustom((byte) 1);
        componentInfo.setComponentYamlAddr(componentInfo1.getComponentYamlAddr());
        componentInfo.setInputStub(componentInfo1.getInputStub());
        componentInfo.setOutputStub(componentInfo1.getOutputStub());
        componentInfoMapper.insertComponentInfo(componentInfo);
        ComponentParameter componentParameter = new ComponentParameter();
        componentParameter.setParameterType("2");
        componentParameter.setName("modeladdr");
        componentParameter.setParameterDesc("modeladdr");
        componentParameter.setDefaultValue(model.getModelFileAddr());
        componentParameter.setFkComponentInfoId(componentInfo.getId());
        List<ComponentParameter> componentParameters = new ArrayList<>();
        componentParameters.add(componentParameter);
        componentParameterMapper.insertComponentParam(componentParameters);
        customComponent.setFkUserId(userId);
        customComponent.setFkComponentInfoId(componentInfo.getId());
        customComponent.setIsDeleted((byte) 0);
        customComponent.setType((byte) 2);
        customComponent.setSourceId(modelId.toString());
        customComponent.setCreateTime(new Date());
        customComponentMapper.insertCustomComponent(customComponent);
        return true;
    }

    @Override
    public boolean saveModel(String runningId, String componentId, String conversationId,String modelFileAddr) {
        ExperimentRunning experimentRunning = experimentRunningMapper.selectExperimentRunningByRunningId(Integer.parseInt(runningId));
        Integer experimentId = experimentRunning.getFkExperimentId();
        Experiment experiment = experimentMapper.selectExperimentById(experimentId);
        Workflow workflow = workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        ComponentInfo componentInfo = componentInfoMapper.selectComponentInfoById(Integer.parseInt(componentId));
        Model model = new Model();
        model.setFkUserId(workflow.getFkUserId());
        model.setFkComponentId(Integer.parseInt(componentId));
        model.setFkRunningId(Integer.parseInt(runningId));
        model.setIsSaved(0);
        model.setCreateTime(new Date());
        model.setModelFileAddr(modelFileAddr);
        model.setIsDeleted((byte) 0);
        //插入数据库
        modelMapper.insertModel(model);
        System.out.println(model.getId());
        //推送消息
        experimentRunning.setFkModelId(model.getId());
        experimentRunningMapper.updateExperimentRunning(experimentRunning);
        Map<String,String> data = new HashMap<>(2);
        data.put("taskName",componentInfo.getName());
        data.put("status","saving model");
        ProcessSseEmitters.sendEvent(conversationId,new ResponseResult(true,"005","成功保存模型",data));
        return true;
    }
   /* @Override
    public HttpServletResponse downloadModelFromMinio(Integer userId, Integer modelId, HttpServletResponse response){
        MinioFileUtils minioFileUtils = new MinioFileUtils(host,username,password,false);
        Model model = modelMapper.selectModelById(modelId);
        String bucketName = "user"+userId;
        String filePath = model.getModelFileAddr();
        try {
            InputStream inputStream = minioFileUtils.downLoadFile(bucketName, filePath);
            byte buf[] = new byte[1024];
            int length = 0;
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            OutputStream outputStream = response.getOutputStream();
            while ((length = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, length);
            }
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();

        }
        return response;
    }*/

    /**
     * @Author Lei junting
     * 根据模型的运行id获取实验，定位模型
     * @param modelId
     * @return
     */
    @Override
    public Integer getRunningId(Integer modelId){
        return modelMapper.getExperienceId(modelId);
    }
}
