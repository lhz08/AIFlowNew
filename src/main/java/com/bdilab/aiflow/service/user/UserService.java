package com.bdilab.aiflow.service.user;

import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.vo.UserInfoVO;

import java.util.List;
import java.util.Map;

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
     * @param userId 返回的所有用户中不包括传入的userId这个用户，如果要所有用户，可以传入null
     * @return List<User>
     */
    List<UserInfoVO> getAllUsers(Integer userId);
    /**
     * 登录验证
     * @param username
     * @param password
     * @return
     */
    User userLoginCheck(String username, String password);

    int updateUserInfo(Map<String,Object> map);

    int deleteUser(Integer id);
}
