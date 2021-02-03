package com.bdilab.aiflow.common.enums;

public enum RunningStatus {
    /**
     * 运行状态
     *
     * 0-运行中
     *
     * 1-运行成功
     *
     * 2-运行失败
     */

    RUNNING(0,"运行中"),
    RUNNINGSUCCESS(1,"运行成功"),
    RUNNINGFAIL(2,"运行失败");
    private Integer value;
    private String status;
    RunningStatus(Integer value, String status){
        this.value=value;
        this.status=status;
    }

    public Integer getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public static RunningStatus getRunningStatusById(Integer id){
        switch (id){
            case 0:return RUNNING;
            case 1:return RUNNINGSUCCESS;
            case 2:return RUNNINGFAIL;
            default:return null;
        }
    }
}
