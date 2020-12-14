package com.bdilab.aiflow.vo;

import com.bdilab.aiflow.model.workflow.Variable;

import java.util.List;

/**
 * @author smile
 * @data 2020/9/26 15:56
 **/
public class ComponentInfoVO {
    private Integer componentId;
    private String componentName;
    private String mainType;
    private String componentNameChs;
    private String componentType;
    private String componentDesc;

    public String getComponentDesc() {
        return componentDesc;
    }

    public void setComponentDesc(String componentDesc) {
        this.componentDesc = componentDesc;
    }

    public String getGgeditorObjectString() {
        return ggeditorObjectString;
    }

    public void setGgeditorObjectString(String ggeditorObjectString) {
        this.ggeditorObjectString = ggeditorObjectString;
    }

    private List<Variable> variables;
    private String ggeditorObjectString;

    public String getMainType() {
        return mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public Integer getComponentId() {
        return componentId;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getComponentNameChs() {
        return componentNameChs;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public void setComponentNameChs(String componentNameChs) {
        this.componentNameChs = componentNameChs;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }
}
