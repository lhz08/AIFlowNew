package com.bdilab.aiflow.model.userManage;


import com.bdilab.aiflow.model.application.Application;
import lombok.Data;

import java.util.List;


@Data
public class UUMSUserInfo {
    /**
     * MD5加密签名
     */
    private String sign;

    /**
     * User对象集合
     */
    private List<UUMSUser> userList;

    /**
     * 全部应用对象集合
     */
    private List<Application> applicationList;
}
