package com.bdilab.aiflow.model.component;

import com.bdilab.aiflow.model.ComponentInfo;
import com.bdilab.aiflow.model.ComponentParameter;

import java.util.List;

/**
 * @Description 专门创建组件的实体组合类
 */

public class ComponentCreateInfo {

    private Byte componentType;
    private String sourceId;
    private List<InputStubInfo> inputStubInfoList;
    private List<OutputStubInfo> outputStubInfoList;
    private ComponentInfo componentInfo;
    private List<ComponentParameter> componentParamList;

    @java.lang.Override
    public java.lang.String toString() {
        return "ComponentCreateInfo{" +
                "componentType=" + componentType +
                ", sourceId='" + sourceId + '\'' +
                ", inputStubInfoList=" + inputStubInfoList +
                ", outputStubInfoList=" + outputStubInfoList +
                ", componentInfo=" + componentInfo +
                ", componentParamList=" + componentParamList +
                '}';
    }

    public Byte getComponentType() {
        return componentType;
    }

    public void setComponentType(Byte componentType) {
        this.componentType = componentType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public List<InputStubInfo> getInputStubInfoList() {
        return inputStubInfoList;
    }

    public void setInputStubInfoList(List<InputStubInfo> inputStubInfoList) {
        this.inputStubInfoList = inputStubInfoList;
    }

    public List<OutputStubInfo> getOutputStubInfoList() {
        return outputStubInfoList;
    }

    public void setOutputStubInfoList(List<OutputStubInfo> outputStubInfoList) {
        this.outputStubInfoList = outputStubInfoList;
    }

    public ComponentInfo getComponentInfo() {
        return componentInfo;
    }

    public void setComponentInfo(ComponentInfo componentInfo) {
        this.componentInfo = componentInfo;
    }

    public List<ComponentParameter> getComponentParamList() {
        return componentParamList;
    }

    public void setComponentParamList(List<ComponentParameter> componentParamList) {
        this.componentParamList = componentParamList;
    }
}
