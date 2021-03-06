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

    public String getComponentResultPath() {
        return componentResultPath;
    }

    public void setComponentResultPath(String componentResultPath) {
        this.componentResultPath = componentResultPath;
    }

    public String getDatasetPath() {
        return datasetPath;
    }

    public void setDatasetPath(String datasetPath) {
        this.datasetPath = datasetPath;
    }



    /**
     * 上传dataset地址
     */
    @Value("${user.dataset.path}")
    private String datasetUrl;

    public String getDatasetUrl(){
        return datasetUrl;
    }

    /**
     * 存放临时文件路径
     */
    @Value("${dlDataset.path}")
    private String dlDatasetPath;

    public String getDlDatasetPath() {
        return dlDatasetPath;
    }

    public void setDlDatasetPath(String dlDatasetPath) {
        this.dlDatasetPath = dlDatasetPath;
    }




    @Value("${component.yaml.path}")
    private String componentYamlPath;

    //生成的pipeline python代码存放文件地址
    @Value("${pipline.pythonCode.path}")
    private String pipelineCodePath;

    @Value("${workflow.xmlFile.path}")
    private String workflowXmlFilePath;

    @Value("${component.result.path}")
    private String componentResultPath;

    @Value("${dataset.path}")
    private String datasetPath;

    @Value("${nfs.path}")
    private String data_dir;


    public String getData_dir() {
        return data_dir;
    }

    public void setData_dir(String data_dir) {
        this.data_dir = data_dir;
    }
}
