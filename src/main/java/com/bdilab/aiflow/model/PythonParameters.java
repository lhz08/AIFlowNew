package com.bdilab.aiflow.model;

import java.util.List;
import java.util.Map;

/**
 * @Decription TODO
 * @Author Humphrey
 * @Date 2019/9/10 11:22
 * @Version 1.0
 **/
public class PythonParameters {

    private String curTaskId;
    private String processInstanceId;
    private String curTaskName;
    private String modelPath;
    /**
     * 前置节点
     */
    private List<String> priorIds;

    private List<String> rearIdS;

    private boolean isEnd;

    private Map<String,String> parameters;

    public void setCurTaskId(String curTaskId) {
        this.curTaskId = curTaskId;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getCurTaskId() {
        return curTaskId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public List<String> getRearIdS() {
        return rearIdS;
    }

    public void setRearIdS(List<String> rearIdS) {
        this.rearIdS = rearIdS;
    }

    public void setPriorIds(List<String> priorIds) {
        this.priorIds = priorIds;
    }
    public void setRearIds(List<String> rearIds) {
        this.rearIdS = rearIds;
    }
    public List<String> getPriorIds() {
        return priorIds;
    }

    public void setIsEnd(boolean end) {
        isEnd = end;
    }

    public boolean getIsEnd() {
        return isEnd;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getCurTaskName() {
        return curTaskName;
    }

    public void setCurTaskName(String curTaskName) {
        this.curTaskName = curTaskName;
    }
}
