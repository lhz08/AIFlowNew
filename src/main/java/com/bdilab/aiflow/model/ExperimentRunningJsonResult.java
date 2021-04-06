package com.bdilab.aiflow.model;

import java.util.Date;

public class ExperimentRunningJsonResult {

    private Integer id;
    private Integer fkExperimentRunningId;
    private String resultJsonString;
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFkExperimentRunningId() {
        return fkExperimentRunningId;
    }

    public void setFkExperimentRunningId(Integer fkExperimentRunningId) {
        this.fkExperimentRunningId = fkExperimentRunningId;
    }

    public String getResultJsonString() {
        return resultJsonString;
    }

    public void setResultJsonString(String resultJsonString) {
        this.resultJsonString = resultJsonString;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}