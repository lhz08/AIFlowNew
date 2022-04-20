package com.bdilab.aiflow.model.job;

public class ApiTrigger {
    /*Trigger defines what starts a pipeline run.*/

    private ApiCronSchedule cron_schedule;
    private ApiPeriodicSchedule periodic_schedule;

    public ApiCronSchedule getCron_schedule() {
        return cron_schedule;
    }

    public void setCron_schedule(ApiCronSchedule cron_schedule) {
        this.cron_schedule = cron_schedule;
    }

    public ApiPeriodicSchedule getPeriodic_schedule() {
        return periodic_schedule;
    }

    public void setPeriodic_schedule(ApiPeriodicSchedule periodic_schedule) {
        this.periodic_schedule = periodic_schedule;
    }
}
