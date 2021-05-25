package com.bdilab.aiflow.service.UUMS.impl;

import com.bdilab.aiflow.mapper.UUMSUserMapper;
import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.model.application.Application;
import com.bdilab.aiflow.model.menu.TransferAmenu;
import com.bdilab.aiflow.model.userManage.UUMSUser;
import com.bdilab.aiflow.service.UUMS.UUMSUserService;
import com.github.pagehelper.util.StringUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UUMSUserServiceImpl implements UUMSUserService {

    @Autowired
    private UUMSUserMapper uumsUserMapper;

    @Override
    public void uumsUserRegister(List<UUMSUser> userList) {
        List<String> usernameList=new ArrayList<>();
        for (UUMSUser uumsUser : userList) {
            if (!StringUtil.isEmpty(uumsUser.getAccount())){
                usernameList.add(uumsUser.getAccount());
            }
        }

        List<String> exitsList=uumsUserMapper.selectByUsernameList(usernameList);
        //存在的直接更新
        User user=null;
        for (UUMSUser uumsUser : userList) {
            user=new User();
            user.setUserName(uumsUser.getAccount());
            user.setEmail(uumsUser.getEmail());
            if (StringUtil.isEmpty(uumsUser.getRoleType().toString())){
                uumsUser.setRoleType(0);
            }
            user.setType(uumsUser.getRoleType()==0?1:0);//一体化是 0 admin 1 user
            user.setEnabled(uumsUser.getEnabled());
            user.setMobile(uumsUser.getMobile());
            user.setPassword(BCrypt.hashpw(uumsUser.getPassword(), BCrypt.gensalt()));
            user.setUserCode(uumsUser.getUsername());
            user.setStatus(1);

            if (exitsList.contains(uumsUser.getAccount())){
                //更新
                uumsUserMapper.updateByUsername(user);
            }else {
                //不存在的直接插入
                user.setCreateTime(new Date());
                uumsUserMapper.insert(user);
            }

            List<Application> applicationList=uumsUser.getApplicationList();
            Long userId=uumsUserMapper.getIdByName(user.getUserName());
            uumsUserMapper.deleteUserApplicationByUserId(userId);
            Long appId=null;
            for (Application application : applicationList) {
                appId=uumsUserMapper.getAppIdByCodingId(application.getCodingId());
                uumsUserMapper.insertUserApplication(appId,userId);
            }
        }



    }

    @Override
    public void uumsApplicationRegister(List<Application> applicationList) {

        List<String> codingIdList=new ArrayList<>();
        for (Application application : applicationList) {
            if (!StringUtil.isEmpty(application.getCodingId())){
                codingIdList.add(application.getCodingId());
            }
        }

        List<String> exitsList=uumsUserMapper.selectByCodingIdList(codingIdList);

        for (Application application : applicationList) {
            if (exitsList.contains(application.getCodingId())){
                //更新
                uumsUserMapper.updateApplicationByCodingId(application);
            }else {
                //不存在的直接插入
                uumsUserMapper.insertApplication(application);
            }
        }
    }

    @Override
    public List<TransferAmenu> selectParentMenus() {
        return uumsUserMapper.selectParentMenus();
    }

    @Override
    public List<TransferAmenu> selectMenusByParentcode(String code) {
        return uumsUserMapper.selectMenusByParentcode(code);
    }


    @Override
    public User uumsUserLogin(String username) {
        return null;
    }

    @Override
    public String selectDisplayLimitStr() {
        return null;
    }

    @Override
    public List<Application> getUumsUserApplication(Long userId) {
        return uumsUserMapper.getUumsUserApplication(userId);
    }


}
