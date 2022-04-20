package com.bdilab.aiflow.model.job;

import lombok.Data;

@Data
public class CronSchedule {
   /* start_time: string (date-time)*/
    private String start_time;

    /*end_time: string (date-time)*/
    private String date_time;

   /* cron: string*/
    private String cron;
}
