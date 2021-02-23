package com.bdilab.aiflow.model.component;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 组件的输入桩创建信息
 */
@Data
public class InputStubInfo {

    private String inputName;

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

    private String inputType;

}
