package com.bdilab.aiflow.model.component;

import lombok.Data;

import java.util.Date;

@Data
public class CustomComponentInfo {
    private Integer id;

    private Date createTime;

    public String getGgeditorObjectString() {
        return ggeditorObjectString;
    }

    public void setGgeditorObjectString(String ggeditorObjectString) {
        this.ggeditorObjectString = ggeditorObjectString;
    }

    private String name;

    private String ggeditorObjectString;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String tags;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getComponentDesc() {
        return componentDesc;
    }

    public void setComponentDesc(String componentDesc) {
        this.componentDesc = componentDesc;
    }

    private String componentDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
