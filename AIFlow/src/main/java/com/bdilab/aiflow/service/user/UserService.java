package com.bdilab.aiflow.service.user;

import com.bdilab.aiflow.model.User;

import java.util.List;

/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/28 12:07
 * @Version 1.0
 **/
public interface UserService {

    /**
     * 注册检测
     * @param username
     * @return
     */
    User userRegisterCheck(String username);

    /**
     * 创建用户
     * @param user
     */
    void createUser(User user);

    /**
     * 获取所有用户
     * @param
     * @return List<User>
     */
    List<User> getAllUsers();

}
