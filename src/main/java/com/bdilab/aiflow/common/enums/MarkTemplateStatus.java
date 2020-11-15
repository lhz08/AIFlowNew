package com.bdilab.aiflow.common.enums;

public enum MarkTemplateStatus {

    /**
     * 存在状态
     *
     * 0-未删除
     *
     * 1-已删除，在回收站中显示
     *
     */

    NOTMARKED(0,"未标记成模板"),
    MARKED(1,"已标记成模板");
    private Integer value;
    private String status;
    MarkTemplateStatus(Integer value, String status){
        this.value=value;
        this.status=status;
    }

    public Integer getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public static MarkTemplateStatus getMarkTemplateStatusById(Integer id){
        switch (id){
            case 0:return NOTMARKED;
            case 1:return MARKED;
            default:return null;
        }
    }
}
