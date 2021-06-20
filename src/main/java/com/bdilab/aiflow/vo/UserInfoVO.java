package com.bdilab.aiflow.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private Integer id;
    private String userName;

    /**
     * 角色：1 admin ； 0 user 与一体化相反
     */
    private Integer type;

    private Integer status;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 用户名称，对应一体化字段userName
     */
    private String userCode;
}
