package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.component.ComponentCreateInfo;
import com.bdilab.aiflow.model.component.CustomComponentInfo;
import com.bdilab.aiflow.service.component.CustomComponentService;
import com.bdilab.aiflow.vo.ComponentInfoVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.annotation.Resource;
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

    @Value("${customComponent.file.path}")
    private String customFilePath;
    @Value("${customComponent.headFile.path}")
    private String customHeadFilePath;
    @Value("/home/customComponent/headfile/init.yaml")
    private String customHeadFileYaml;


    /**
     * 创建自定义组件
     * 假定用户已经上传至harbor，只需要传入yaml名称，现在存储位置为/home/pipelineYaml/
     * @param yamlFileName 不带.yaml
     * @param componentCreateInfo
     * @param httpSession
     * @return
     */
    @ResponseBody
    @ApiOperation("创建自定义组件")
    @RequestMapping(value = "/customComponent/createComponent", method = RequestMethod.POST)
    public ResponseResult createComponent(@RequestParam @ApiParam("yamlFileName")  String yamlFileName,
                                          @RequestPart  @ApiParam("componentCreateInfo")ComponentCreateInfo componentCreateInfo,
                                          HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }

        System.out.println(componentCreateInfo);
        System.out.println("yamlFileName:"+yamlFileName);

        boolean isCreateSuccess = customComponentService.saveComponent(userId, yamlFileName, componentCreateInfo);
        if (isCreateSuccess) {
            return new ResponseResult(true, "001", "Created successfully");
        } else {
            return new ResponseResult(false, "002", "Created failed.");
        }
    }

//    /**
//     * 创建自定义组件
//     * (实际考虑直接上传文件)
//     * 前端组装componentCreateInfo信息由用户填入：
//     * componentInfo:组件name，tags,yaml文件，输入列表(string,string)，输出列表(string,string)，描述，中文名
//     * componentParameter列表中的每个参数:参数name，参数类型(int)，默认值，描述
//     * 其余信息：组件类型(0算法,1流程,2模型)，组件源sourceId，

