package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.utils.DecryptUtils;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @Decription TODO
 * @Author Jin Lingming
 * @Date 2020/08/28 12:07
 * @Version 1.0
 **/

@Api(value = "用户控制器")
@Controller
@CrossOrigin
public class UserController {

    /**
     * 用户登录
     * @param username
     * @param password
     * @param httpSession
     * @return
     */
    @Autowired
    UserService userService;
    @ResponseBody
    @ApiOperation("用户登录")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseResult login(@RequestParam @ApiParam(value = "用户名") String username,
                                @RequestParam @ApiParam(value = "密码") String password,
                                HttpSession httpSession) {
        String md5Password = DecryptUtils.getMd5(password);
        User user = userService.userLoginCheck(username,md5Password);
        if(user!=null){
            httpSession.setAttribute("user_id", user.getId());
            httpSession.setAttribute("name", user.getUserName());
            Map<String, Object> data = new HashMap<>(2);
            data.put("uid",user.getId());
            data.put("name",user.getUserName());
            return new ResponseResult(true, "001", "登录成功", data);
        } else
            return new ResponseResult(false, "002", "用户名或密码错误", null);
    }
}
