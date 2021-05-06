package com.bdilab.aiflow.model;

import java.util.Date;

public class ExperimentRunningJsonResult {

    private Integer id;
    private Integer fkExperimentRunningId;
    private Integer fkComponentInfoId;
    private String mapConfigString;
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

    public Integer getFkComponentInfoId() {
        return fkComponentInfoId;
    }

    public void setFkComponentInfoId(Integer fkComponentInfoId) {
        this.fkComponentInfoId = fkComponentInfoId;
    }

    public String getMapConfigString() {return mapConfigString;}

    public void setMapConfigString(String mapConfigString) {this.mapConfigString = mapConfigString; }

    public String getResultJsonString() { return resultJsonString; }

    public void setResultJsonString(String resultJsonString) { this.resultJsonString = resultJsonString; }

    public Date getCreateTime() { return createTime; }

    public void setCreateTime(Date createTime) { this.createTime = createTime; }

}