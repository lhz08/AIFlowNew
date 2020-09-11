package com.bdilab.aiflow.model;

import java.util.Date;

public class Dataset {

    private Integer id;
    private String name;
    private Integer type;
    private Integer fkUserId;
    private String tags;
    private Byte isDeleted;
    private String datasetAddr;
    private Date createTime;
    private String datasetDesc;

    @Override
    public String toString() {
        return "Dataset{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", fkUserId=" + fkUserId +
                ", tags='" + tags + '\'' +
                ", isDeleted=" + isDeleted +
                ", datasetAddr='" + datasetAddr + '\'' +
                ", createTime=" + createTime +
                ", datasetDesc='" + datasetDesc + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Integer fkUserId) {
        this.fkUserId = fkUserId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getDatasetAddr() {
        return datasetAddr;
    }

    public void setDatasetAddr(String datasetAddr) {
        this.datasetAddr = datasetAddr;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDatasetDesc() {
        return datasetDesc;
    }

    public void setDatasetDesc(String datasetDesc) {
        this.datasetDesc = datasetDesc;
    }
}