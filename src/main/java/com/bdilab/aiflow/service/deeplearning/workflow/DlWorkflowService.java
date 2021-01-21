package com.bdilab.aiflow.service.deeplearning.workflow;

import com.bdilab.aiflow.model.workflow.EpochInfo;

import java.util.Map;

/**
 * @author smile
 * @data 2021/1/6 15:37
 **/


public interface DlWorkflowService {

    public Map generateDLPipeline(String workflowXmlAddr,Integer userId);



}
