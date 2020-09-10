package com.bdilab.aiflow.service.user.impl;

import com.bdilab.aiflow.mapper.UserMapper;
import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<User> getAllUsers() {
        List<User> users = userMapper.getAllUsers();

//        for (User user : users) {
//            System.out.println(user);
//        }

        return users;
    }
}
