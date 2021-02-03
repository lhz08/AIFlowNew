package com.bdilab.aiflow.vo;

import java.util.Date;

/**
 * @author smile
 * @data 2020/11/11 16:52
 **/
public class ExperimentRunningVO {
    public Integer getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Integer fkUserId) {
        this.fkUserId = fkUserId;
    }

    private Integer id;

    private Integer fkUserId;

    private Integer runningStatus;


    private Integer isDeleted;


    private Integer fkExperimentId;


    private Date startTime;


    private Date endTime;
    private String conversationId;

    private String ggeditorObjectString;

    public String getGgeditorObjectString() {
        return ggeditorObjectString;
    }

    public void setGgeditorObjectString(String ggeditorObjectString) {
        this.ggeditorObjectString = ggeditorObjectString;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(Integer runningStatus) {
        this.runningStatus = runningStatus;
    }


    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }


    public Integer getFkExperimentId() {
        return fkExperimentId;
    }


    public void setFkExperimentId(Integer fkExperimentId) {
        this.fkExperimentId = fkExperimentId;
    }


    public Date getStartTime() {
        return startTime;
    }


    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }


    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
