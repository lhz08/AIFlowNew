package com.bdilab.aiflow.model.job;

public class ApiCronSchedule {

    /*date-time*/
    private String start_time;
    /*date-time*/
    private String end_time;
    /*date-time*/
    private String cron;

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}
