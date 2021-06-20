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
import java.util.Date;
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
            if(user.getStatus() == 0)
                return new ResponseResult(false, "003", "用户已被禁用，请联系管理员", null);
            httpSession.setAttribute("user_id", user.getId());
            httpSession.setAttribute("name", user.getUserName());
            httpSession.setAttribute("type",user.getType());
            Map<String, Object> data = new HashMap<>(2);
            data.put("uid",user.getId());
            data.put("name",user.getUserName());
            data.put("type",user.getType());
            return new ResponseResult(true, "001", "登录成功", data);
        } else
            return new ResponseResult(false, "002", "用户名或密码错误", null);
    }

    @ResponseBody
    @ApiOperation("用户注册")
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ResponseResult register(@RequestParam @ApiParam(value = "用户名") String username,
                                   @RequestParam @ApiParam(value = "密码") String password,
                                   @RequestParam(required = false) @ApiParam(value = "邮箱") String email,
                                   @RequestParam(required = false) @ApiParam(value = "手机号") String mobile
    )throws IOException, NoSuchAlgorithmException {
        User user = new User();
        if(userService.userRegisterCheck(username)!=null) {
            return new ResponseResult(false, "002", "注册失败，用户名已存在", null);
        }
        String md5Password = DecryptUtils.getMd5(password);
        user.setUserName(username);
        user.setPassword(md5Password);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setType(0);
        user.setCreateTime(new Date());
        user.setStatus(1);
        userService.createUser(user);
        return new ResponseResult(true, "001", "注册成功",username);
    }

    /**
     * 用户注销
     * @param httpSession
     * @return
     */
    @ResponseBody
    @ApiOperation("用户注销")
    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public ResponseResult logout(HttpSession httpSession) {
        httpSession.invalidate();
        return new ResponseResult(true,"001","注销成功");
    }
}
