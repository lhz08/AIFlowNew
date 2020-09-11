package com.bdilab.aiflow.controller;

import com.alibaba.fastjson.JSON;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.component.ComponentCreateInfo;
import com.bdilab.aiflow.model.component.CustomComponentInfo;
import com.bdilab.aiflow.service.component.CustomComponentService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"自定义组件管理"})
@RestController
@RequestMapping("customComponent")
public class  CustomComponentController {

    @Autowired
    CustomComponentService customComponentService;

    /**
     * 创建自定义组件
     * @param userId 用户id
     * @param componentFile 要保存的yaml文件，流程组件没有yaml文件
     * @param createInfo 组件创建信息
     * @return
     */
    @ApiOperation("创建自定义组件")
    @RequestMapping(value = "createComponent", method = RequestMethod.POST)
    public ResponseResult createComponent(@RequestParam @ApiParam(value = "userId") Integer userId,
                                          @RequestParam @ApiParam(value = "componentFile") MultipartFile componentFile,
                                          @RequestPart @ApiParam(value = "component") String createInfo) {
        ComponentCreateInfo componentCreateInfo = JSON.parseObject(createInfo, ComponentCreateInfo.class);
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
    @ApiOperation("删除自定义组件到回收站")
    @RequestMapping(value = "deleteComponent", method = RequestMethod.POST)
    public ResponseResult deleteComponent(@RequestParam @ApiParam(value = "componentId") Integer componentId) {
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
    @ApiOperation("彻底删除自定义组件")
    @RequestMapping(value = "deleteComponentPermanently", method = RequestMethod.POST)
    public ResponseResult deleteComponentPermanently(@RequestParam @ApiParam(value = "componentId") List<Integer> componentIds) {
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
    @ApiOperation("从回收站恢复组件")
    @RequestMapping(value = "restoreComponent", method = RequestMethod.POST)
    public ResponseResult restoreComponent(@RequestParam @ApiParam(value = "componentId") List<Integer> componentIds) {
        boolean isComponentRestore = customComponentService.restoreComponent(componentIds);
        if (isComponentRestore) {
            return new ResponseResult(true,"001","Restore successfully.");
        } else {
            return new ResponseResult(false,"002","Restore failed.");
        }
    }

    /**
     * 加载回收站的组件
     * @param userId 用户id
     * @param type 回收站中要加载的组件类型
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return
     */
    @ApiOperation("加载回收站的组件")
    @RequestMapping(value = "getComponentInTrash", method = RequestMethod.POST)
    public ResponseResult getComponentInTrash(@RequestParam @ApiParam(value = "userId") Integer userId,
                                              @RequestParam @ApiParam(value = "type") Integer type,
                                              @RequestParam(defaultValue = "1")@ApiParam(value = "pageNum") Integer pageNum,
                                              @RequestParam(defaultValue = "10")@ApiParam(value = "pageSize") Integer pageSize) {
        PageInfo<CustomComponentInfo> data = customComponentService.getComponentInTrash(userId, type, pageNum, pageSize);
        return new ResponseResult(true,"001","Get component in trash successfully.",data,(int)data.getTotal());
    }

    @RequestMapping(value = "getUserCustomComponentByKeyword", method = RequestMethod.POST)
    public ResponseResult getUserCustomComponentByKeyword(@RequestParam(value = "keyword") String keyword, @RequestParam(value = "type") int type,
                                                          @RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                          @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize){
        PageInfo<CustomComponentInfo> customComponentList = customComponentService.selectComponentByKeyword(keyword,type,pageNum,pageSize);
        Map<String, Object> data = new HashMap<>(2);
        if(customComponentList!=null&&customComponentList.getSize()!=0) {
            data.put("ComponentList",customComponentList);
            return new ResponseResult(true, "001","搜索成功",data,(int)customComponentList.getTotal());
        }
        return new ResponseResult(false, "002","搜索失败",null,0);
    }

    @RequestMapping(value = "getUserCustomComponentByTag", method = RequestMethod.POST)
    public ResponseResult getUserCustomComponentByTag(@RequestParam(value = "tag") String tag, @RequestParam(value = "type") int type,
                                                      @RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                      @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize){
        PageInfo<CustomComponentInfo> customComponentList = customComponentService.selectComponentByTag(tag,type,pageNum,pageSize);
        Map<String, Object> data = new HashMap<>(2);
        if(customComponentList!=null && customComponentList.getSize()!=0) {
            data.put("ComponentList",customComponentList);
            return new ResponseResult(true, "001","搜索成功",data,(int)customComponentList.getTotal());
        }
        return new ResponseResult(false, "002","搜索失败",null,0);
    }

    @ApiOperation("加载用户组件信息")
    @RequestMapping(value = "loadCustomComponentInfo", method = RequestMethod.POST)
    public ResponseResult loadCustomComponentInfo(@RequestParam(value = "userId") int userId,
                                                  @RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                  @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
                                                  @RequestParam(value = "type") int type) {
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        PageInfo<CustomComponentInfo> data = customComponentService.loadCustomComponentByUserIdAndType(userId,pageNum,pageSize,type);
        return new ResponseResult(true,"001","成功加载用户组件信息",data,(int)data.getTotal());
    }
}
