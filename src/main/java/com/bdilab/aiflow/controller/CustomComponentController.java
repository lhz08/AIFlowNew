package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.component.ComponentCreateInfo;
import com.bdilab.aiflow.model.component.CustomComponentInfo;
import com.bdilab.aiflow.service.component.CustomComponentService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
@Api(value="自定义组件Controller")
public class CustomComponentController {

    @Autowired
    CustomComponentService customComponentService;

    /**
     * 创建自定义组件
     * @param componentFile 要保存的yaml文件，流程组件没有yaml文件
     * @param componentCreateInfo 专门用来保存组件创建信息的对象
     * @return
     */
    @ResponseBody
    @ApiOperation("创建自定义组件")
    @RequestMapping(value = "/customComponent/createComponent", method = RequestMethod.POST)
    public ResponseResult createComponent(@RequestPart @ApiParam(value = "componentFile") MultipartFile componentFile,
                                          @RequestPart @ApiParam(value = "component") ComponentCreateInfo componentCreateInfo,
                                          HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        System.out.println(componentCreateInfo);
        System.out.println("file:"+componentFile.getOriginalFilename());

        boolean isCreateSuccess = customComponentService.saveComponent(userId, componentFile, componentCreateInfo);
        if (isCreateSuccess) {
            return new ResponseResult(true, "001", "Created successfully");
        } else {
            return new ResponseResult(false, "002", "Created failed.");
        }
    }

    /**
     * 删除自定义组件到回收站
     * @param componentId 组件ID
     * @return
     */
    @ResponseBody
    @ApiOperation("删除自定义组件到回收站")
    @RequestMapping(value = "/customComponent/deleteComponent", method = RequestMethod.POST)
    public ResponseResult deleteComponent(@RequestParam @ApiParam(value = "componentId") Integer componentId,
                                          HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isComponentDelete = customComponentService.deleteComponent(componentId);
        if (isComponentDelete) {
            return new ResponseResult(true,"001","Deleted successfully.");
        } else {
            return new ResponseResult(false, "002", "Deleted failed.");
        }
    }

    /**
     * 根据组件id列表，彻底删除自定义组件及其yaml文件
     * @param componentIds 包含组件id的列表
     * @return
     */
    @ResponseBody
    @ApiOperation("彻底删除自定义组件")
    @RequestMapping(value = "/customComponent/deleteComponentPermanently", method = RequestMethod.POST)
    public ResponseResult deleteComponentPermanently(@RequestParam @ApiParam(value = "componentId") List<Integer> componentIds,
                                                     HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isDeletePermanently = customComponentService.deleteComponentPermanently(componentIds);
        if (isDeletePermanently) {
            return new ResponseResult(true,"001","Deleted forever.");
        } else {
            return new ResponseResult(false,"002","Deleted failed.");
        }
    }

    /**
     * 从回收站恢复组件
     * @param componentIds 包含组件id的列表
     * @return
     */
    @ResponseBody
    @ApiOperation("从回收站恢复组件")
    @RequestMapping(value = "/customComponent/restoreComponent", method = RequestMethod.POST)
    public ResponseResult restoreComponent(@RequestParam @ApiParam(value = "componentId") List<Integer> componentIds,
                                           HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isComponentRestore = customComponentService.restoreComponent(componentIds);
        if (isComponentRestore) {
            return new ResponseResult(true,"001","Restore successfully.");
        } else {
            return new ResponseResult(false,"002","Restore failed.");
        }
    }

    @ResponseBody
    @ApiOperation("根据关键字获取用户自定义组件")
    @RequestMapping(value = "/customComponent/getUserCustomComponentByKeyword", method = RequestMethod.POST)
    public ResponseResult getUserCustomComponentByKeyword(@RequestParam(value = "keyword") String keyword, @RequestParam(value = "type") int type,
                                                          @RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                          @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
                                                          HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        PageInfo<CustomComponentInfo> customComponentList = customComponentService.selectComponentByKeyword(keyword,type,pageNum,pageSize);
        Map<String, Object> data = new HashMap<>(2);
        if(customComponentList!=null&&customComponentList.getSize()!=0) {
            data.put("ComponentList",customComponentList);
            return new ResponseResult(true, "001","搜索成功",data,(int)customComponentList.getTotal());
        }
        return new ResponseResult(false, "002","搜索失败",null,0);
    }

    @ResponseBody
    @ApiOperation("根据标签获取用户自定义组件")
    @RequestMapping(value = "/customComponent/getUserCustomComponentByTag", method = RequestMethod.POST)
    public ResponseResult getUserCustomComponentByTag(@RequestParam(value = "tag") String tag, @RequestParam(value = "type") int type,
                                                      @RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                      @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
                                                      HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        PageInfo<CustomComponentInfo> customComponentList = customComponentService.selectComponentByTag(tag,type,pageNum,pageSize);
        Map<String, Object> data = new HashMap<>(2);
        if(customComponentList!=null && customComponentList.getSize()!=0) {
            data.put("ComponentList",customComponentList);
            return new ResponseResult(true, "001","搜索成功",data,(int)customComponentList.getTotal());
        }
        return new ResponseResult(false, "002","搜索失败",null,0);
    }

    @ResponseBody
    @ApiOperation("加载用户组件信息")
    @RequestMapping(value = "/customComponent/loadCustomComponentInfo", method = RequestMethod.POST)
    public ResponseResult loadCustomComponentInfo(@RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                  @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
                                                  @RequestParam(value = "type") int type,
                                                  HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        PageInfo<CustomComponentInfo> data = customComponentService.loadCustomComponentByUserIdAndType(userId,pageNum,pageSize,type);
        return new ResponseResult(true,"001","成功加载用户组件信息",data,(int)data.getTotal());
    }
}
