package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.service.datasource.DataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(value = "数据源控制器")
@Controller
@CrossOrigin
public class DataSourceController {

    @Autowired
    DataSourceService datasourceService;



    @ResponseBody
    @ApiOperation("新增数据源")
    @RequestMapping(value="/datasource/newUserDataSource",method= RequestMethod.POST)
    public ResponseResult newDataSource(//@ApiParam(value="数据源id") int id,
                                        //  @RequestParam@ApiParam(value="用户id") int fkUserId,
                                        @RequestParam @ApiParam(value="数据源名称") String name,
                                        @RequestParam@ApiParam(value="数据源类型") boolean type,
                                        @RequestParam@ApiParam(value="数据源url") String url,
                                        @RequestParam@ApiParam(value="数据源账号") String username,
                                        @RequestParam@ApiParam(value="数据源密码") String password,
                                        @RequestParam@ApiParam(value="描述") String description,
                                        // @RequestParam@ApiParam(value="创建时间") Date createTime,
                                        HttpSession httpSession) {
        Date createTime = new Date();
        Integer fkUserId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = datasourceService.importDataSource(fkUserId, name, type, url, username, password, description, createTime);
        if (isSuccess){
            return new ResponseResult(true, "001", "成功导入数据源");
        }
        return new ResponseResult(false, "002", "导入数据源失败");
    }




    @ResponseBody
    @ApiOperation("删除数据源")
    @RequestMapping(value = "/datasource/deleteDataSource",method = RequestMethod.POST)
    public ResponseResult deleteDataSource(@RequestParam@ApiParam(value = "datasource id") Integer datasourceId,
                                           HttpSession httpSession){
        boolean isSuccess = datasourceService.deleteDataSource(datasourceId);
        if (isSuccess){
            return new ResponseResult(true,"001","成功删除数据集");
        }
        return new ResponseResult(false,"002","删除数据集失败");
    }


    @ResponseBody
    @ApiOperation("修改数据源")
    @RequestMapping(value="/datasource/updateDataSource",method=RequestMethod.POST)
    public ResponseResult updateDataSource(@ApiParam(value="数据源id") int id,
                                           //  @RequestParam@ApiParam(value="用户id") int fkUserId,
                                           @RequestParam@ApiParam(value="数据源名称") String name,
                                           @RequestParam@ApiParam(value="数据源类型") boolean type,
                                           @RequestParam@ApiParam(value="数据源url") String url,
                                           @RequestParam@ApiParam(value="数据源账号") String username,
                                           @RequestParam@ApiParam(value="数据源密码") String password,
                                           @RequestParam@ApiParam(value="描述") String description,
                                           // @RequestParam@ApiParam(value="创建时间") Date createTime,
                                           HttpSession httpSession) {
        Date createTime = new Date();
        Integer fkUserId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean flage = datasourceService.updateDataSource(id,fkUserId, name, type, url,username,password, description, createTime);
        if(flage){
            return new ResponseResult(true, "001", "成功更新数据源");
        }
        return new ResponseResult(false, "400", "失败更新数据源");
    }


    @ResponseBody
    @ApiOperation("新增HBase数据源")
    @RequestMapping(value = "/datasource/addHBaseDatasource",method = RequestMethod.POST)
    public ResponseResult addHBaseDatasource(@RequestParam@ApiParam(value="数据源名称") String name,
                                             @RequestParam@ApiParam(value="数据源描述") String description,
                                             @RequestParam@ApiParam(value = "MASTER IP") String hBaseMaster,
                                             @RequestParam@ApiParam(value = "Zookeeper IP") String zookeeperQuorum,
                                             @RequestParam@ApiParam(value = "Zookeeper Clientport") String zookeepeClientport,
                                             HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,String> config  = new HashMap<>();
        config.put("hbase.master",hBaseMaster);
        config.put("hbase.zookeeper.quorum",zookeeperQuorum);
        config.put("hbase.zookeeper.property.clientport",zookeepeClientport);
        boolean isSuccess = datasourceService.addHBaseDatasource(userId,config,name,description);
        if(isSuccess){
            return new ResponseResult(true,"001","成功添加HBase数据源。");
        }
        return new ResponseResult(false,"002","添加HBase数据源失败。");
    }

    @ResponseBody
    @ApiOperation("更新HBase数据源")
    @RequestMapping(value = "/datasource/updateHBaseDatasource",method = RequestMethod.POST)
    public ResponseResult updateHBaseDatasource(@RequestParam@ApiParam(value="数据源id") Integer id,
                                                @RequestParam(required = false)@ApiParam(value="数据源名称") String name,
                                                @RequestParam(required = false)@ApiParam(value="数据源描述") String description,
                                                @RequestParam(required = false)@ApiParam(value = "MASTER IP") String hBaseMaster,
                                                @RequestParam(required = false)@ApiParam(value = "Zookeeper IP") String zookeeperQuorum,
                                                @RequestParam(required = false)@ApiParam(value = "Zookeeper Clientport") String zookeepeClientport,
                                                HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,String> config  = new HashMap<>();
        config.put("hbase.master",hBaseMaster);
        config.put("hbase.zookeeper.quorum",zookeeperQuorum);
        config.put("hbase.zookeeper.property.clientport",zookeepeClientport);
        boolean isSuccess = datasourceService.updateHbaseDataSource(userId,id,config,name,description);
        if(isSuccess){
            return new ResponseResult(true,"001","成功更新HBase数据源。");
        }
        return new ResponseResult(false,"002","更新HBase数据源失败。");
    }


    @ResponseBody
    @ApiOperation("获取数据源预览信息")
    @RequestMapping(value = "/datasource/previewDataSource",method = RequestMethod.GET)
    public ResponseResult previewDataSource(HttpSession httpSession){
        Integer fkUserId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String, Object> data = datasourceService.generatePreview(fkUserId);

        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        if(data.get("dateSouce list")==null){
            return new ResponseResult(false,"002","获取预览信息失败");
        }
        return new ResponseResult(true,"001","成功获取预览信息",data);
    }
}
