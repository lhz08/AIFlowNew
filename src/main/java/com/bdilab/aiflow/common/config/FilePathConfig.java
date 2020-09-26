package com.bdilab.aiflow.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class FilePathConfig {


    public void setDatasetUrl(String datasetUrl) {
        this.datasetUrl = datasetUrl;
    }

    public String getComponentYamlPath() {
        return componentYamlPath;
    }

    public void setComponentYamlPath(String componentYamlPath) {
        this.componentYamlPath = componentYamlPath;
    }

    public String getPipelineCodePath() {
        return pipelineCodePath;
    }

    public void setPipelineCodePath(String pipelineCodePath) {
        this.pipelineCodePath = pipelineCodePath;
    }

    public String getWorkflowXmlFilePath() {
        return workflowXmlFilePath;
    }

    public void setWorkflowXmlFilePath(String workflowXmlFilePath) {
        this.workflowXmlFilePath = workflowXmlFilePath;
    }

    /**
     * 上传dataset地址
     */
    @Value("${user.dataset.path}")
    private String datasetUrl;


    public String getDatasetUrl(){
        return datasetUrl;
    }


    @Value("${component.yaml.path}")
    private String componentYamlPath;

    //生成的pipeline python代码存放文件地址
    @Value("${pipline.pythonCode.path}")
    private String pipelineCodePath;

    @Value("${workflow.xmlFile.path}")
    private String workflowXmlFilePath;

}
