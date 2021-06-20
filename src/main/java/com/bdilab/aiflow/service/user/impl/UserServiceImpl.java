package com.bdilab.aiflow.service.user.impl;

import com.bdilab.aiflow.mapper.UserMapper;
import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.service.user.UserService;
import com.bdilab.aiflow.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/28 12:07
 * @Version 1.0
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User userRegisterCheck(String username){
        return userMapper.selectUserByName(username);
    }

    @Override
    public void createUser(User user){
        userMapper.insertUser(user);
    }

    @Override
    public List<UserInfoVO> getAllUsers(Integer userId) {
        List<UserInfoVO> users = userMapper.getAllUsers(userId);

//        for (User user : users) {
//            System.out.println(user);
//        }

        return users;
    }
    @Override
    public User userLoginCheck(String username, String password) {
        return userMapper.selectUserByNameAndPassword(username,password);

    }

    @Override
    public int updateUserInfo(Map<String,Object> map){
        return userMapper.update(map);
    }

    @Override
    public int deleteUser(Integer id) {
        return userMapper.deleteByPrimaryKey(id);
    }
}
