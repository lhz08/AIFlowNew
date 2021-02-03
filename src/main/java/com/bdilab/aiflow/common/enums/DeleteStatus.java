package com.bdilab.aiflow.common.enums;

/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/29 12:07
 * @Version 1.0
 **/

public enum DeleteStatus {
    /**
     * 存在状态
     *
     * 0-未删除
     *
     * 1-已删除，在回收站中显示
     *
     */

    NOTDELETED(0,"未删除"),
    DELETED(1,"已删除");
    private Integer value;
    private String status;
    DeleteStatus(Integer value, String status){
        this.value=value;
        this.status=status;
    }

    public Integer getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public static DeleteStatus getDeleteStatusById(Integer id){
        switch (id){
            case 0:return NOTDELETED;
            case 1:return DELETED;
            default:return null;
        }
    }
}
