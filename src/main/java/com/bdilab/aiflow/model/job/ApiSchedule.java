package com.bdilab.aiflow.model.job;

import lombok.Data;

@Data
public class ApiSchedule {
    /*date-time*/
    private String start_time;
    /*date-time*/
    private String end_time;
    /*date-time*/
    private String scheduleTime;
}
