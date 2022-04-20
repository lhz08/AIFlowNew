package com.bdilab.aiflow.model.job;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class ExperimentJob {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer fkUserId;
    private Integer fkExperimentId;
    private String jobName;
    private String jobGroupName;
    private Integer status;
    private String jobId;
    private Date startTime;
    private Date endTime;
    /*提供两种形式的定时周期方式
    * 1、cron方式
    * */
    private String cronJobTime;
    /*提供两种形式的定时周期方式
     * 2、periodic方式
     * */
    private String periodicJobTime;
}
