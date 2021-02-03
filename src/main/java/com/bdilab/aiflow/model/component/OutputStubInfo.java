package com.bdilab.aiflow.model.component;

import lombok.Data;

/**
 * 组件的输出桩创建信息
 */
@Data
public class OutputStubInfo {

    private String outputName;

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    private String outputType;
}
