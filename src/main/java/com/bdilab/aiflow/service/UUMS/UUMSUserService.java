package com.bdilab.aiflow.service.UUMS;


import com.bdilab.aiflow.model.User;

import com.bdilab.aiflow.model.application.Application;
import com.bdilab.aiflow.model.menu.TransferAmenu;
import com.bdilab.aiflow.model.userManage.UUMSUser;

import java.util.List;

public interface UUMSUserService {

    void uumsUserRegister(List<UUMSUser> userList);

    void uumsApplicationRegister(List<Application> applicationList);

    List<Application> getUumsUserApplication(Long userId);

    List<TransferAmenu> selectParentMenus();

    List<TransferAmenu> selectMenusByParentcode(String code);

    User uumsUserLogin(String username);

    String selectDisplayLimitStr();

}
