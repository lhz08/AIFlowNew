package com.bdilab.aiflow.service.component.impl;


import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.mapper.ComponentInfoMapper;
import com.bdilab.aiflow.mapper.ComponentParameterMapper;
import com.bdilab.aiflow.mapper.CustomComponentMapper;
import com.bdilab.aiflow.model.ComponentInfo;
import com.bdilab.aiflow.model.ComponentParameter;
import com.bdilab.aiflow.model.CustomComponent;
import com.bdilab.aiflow.model.component.ComponentCreateInfo;
import com.bdilab.aiflow.model.component.CustomComponentInfo;
import com.bdilab.aiflow.model.component.InputStubInfo;
import com.bdilab.aiflow.model.component.OutputStubInfo;
import com.bdilab.aiflow.service.component.CustomComponentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class CustomComponentServiceImpl implements CustomComponentService {

    @Autowired
    FilePathConfig filePathConfig;

    @Autowired
    CustomComponentMapper customComponentMapper;

    @Autowired
    ComponentInfoMapper componentInfoMapper;

    @Autowired
    ComponentParameterMapper componentParamMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean saveComponent(Integer userId, MultipartFile componentFile, ComponentCreateInfo componentCreateInfo) {

        ComponentInfo componentInfo = componentCreateInfo.getComponentInfo();
        List<ComponentParameter> componentParam = componentCreateInfo.getComponentParamList();

        //组装ComponentInfo对象
        componentInfo.setIsCustom((byte) 1);

        //组装、存储组件yaml文件
        if (!componentFile.isEmpty() && componentCreateInfo.getComponentType()!=2) {
            String direcrotyPath = "";
            switch (componentCreateInfo.getComponentType()) {
                case 1:
                    direcrotyPath = "algorithm"; break;
                case 3:
                    direcrotyPath = "model"; break;
                default:
                    direcrotyPath = "";
            }

            String yamlFilePath = filePathConfig.getComponentYamlPath() + File.separator +
                    direcrotyPath + File.separator + userId + "_" +componentCreateInfo.getComponentInfo().getName() + ".yaml";
            File dest = new File(yamlFilePath);
            try {
                componentFile.transferTo(dest);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            componentInfo.setComponentYamlAddr(yamlFilePath);
        }

        //组装inputStub对象
        StringBuilder inputStubBuilder = new StringBuilder();
        List<InputStubInfo> inputList = componentCreateInfo.getInputStubInfoList();
        for (InputStubInfo input : inputList) {
            inputStubBuilder.append(input.getInputName()+":");
            inputStubBuilder.append(input.getInputType()+",");
        }
        inputStubBuilder.deleteCharAt(inputStubBuilder.length()-1);
        componentInfo.setInputStub(inputStubBuilder.toString());

        //组装outputStub对象
        StringBuilder outputStubBuilder = new StringBuilder();
        List<OutputStubInfo> outputList = componentCreateInfo.getOutputStubInfoList();
        for (OutputStubInfo output : outputList) {
            outputStubBuilder.append(output.getOutputName()+":");
            outputStubBuilder.append(output.getOutputType()+",");
        }
        outputStubBuilder.deleteCharAt(outputStubBuilder.length()-1);
        componentInfo.setOutputStub(outputStubBuilder.toString());

        int isInfoSuccess = componentInfoMapper.insertComponentInfo(componentInfo);

        //组装ComponentParameter对象
        if (isInfoSuccess > 0) {
            for (ComponentParameter param : componentParam) {
                param.setFkComponentInfoId(componentInfo.getId());
            }
        } else {
            return false;
        }

        int isParamSuccess = componentParamMapper.insertComponentParam(componentParam);
        if (isParamSuccess > 0) {
            System.out.println(componentInfo);
            System.out.println(componentCreateInfo.getComponentParamList());

            //组装customComponent对象
            CustomComponent customComponent = new CustomComponent();
            customComponent.setFkUserId(userId);
            customComponent.setFkComponentInfoId(componentInfo.getId());
            customComponent.setIsDeleted((byte) 0);
            customComponent.setType(componentCreateInfo.getComponentType());
            customComponent.setSourceId(componentCreateInfo.getSourceId());
            customComponent.setCreateTime(new Date());

            int isCustomSuccess = customComponentMapper.insertCustomComponent(customComponent);
            if (isCustomSuccess > 0) {
                System.out.println(customComponent);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteComponent(Integer componentId) {
        int isDeleteSuccess = customComponentMapper.deleteComponent(componentId);
        if (isDeleteSuccess > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteComponentPermanently(List<Integer> componentIds) {

        int componentAmount = componentIds.size();
        System.out.println(componentIds);

        List<Integer> fkComponentInfoIds = customComponentMapper.selectFkComponentInfoIds(componentIds);
        System.out.println(fkComponentInfoIds);

        //删除custom_component表数据
        int isCustomDeleteSuccess = customComponentMapper.deleteComponentPermanently(componentIds);
        System.out.println("custom:"+isCustomDeleteSuccess);

        if (isCustomDeleteSuccess > 0) {
            //删除component_parameter表数据
            int isParamDeleteSuccess = componentParamMapper.deleteComponentPermanently(fkComponentInfoIds);
            System.out.println("param:"+isParamDeleteSuccess);
            if (isParamDeleteSuccess > 0) {

                //删除组件对应的yaml文件
                List<String> yamlFilePath = componentInfoMapper.selectComponentYamlAddr(fkComponentInfoIds);
                System.out.println("yamlFilePath:"+yamlFilePath);
                for (String path : yamlFilePath) {
                    if (path == null) {
                        continue;
                    }
                    File yamlFile = new File(path);
                    if (yamlFile.exists()) {
                        yamlFile.delete();
                        System.out.println(yamlFile);
                    } else {
                        System.out.println("组件yaml文件：" + yamlFile + " 不存在！删除失败！");
                    }
                }

                //删除component_info表数据
                int isInfoDeleteSuccess = componentInfoMapper.deleteComponentPermanently(fkComponentInfoIds);
                System.out.println("info:"+isInfoDeleteSuccess);
                if (isInfoDeleteSuccess == componentAmount) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean restoreComponent(List<Integer> componentIds) {
        int componentAmount = componentIds.size();
        int isRestoreSuccess = customComponentMapper.restoreComponent(componentIds);
        if (isRestoreSuccess == componentAmount) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public PageInfo<CustomComponentInfo> selectComponentByKeyword(String keyword, int type, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<CustomComponentInfo> customComponentList = customComponentMapper.selectCustomComponentByKeyword(keyword,type);
        PageInfo<CustomComponentInfo> pageInfo = new PageInfo<>(customComponentList);
        return pageInfo;
    }

    @Override
    public PageInfo<CustomComponentInfo> selectComponentByTag(String tag, int type, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<CustomComponentInfo> customComponentList = customComponentMapper.selectCustomComponentByTag(tag,type);
        PageInfo<CustomComponentInfo> pageInfo = new PageInfo<>(customComponentList);
        return pageInfo;
    }

    @Override
    public PageInfo<CustomComponentInfo> loadCustomComponentByUserIdAndType(int userId, int pageNum, int pageSize, int type) {
        PageHelper.startPage(pageNum, pageSize);
        System.out.println(userId);
        List<CustomComponentInfo> customComponentList = customComponentMapper.loadCustomComponentByUserIdAndType(userId, type);
        System.out.println(customComponentList);
        PageInfo<CustomComponentInfo> pageInfo = new PageInfo<>(customComponentList);
        return pageInfo;
    }

    @Override
    public PageInfo<CustomComponentInfo> loadPublicComponentInfo(int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<CustomComponentInfo> customComponentList = componentInfoMapper.loadPublicComponentInfo();
        System.out.println(customComponentList);
        PageInfo<CustomComponentInfo> pageInfo = new PageInfo<>(customComponentList);
        return pageInfo;
    }
}
