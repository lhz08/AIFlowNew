package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.utils.DecryptUtils;
import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.service.user.UserService;
import com.bdilab.aiflow.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Decription TODO
 * @Author liran
 * @Date 2021/05/24 22:41
 * @Version 1.0
 **/

@Api(value = "管理员控制器")
@Controller
@CrossOrigin
public class AdminController {

    /**
     * 查看所有用户
     * @param httpSession
     * @return
     */
    @Autowired
    UserService userService;

    @ResponseBody
    @ApiOperation("获取所有用户列表")
    @RequestMapping(value = "/admin/getAllUsers",method = RequestMethod.GET)
    public ResponseResult getAllUsers(HttpSession httpSession) {
        if((Integer) httpSession.getAttribute("type")!=1){
            return new ResponseResult(false, "002", "无权限查看！", null);
        }
        List<UserInfoVO> allUsers = userService.getAllUsers(null);
        return new ResponseResult(true, "001", "查询成功", allUsers);
    }

    @ResponseBody
    @ApiOperation("编辑用户")
    @RequestMapping(value = "/admin/editUser",method = RequestMethod.POST)
    public ResponseResult editUser(@RequestParam @ApiParam(value = "用户id") Integer id,
                                   @RequestParam @ApiParam(value = "邮箱") String email,
                                   @RequestParam @ApiParam(value = "手机号") String mobile,
                                   @RequestParam @ApiParam(value = "一体化名称") String userCode,
                                   HttpSession httpSession) {
        if((Integer) httpSession.getAttribute("type")!=1){
            return new ResponseResult(false, "002", "无权限编辑！", null);
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("email",email);
        map.put("mobile",mobile);
        map.put("userCode",userCode);
        map.put("updateTime",new Date());
        try{
            userService.updateUserInfo(map);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(false, "003", "更新失败");
        }
        return new ResponseResult(true, "001", "更新成功");
    }

    @ResponseBody
    @ApiOperation("启用|禁用-用户")
    @RequestMapping(value = "/admin/changeUserStatus",method = RequestMethod.POST)
    public ResponseResult changeUserStatus(
            @RequestParam @ApiParam(value = "用户id") Integer id,
            @RequestParam @ApiParam(value = "用户状态") Integer status,
            HttpSession httpSession) {
        if((Integer) httpSession.getAttribute("type")!=1){
            return new ResponseResult(false, "002", "无权限操作！", null);
        }
        if(status!=0&&status!=1)return new ResponseResult(false, "003", "操作失败");
        HashMap<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("status",status);
        try{
            userService.updateUserInfo(map);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(false, "003", "操作失败");
        }
        return new ResponseResult(true, "001", "操作成功");
    }

    @ResponseBody
    @ApiOperation("删除用户")
    @RequestMapping(value = "/admin/deleteUser",method = RequestMethod.POST)
    public ResponseResult changeUserStatus(
            @RequestParam @ApiParam(value = "用户id") Integer id,
            HttpSession httpSession) {
        if((Integer) httpSession.getAttribute("type")!=1){
            return new ResponseResult(false, "002", "无权限删除！", null);
        }
        try{
            userService.deleteUser(id);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(false, "003", "删除失败");
        }
        return new ResponseResult(true, "001", "删除成功");
    }

}
