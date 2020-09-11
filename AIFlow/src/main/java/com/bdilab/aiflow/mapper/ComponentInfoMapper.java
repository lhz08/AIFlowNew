package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.ComponentInfo;

import java.util.List;

public interface ComponentInfoMapper {

    int insertComponentInfo(ComponentInfo componentInfo);

    List<String> selectComponentYamlAddr(List<Integer> fkComponentInfoIds);

    int deleteComponentPermanently(List<Integer> componentIds);

}