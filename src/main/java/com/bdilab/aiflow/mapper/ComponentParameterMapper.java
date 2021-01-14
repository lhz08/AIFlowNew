package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.ComponentParameter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ComponentParameterMapper {

    int insertComponentParam(List<ComponentParameter> componentParam);

    int deleteComponentPermanently(List<Integer> fkComponentInfoIds);

    /**
     * 根据组件id检索组件参数信息
     * @param componentInfoId
     * @return
     */
    List<ComponentParameter> selectComponentParameterByComponentId(Integer componentInfoId);
}