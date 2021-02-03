package com.bdilab.aiflow.vo;

import java.util.Date;
import java.util.List;

/**
 * 向前端传递的
 */
public class WorkflowVO {
    private Integer id;

    private String name;

    private String tags;

    private String workflowXml;

    private String ggeditorObejectString;

    private Date createTime;

    private String workflowDesc;

    public List<Object> customComponentIdList;

    public List<Object> experimentList;

    public Integer getId() { return id; }

    public String getName() { return name; }

    public String getTags() { return tags; }

    public String getGgeditorObejectString() { return ggeditorObejectString; }

    public Date getCreateTime() { return createTime; }

    public String getWorkflowDesc() { return workflowDesc; }

    public void setId(Integer id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setTags(String tags) { this.tags = tags; }

    public void setWorkflowXml(String workflowXml) { this.workflowXml = workflowXml; }

    public void setGgeditorObejectString(String ggeditorObejectString) { this.ggeditorObejectString = ggeditorObejectString; }

    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public void setWorkflowDesc(String workflowDesc) { this.workflowDesc = workflowDesc; }

    public String getWorkflowXml() { return workflowXml; }
}
