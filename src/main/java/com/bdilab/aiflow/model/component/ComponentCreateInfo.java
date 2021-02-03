package com.bdilab.aiflow.model.component;

import com.bdilab.aiflow.model.ComponentInfo;
import com.bdilab.aiflow.model.ComponentParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 专门创建组件的实体组合类
 */
@Data
public class ComponentCreateInfo {

    /**
     * 组件类型:
     * 0-自定义算法组件
     * 1-自定义流程组件
     * 2-自定义模型组件
     */
    private Byte componentType;
    /**
     * 组件源
     * 算法组件：用户id
     * 流程组件：来源流程id
     * 模型组件：来源模型id
     */
    private String sourceId;
    /**
     * 组件输入桩列表：
     * 输入桩的参数名，参数类型
     * InputStubInfo:inputName,inputType
     */
    private List<InputStubInfo> inputStubInfoList;
    /**
     * 组件输出桩列表:
     * 输出桩的参数名，参数类型
     * OutputStubInfo:outputName,outputType
     */
    private List<OutputStubInfo> outputStubInfoList;
    /**
     * 组件信息ComponentInfo：
     * 组件的id，名称，标签(逗号分隔)，是否为自定义(0-公共，1-自定义)，Yaml地址，输入桩({名:类型})，输出桩({名：类型})
     * id,name,tags,isCustom,componentYamlAddr,inputStub,outputStub,
     * 图片类型，描述，中文名
     * graphTypeIds,componentDesc,componentNameChs
     */
    private ComponentInfo componentInfo;
    /**
     * 组件包含可变参
     * 每个参数ComponentParameter：
     * 参数的id，名称，所属组件id，参数类型(0数值，1枚举，2自定义类型)，默认值，参数描述
     * id,name,fkComponentInfoId,parameterType,defaultValue,parameterDesc
     */
    private List<ComponentParameter> componentParamList;

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
