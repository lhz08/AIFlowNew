package com.bdilab.aiflow.controller;

import com.alibaba.fastjson.JSON;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.mapper.UserMapper;
import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.utils.EncryptionUtils;
import com.bdilab.aiflow.common.utils.HttpClientHelper;
import com.bdilab.aiflow.common.utils.UUMSEncryptionUtils;
import com.bdilab.aiflow.common.utils.UUMSRsaUtil;
import com.bdilab.aiflow.mapper.UserMapper;
import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.model.application.Application;
import com.bdilab.aiflow.model.menu.MenuInfo;
import com.bdilab.aiflow.model.menu.TransferAmenu;
import com.bdilab.aiflow.model.userManage.UUMSUser;
import com.bdilab.aiflow.model.userManage.UUMSUserInfo;
import com.bdilab.aiflow.model.userManage.UUMSUserInfoInsert;
import com.bdilab.aiflow.service.UUMS.UUMSUserService;
import com.bdilab.aiflow.vo.MenuInfoVo;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author csh
 * @version 1.0
 * @date 2021/2/20 15:30
 */
@Controller
@CrossOrigin
@Api(value = "一体化Controller")
@RequestMapping(value = "/uums/users")
@Slf4j
public class UUMSUserController {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UUMSUserService uumsUserService;

    @Value("${certificate.path}")
    private String certificatePath;

    @Value("${keystorePath}")
    private String keystorePath;

    @Value("${uumsMenuInterface}")
    private String uumsMenuInterface;

    @Value("${uumsCodingId}")
    private String uumsCodingId;

