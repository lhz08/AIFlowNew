package com.bdilab.aiflow.model.workflow;

import com.bdilab.aiflow.model.EnumValue;

import java.util.List;

/**
 * @Decription TODO
 * @Author Humphrey
 * @Date 2019/10/18 10:22
 * @Version 1.0
 **/
public class Variable {
    private String variableName;

    private Integer variableType;

    private String variableDes;

    private List<EnumValue> enumValues;

    private String defaultValue;

    public Integer getVariableType() {
        return variableType;
    }

    public List<EnumValue> getEnumValues() {
        return enumValues;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getVariableDes() {
        return variableDes;
    }

    public void setVariableDes(String variableDes) {
        this.variableDes = variableDes;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setEnums(List<EnumValue> enums) {
        this.enumValues = enums;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setVariableType(Integer variableType) {
        this.variableType = variableType;
    }
}
