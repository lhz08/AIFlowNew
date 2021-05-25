package com.bdilab.aiflow.model.userManage;


import com.bdilab.aiflow.model.application.Application;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class UUMSUser {
    /**
     * 用户名称
     */
    private String username;
    /**
     * 用户名
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱激活状态 0未激活 1激活
     */
    private Integer enabled;
    /**
     * 角色 0  admin  1  user
     */
    private Integer roleType;

    private Date createTime;

    private Date updateTime;
    /**
     * 当前用户拥有应用权限对象集合
     */
    private List<Application> applicationList;

}