    @ResponseBody
    @ApiOperation("用户免密登录")
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test(){
        String property = System.getProperty("java.library.path");
        log.info("【java.library.path路径】{}",property);
        return property;
    }
    @ResponseBody
    @ApiOperation("用户免密登录")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseResult userLogin(@ApiParam(value = "account加密后的值",required = true) @RequestParam String account,
                                    HttpSession httpSession) {
        System.err.println("用户免密登录--->" + account);
        log.error("用户免密登录--->" + account);

        try {
            String username = UUMSRsaUtil.decrypt(account);
            System.out.println(username);
            User user = userMapper.selectUserByName(username);
            if (user != null && user.getStatus() == 1) {
                httpSession.setAttribute("user_id", user.getId());
                httpSession.setAttribute("name", user.getUserName());
                Map<String, Object> data = new HashMap<>(2);
                data.put("uid", user.getId());
                data.put("name", user.getUserName());
                data.put("user type", user.getType());
                return new ResponseResult(true, "001", "登录成功", data);
            } else if (user != null && user.getStatus() == 0)
                return new ResponseResult(false, "002", "登录失败，请联系管理员确认你是否有权限使用本系统");
            return new ResponseResult(false, "002", "用户名或密码错误", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(false, "002", "登录失败，请重试", null);
        }
    }

    @ResponseBody
    @ApiOperation("一体化平台增加或者更新用户")
    @RequestMapping(value = "/uumsRegister",method = RequestMethod.POST)
    public ResponseResult uumsRegister(@RequestBody UUMSUserInfoInsert userInfo
//                                   @ApiIgnore BindingResult bindingResult
    ) {
        System.err.println("一体化平台增加或者更新用户--->"+userInfo.getUserInfo());
        log.error("一体化平台增加或者更新用户--->"+userInfo.getUserInfo());
//
        // 验证码校验
        try {
//
            // rsa解密
            String data = EncryptionUtils.decryptLicense(userInfo.getUserInfo(),certificatePath);

            // 将rsa解密后的json转为对象
            UUMSUserInfo UMMSUserInfo = JSON.parseObject(data, UUMSUserInfo.class);

            //判断用户信息校验将收到对象的data + encrypt 用MD5加密后与sign做比较
            if (UMMSUserInfo.getSign().equals(
                    EncryptionUtils.getMd5("test" + JSON.toJSONString(UMMSUserInfo.getUserList())))) {
                // 校验信息完成后处理数据
                // 获取传过来需要同步的统一侧用户集合
                List<UUMSUser> userList = UMMSUserInfo.getUserList();

                uumsUserService.uumsUserRegister(userList);
                //全部应用对象集合
                List<Application> applicationList = UMMSUserInfo.getApplicationList();
                //插入或者更新数据库
                uumsUserService.uumsApplicationRegister(applicationList);

                //异步调用菜单接口 调杜写的接口
                sendMenus();

                return new ResponseResult(true, "001", "操作成功", data);
            }
            return new ResponseResult(false, "002", "处理失败", data);
        }  catch (Exception e) {
            e.printStackTrace();

            return new ResponseResult(false, "002", "操作异常，请重试", null);
        }

    }

    @ResponseBody
    @ApiOperation("一体化平台菜单接口请求")
    @RequestMapping(value = "/uumsMenus",method = RequestMethod.POST)
    @Async
    public void sendMenus() throws Exception {
//        projectPath = System.getProperty("user.dir");
        //一体化平台菜单接口请求
        //获取父级菜单
        List<TransferAmenu> transferAmenus=uumsUserService.selectParentMenus();
        List<TransferAmenu> sonTransferAmenus=null;
        List<TransferAmenu> allTransferAmenus=new ArrayList<>();

        List<TransferAmenu> allTransferAmenusUser=new ArrayList<>();
        TransferAmenu transferAmenuUser=null;
        TransferAmenu sontransferAmenuUser=null;
        List<TransferAmenu> sonTransferAmenuUserList=null;
        for (TransferAmenu transferAmenu : transferAmenus) {
            transferAmenu.setRoleType(0);
            sonTransferAmenus=uumsUserService.selectMenusByParentcode(transferAmenu.getCode());
            //用户类型子菜单集合
            sonTransferAmenuUserList=new ArrayList<>();

            for (TransferAmenu sonTransferAmenu : sonTransferAmenus) {
                sonTransferAmenu.setRoleType(0);

                sontransferAmenuUser=new TransferAmenu();
                sontransferAmenuUser.setRoleType(1);
                sontransferAmenuUser.setSort(sonTransferAmenu.getSort());
                sontransferAmenuUser.setName(sonTransferAmenu.getName());
                sontransferAmenuUser.setCode(sonTransferAmenu.getCode());
                sontransferAmenuUser.setTransferAmenuList(new ArrayList<>());
                sonTransferAmenuUserList.add(sontransferAmenuUser);
            }
            transferAmenu.setTransferAmenuList(sonTransferAmenus);
            allTransferAmenus.add(transferAmenu);
            //用户类型菜单
            transferAmenuUser=new TransferAmenu();
            transferAmenuUser.setRoleType(1);
            transferAmenuUser.setCode(transferAmenu.getCode());
            transferAmenuUser.setName(transferAmenu.getName());
            transferAmenuUser.setSort(transferAmenu.getSort());
            transferAmenuUser.setTransferAmenuList(sonTransferAmenuUserList);
            allTransferAmenusUser.add(transferAmenuUser);
        }

        //admin和user的菜单集合
        List<TransferAmenu> totalTransferAmenu=new ArrayList<>();
        totalTransferAmenu.addAll(allTransferAmenus);
        totalTransferAmenu.addAll(allTransferAmenusUser);

        //MD5加密生成sign
        String sign= UUMSEncryptionUtils.getMd5("test"+JSON.toJSONString(totalTransferAmenu)+uumsCodingId);
        MenuInfo menuInfo=new MenuInfo();
        menuInfo.setCodingId(uumsCodingId);
        menuInfo.setSign(sign);
        menuInfo.setTransferAmenuList(totalTransferAmenu);
        String menuInfoStr=JSON.toJSONString(menuInfo);
        //私钥加密
        String menuInfoEncrypt=UUMSEncryptionUtils.encryptLicense(menuInfoStr,keystorePath);
//        System.err.println(UUMSEncryptionUtils.decryptLicense(menuInfoEncrypt,certificatePath));
        MenuInfoVo menuInfoVo=new MenuInfoVo();
        menuInfoVo.setMenuInfo(menuInfoEncrypt);
        String response= HttpClientHelper.sendJsonHttpPost(uumsMenuInterface,new Gson().toJson(menuInfoVo));
        log.info(response);
//        if (response.getStatusCodeValue()!=200){
//          log.error("uumsMenuInterfaceError-->"+response);
//        }

    }

    @ResponseBody
    @ApiOperation("用户获取可跳转应用")
    @RequestMapping(value = "/getJumpableApplication",method = RequestMethod.POST)
    public ResponseResult getJumpableApplication(HttpSession httpSession){
        Integer userId = null;
        try {
            userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return  new ResponseResult(false,"001","获取可跳转应用失败,请登录");
        }
        System.err.println("用户获取一体化平台可跳转应用--->"+userId);
        log.error("用户获取一体化平台可跳转应用--->"+userId);
        List<Application> applicationList=uumsUserService.getUumsUserApplication((long)userId);
        if(applicationList.isEmpty()){
            return  new ResponseResult(false,"001","获取可跳转应用失败");
        }
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(applicationList);
        responseResult.setMeta(new MetaData(true, "001", "获取可跳转应用成功"));
        return responseResult;
    }
}
