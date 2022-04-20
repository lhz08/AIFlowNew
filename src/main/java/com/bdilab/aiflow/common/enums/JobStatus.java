package com.bdilab.aiflow.common.enums;

public enum  JobStatus {
    ENABLED(0,"执行"),
    DISENABLED(1,"暂停");
    private Integer value;
    private String status;
    JobStatus(Integer value, String status){
        this.value=value;
        this.status=status;
    }

    public Integer getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public static JobStatus getDeleteStatusById(Integer id){
        switch (id){
            case 0:return ENABLED;
            case 1:return  DISENABLED;
            default:return null;
        }
    }
}