//     * @param componentFile 要保存的yaml文件，流程组件没有yaml文件
//     *                      getOriginalFilename()：用来得到文件名
//     * @param componentCreateInfo 专门用来保存组件创建信息的对象
//     * @return
//     */
//    @ResponseBody
//    @ApiOperation("创建自定义组件")
//    @RequestMapping(value = "/customComponent/createComponent", method = RequestMethod.POST)
//    public ResponseResult createComponent(@RequestPart @ApiParam(value = "componentFile") MultipartFile componentFile,
//                                          @RequestPart @ApiParam(value = "component") ComponentCreateInfo componentCreateInfo,
//                                          HttpSession httpSession) {
//        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
//        if(userId == null){
//            return new ResponseResult(false,"500","用户未登录");
//        }
//        System.out.println(componentCreateInfo);
//        System.out.println("file:"+componentFile.getOriginalFilename());
//
//        boolean isCreateSuccess = customComponentService.saveComponent(userId, componentFile, componentCreateInfo);
//        if (isCreateSuccess) {
//            return new ResponseResult(true, "001", "Created successfully");
//        } else {
//            return new ResponseResult(false, "002", "Created failed.");
//        }
//    }

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
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
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
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
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
                                           @RequestParam @ApiParam(value = "type") int type,
                                           HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
        boolean isComponentRestore = customComponentService.restoreComponent(componentIds,type);
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
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
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
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
        PageInfo<CustomComponentInfo> customComponentList = customComponentService.selectComponentByTag(tag,type,pageNum,pageSize);
        Map<String, Object> data = new HashMap<>(2);
        if(customComponentList!=null && customComponentList.getSize()!=0) {
            data.put("ComponentList",customComponentList);
            return new ResponseResult(true, "001","搜索成功",data,(int)customComponentList.getTotal());
        }
        return new ResponseResult(false, "002","搜索失败",null,0);
    }

        @ResponseBody
        @ApiOperation("分页展示用户自定义组件信息")
        @RequestMapping(value = "/customComponent/getCustomComponentInfo", method = RequestMethod.POST)
        public ResponseResult getCustomComponentInfo(@RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
        @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
        @RequestParam(value = "type") int type,
        HttpSession httpSession) {
            Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
            if(userId == null){
                return new ResponseResult(false,"500","用户未登录");
            }
            int isDeleted = 0;
            PageInfo<CustomComponentInfo> data = customComponentService.getCustomComponentByUserIdAndType(userId,pageNum,pageSize,type,isDeleted);
            return new ResponseResult(true,"001","成功获得用户组件信息",data,(int)data.getTotal());
    }

    @ResponseBody
    @ApiOperation("回收站展示用户自定义组件信息")
    @RequestMapping(value = "/customComponent/getComponentInTrash", method = RequestMethod.POST)
    public ResponseResult getComponentInTrash(@RequestParam(defaultValue = "1")@ApiParam(value = "页码") int pageNum,
                                                 @RequestParam(defaultValue = "10")@ApiParam(value = "每页记录数") int pageSize,
                                                 @RequestParam(value = "type") int type,
                                                 HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
        int isDeleted = 1;
        PageInfo<CustomComponentInfo> data = customComponentService.getCustomComponentByUserIdAndType(userId,pageNum,pageSize,type,isDeleted);
        return new ResponseResult(true,"001","成功获得用户组件信息",data,(int)data.getTotal());
    }


    @ResponseBody
    @ApiOperation("加载用户自定义组件信息")
    @RequestMapping(value = "/customComponent/loadCustomComponentInfo", method = RequestMethod.POST)
    public ResponseResult loadCustomComponentInfo(HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
        Map<String,List<ComponentInfoVO>> data = customComponentService.loadCustomComponentInfo(userId);
        return new ResponseResult(true,"001","成功加载用户自定义组件信息",data);
    }

    @ResponseBody
    @ApiOperation("加载系统组件信息")
    @RequestMapping(value = "/publicComponent/loadPublicComponentInfo", method = RequestMethod.POST)
    public ResponseResult loadPublicComponentInfo(@RequestParam @ApiParam(value = "isMl") Integer isMl,
            HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
        Map<String,List<ComponentInfoVO>> data = customComponentService.loadPublicComponentInfo(isMl);
        return new ResponseResult(true,"001","成功加载系统组件信息",data);
    }

    @ResponseBody
    @ApiOperation("删除自定义流程组件到回收站")
    @RequestMapping(value = "/customComponent/deleteWorkflowComponent", method = RequestMethod.POST)
    public ResponseResult deleteWorkflowComponent(@RequestParam @ApiParam(value = "workflowComponentId") Integer workflowComponentId) {
        boolean isComponentDelete = customComponentService.deleteWorkflowComponent(workflowComponentId);
        if (isComponentDelete) {
            return new ResponseResult(true,"001","Deleted successfully.");
        } else {
            return new ResponseResult(false, "002", "Deleted failed.");
        }
    }

    /**
     * 根据组件id列表，彻底删除自定义组件及其yaml文件
     * @param workflowComponentId 包含组件id的列表
     * @return
     */
    @ResponseBody
    @ApiOperation("彻底删除自定义流程组件")
    @RequestMapping(value = "/customComponent/deleteWorkflowComponentPermanently", method = RequestMethod.POST)
    public ResponseResult deleteWorkflowComponentPermanently(@RequestParam @ApiParam(value = "workflowComponentId") List<Integer> workflowComponentId) {

        boolean isDeletePermanently = customComponentService.deleteWorkflowComponentPermanently(workflowComponentId);
        if (isDeletePermanently) {
            return new ResponseResult(true,"001","Deleted forever.");
        } else {
            return new ResponseResult(false,"002","Deleted failed.");
        }
    }


}
