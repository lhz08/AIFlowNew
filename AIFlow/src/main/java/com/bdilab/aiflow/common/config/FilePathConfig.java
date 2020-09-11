package com.bdilab.aiflow.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class FilePathConfig {


    /**
     * 上传dataset地址
     */
    @Value("${user.dataset.path}")
    private String datasetUrl;

    public String getComponentYamlPath() {
        return componentYamlPath;
    }

    public void setComponentYamlPath(String componentYamlPath) {
        this.componentYamlPath = componentYamlPath;
    }

    public String getDatasetUrl(){
        return datasetUrl;
    }


    @Value("${component.yaml.path}")
    private String componentYamlPath;
}
