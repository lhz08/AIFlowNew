package com.bdilab.aiflow.model.component;

import com.bdilab.aiflow.model.ComponentInfo;
import com.bdilab.aiflow.model.ComponentParameter;
import lombok.Data;

import java.util.List;

/**
 * @Description 专门创建组件的实体组合类
 */
@Data
public class ComponentCreateInfo {

    private Byte componentType;
    private String sourceId;
    private List<InputStubInfo> inputStubInfoList;
    private List<OutputStubInfo> outputStubInfoList;
    private ComponentInfo componentInfo;
    private List<ComponentParameter> componentParamList;

}
