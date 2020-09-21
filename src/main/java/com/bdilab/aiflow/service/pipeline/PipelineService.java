package com.bdilab.aiflow.service.pipeline;

import org.springframework.stereotype.Service;

/**
 * @author smile
 * @data 2020/9/15 10:09
 **/
public interface PipelineService {

    public void generatePipeline(Integer userId,String xmlPath,String name);
    public String generateCode(String json);
}

