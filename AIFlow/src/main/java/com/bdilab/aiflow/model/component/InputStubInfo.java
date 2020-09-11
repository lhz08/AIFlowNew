package com.bdilab.aiflow.model.component;



/**
 * 组件的输入桩创建信息
 */

public class InputStubInfo {

    private String inputName;

    private String inputType;

    @java.lang.Override
    public java.lang.String toString() {
        return "InputStubInfo{" +
                "inputName='" + inputName + '\'' +
                ", inputType='" + inputType + '\'' +
                '}';
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }
}
