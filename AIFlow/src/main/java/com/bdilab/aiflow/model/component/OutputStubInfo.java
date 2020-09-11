package com.bdilab.aiflow.model.component;



/**
 * 组件的输出桩创建信息
 */

public class OutputStubInfo {

    private String outputName;

    private String outputType;

    @java.lang.Override
    public java.lang.String toString() {
        return "OutputStubInfo{" +
                "outputName='" + outputName + '\'' +
                ", outputType='" + outputType + '\'' +
                '}';
    }

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
}
