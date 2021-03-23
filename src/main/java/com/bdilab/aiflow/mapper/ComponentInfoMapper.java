package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.ComponentInfo;
import com.bdilab.aiflow.model.component.CustomComponentInfo;
import com.bdilab.aiflow.model.component.CustomComponentInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ComponentInfoMapper {

    /**
     * 根据id检索组件信息
     * @param id
     * @return
     */
    ComponentInfo selectComponentInfoById(Integer id);

    int insertComponentInfo(ComponentInfo componentInfo);

    List<String> selectComponentYamlAddr(List<Integer> fkComponentInfoIds);

    int deleteComponentPermanently(List<Integer> componentIds);

    List<ComponentInfo> loadPublicComponentInfo(Integer isMl);
}