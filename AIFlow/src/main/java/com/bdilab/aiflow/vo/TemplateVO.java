package com.bdilab.aiflow.vo;


/**
 * 向前端传递
 */
public class TemplateVO {

    private Integer id;

    private String name;

    private Integer type;

    private Integer fkUserId;

    private Integer fkWorkflowId;

    private Integer fkExperimentId;

    private String tags;

    private Integer isDeleted;

    private Integer experimentIsDeleted;

    private String workflowXml;

    private String paramJsonString;

    private String ggeditorObjectString;

    private String templateDesc;




    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

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

    public Integer getFkWorkflowId() {
        return fkWorkflowId;
    }

    public void setFkWorkflowId(Integer fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    public Integer getFkExperimentId() {
        return fkExperimentId;
    }

    public void setFkExperimentId(Integer fkExperimentId) {
        this.fkExperimentId = fkExperimentId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getExperimentIsDeleted() {
        return experimentIsDeleted;
    }

    public void setExperimentIsDeleted(int experimentIsDeleted) {
        this.experimentIsDeleted = experimentIsDeleted;
    }


    public String getParamJsonString() {
        return paramJsonString;
    }

    public void setParamJsonString(String paramJsonString) {
        this.paramJsonString = paramJsonString;
    }

    public String getGgeditorObjectString() {
        return ggeditorObjectString;
    }

    public void setGgeditorObjectString(String ggeditorObjectString) {
        this.ggeditorObjectString = ggeditorObjectString;
    }

    public String getTemplateDesc() {
        return templateDesc;
    }

    public void setTemplateDesc(String templateDesc) {
        this.templateDesc = templateDesc;
    }

    public String getWorkflowXml() {
        return workflowXml;
    }

    public void setWorkflowXml(String workflowXml) {
        this.workflowXml = workflowXml;
    }
}
