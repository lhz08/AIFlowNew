package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.ComponentOutputStub;
import com.bdilab.aiflow.service.component.ComponentOutputStubService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author smile
 * @data 2020/9/28 15:53
 **/
@Controller
@CrossOrigin
@Api(value="组件执行结果展示Controller")
public class ComponentOutputStubController {
    @Resource
    ComponentOutputStubService componentOutputStubService;
    @ResponseBody
    @ApiOperation("获取组件的运行结果")
    @RequestMapping(value = "/componentOutput/getComponentResult", method = RequestMethod.POST)
    public ResponseResult getComponentResult(@RequestParam @ApiParam(value = "runningId") Integer runningId,
                                             @RequestParam @ApiParam(value = "componentId") Integer componentId
    ){
        List<ComponentOutputStub> componentResult = componentOutputStubService.getComponentResult(runningId, componentId);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(componentResult);
        responseResult.setMeta(new MetaData(true,"001","获取结果成功"));
        return responseResult;
    }
    @ResponseBody
    @ApiOperation("组件的运行结果预览")
    @RequestMapping(value = "/componentOutput/previewComponentResult", method = RequestMethod.POST)
    public ResponseResult previewComponentResult(@RequestParam @ApiParam(value = "ComponentOutputStubId") Integer componentOutputStubId,
                                                 HttpSession httpSession
    ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String, Object> data = componentOutputStubService.previewResult(componentOutputStubId,userId);

        if (data.get("content") == null) {
            return new ResponseResult(false, "002", "获取预览信息失败");
        }
        return new ResponseResult(true, "001", "成功获取预览信息", data);
    }

    @ResponseBody
    @ApiOperation("组件的运行结果数据下载")
    @RequestMapping(value = "/componentOutput/downloadComponentResultCSV", method = RequestMethod.POST)
    public ResponseResult downloadComponentResultCSV(@RequestParam @ApiParam(value = "runningId") Integer runningId,
                                                     @RequestParam @ApiParam(value = "componentId") Integer componentId
                                                 //HttpSession httpSession
    ) throws IOException {
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String, Object> data = componentOutputStubService.downloadComponentResultCSV(runningId,componentId);

        if (data.get("content") == null) {
            return new ResponseResult(false, "002", "下载结果数据失败");
        }
        /*File file= (File) data.get("content");
        InputStream inStream = new FileInputStream(file); //读入原文件
        String fileadd="G:\\dataset\\result\\"+file.getName();
        System.out.println(fileadd);
        FileOutputStream fs = new FileOutputStream(fileadd);
        byte[] buffer = new byte[1444];
        int length;
        int bytesum = 0;
        int byteread = 0;
        while ( (byteread = inStream.read(buffer)) != -1) {
            bytesum += byteread; //字节数 文件大小
            System.out.println(bytesum);
            fs.write(buffer, 0, byteread);
        }
        inStream.close();*/
        return new ResponseResult(true, "001", "下载结果数据成功", data);
    }

}
