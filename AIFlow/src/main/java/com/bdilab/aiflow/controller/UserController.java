package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/28 12:07
 * @Version 1.0
 **/

@Controller
@CrossOrigin
@Api(value="用户接口")
public class UserController {
    @Autowired
    private UserService userService;

    @ResponseBody
    @ApiOperation("用户注册")
    @RequestMapping(value = "/register",method = RequestMethod.POST)


    public ResponseResult register(     @RequestParam @ApiParam(value = "用户名") String username,
                                        @RequestParam @ApiParam(value = "密码") String password,
                                        HttpSession httpSession)throws IOException, NoSuchAlgorithmException {
        User user = new User();
        if(userService.userRegisterCheck(username)!=null) {
            return new ResponseResult(true, "002", "注册失败，用户名已存在", null);
        }
        user.setUserName(username);
        user.setPassword(password);
        userService.createUser(user);
        return new ResponseResult(true, "001", "注册成功",username);
    }
}
