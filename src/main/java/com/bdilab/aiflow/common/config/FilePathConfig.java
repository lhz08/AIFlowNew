package com.bdilab.aiflow.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class FilePathConfig {


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
}
