package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.mysql.MysqlConnection;
import com.bdilab.aiflow.common.mysql.MysqlUtils;
import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.common.utils.MinioFileUtils;
import com.bdilab.aiflow.service.dataset.DatasetService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhangmin
 * @create 2020-08-26 17:39
 */
@Controller
public class DatasetController {

    @Autowired
    DatasetService datasetService;

    @Autowired
    FilePathConfig filePathConfig;

    /*
    * 分页获得公开数据集信息列表
    */
    @ResponseBody
    @ApiOperation(value = "分页获得公开数据集列表")
    @RequestMapping(value = "/dataset/getPublicDataset",method = RequestMethod.GET)
    public ResponseResult getPublicDataset(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = datasetService.getPublicDataset(pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取公开数据集列表"));
        System.out.println(responseResult);
        return responseResult;
    }

    /*分页获取用户自定义数据集信息列表*/
    @ResponseBody
    @ApiOperation("分页获取用户自定义数据集")
    @RequestMapping(value = "/dataset/getUserDataset",method = RequestMethod.GET)
    public ResponseResult getUserDataset(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId =6;
        Map<String,Object> data = datasetService.getUserDataset(userId,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功分页获取用户自定义数据集列表"));
        System.out.println(responseResult);
        return responseResult;
    }


   /* @ResponseBody
    @ApiOperation("上传数据集到minio服务器")
    @RequestMapping(value = "/dataset/uploadDataset",method = RequestMethod.POST)
    public ResponseResult uploadDatasetToMinio(@RequestParam MultipartFile file,
                                        @RequestParam String datasetName,
                                        @RequestParam String tags,
                                        @RequestParam String datasetDesc,
                                        HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(datasetService.uploadDatasetToMinio(file,datasetName,tags,datasetDesc,userId)) {
            return new ResponseResult(true, "001", "上传数据集成功");
        }
        return new ResponseResult(true,"002","上传数据集失败");
    }*/

    @ResponseBody
    @ApiOperation("从用户mysql上传表结构数据集")
    @RequestMapping(value = "/dataset/uploadUserDatasetFromMysql",method = RequestMethod.POST)
    public ResponseResult uploadUserDatasetFromMysql(@RequestParam String url,
                                                     @RequestParam String username,
                                                     @RequestParam String password,
                                                     @RequestParam String tableName,
                                                     @RequestParam String datasetName,
                                                     @RequestParam String tags,
                                                     @RequestParam String datasetDesc,
                                                     HttpSession httpSession) throws SQLException, IOException {
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Integer userId=6;

        //设置文件后缀为csv
        String suffixName=".csv";
        //为文件随机生成一个名字
        String filename = UUID.randomUUID()+suffixName;
        System.out.println(filename);
        String filePath = filePathConfig.getDatasetUrl()+File.separatorChar+filename;
        System.out.println(filePath);
        MysqlConnection mysqlConnection =new MysqlConnection();
        MysqlUtils mysqlUtils = new MysqlUtils();
        Connection con =null;
        //获取用户mysql连接
        try {
            mysqlConnection.setDriver("com.mysql.cj.jdbc.Driver");
            mysqlConnection.setUrl(url);
            mysqlConnection.setUser(username);
            mysqlConnection.setPassword(password);
            con = mysqlConnection.getConn();
        }catch (Exception e){
            return new ResponseResult(false,"003","MySQL连接失败");
        }
        if (!con.isClosed()){
            System.out.println("连接数据库成功!");
            String datasetfilePath = mysqlUtils.readTableToCSV(tableName, con, filePath);
            File datasetFile=new File(datasetfilePath);
            if(!datasetFile.exists()){
                con.close();
                return new ResponseResult(false,"002","注册数据集失败");
            }else{
                boolean isSuccess = datasetService.insertUserDataset(userId,datasetName,tags,filePath,datasetDesc);
                con.close();
                if (isSuccess){
                    return new ResponseResult(true,"001","数据集注册成功");
                }
                return new ResponseResult(false,"002","数据集注册失败");
            }
        }
        return new ResponseResult(false,"002","注册数据集失败");
    }
    @ResponseBody
    @ApiOperation("获取数据源列表")
    @RequestMapping(value = "/dataset/getAllTableName", method = RequestMethod.POST)
    public ResponseResult getAllTableName(@RequestParam @ApiParam(value = "数据库url") String databaseUrl,
                                          @RequestParam @ApiParam(value = "用户名") String userName,
                                          @RequestParam @ApiParam(value = "用户密码") String password,
                                          HttpSession httpSession
    ) {
        MysqlConnection mysqlConnection =new MysqlConnection();
        ResponseResult responseResult = new ResponseResult();
        List<String> list = new ArrayList<String>();
        Connection con =null;
        try {
            mysqlConnection.setDriver("com.mysql.jdbc.Driver");
            mysqlConnection.setUrl(databaseUrl);
            mysqlConnection.setUser(userName);
            mysqlConnection.setPassword(password);
            con = mysqlConnection.getConn();
        }catch (Exception e){
            return new ResponseResult(false,"003","MySQL连接失败");
        }
        try {
            String databaseName = databaseUrl.substring(databaseUrl.lastIndexOf("/")+1);
            PreparedStatement preparedStatement = con.prepareStatement("select table_name from information_schema.tables where table_schema= ?");
            preparedStatement.setString(1, databaseName);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("table_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            mysqlConnection.endConnection();
        }
        responseResult.setData(list);
        responseResult.setMeta(new MetaData(true, "001", "成功获取到数据源列表"));
        return responseResult;
    }

    @ResponseBody
    @ApiOperation("用户导入MySQL数据源")
    @RequestMapping(value = "/dataset/importMySQLDataSource", method = RequestMethod.POST)
    public ResponseResult importMySqlDataSource(@RequestParam @ApiParam(value = "数据库url") String databaseUrl,
                                                @RequestParam @ApiParam(value = "表名") String tableName,
                                                @RequestParam @ApiParam(value = "用户名") String userName,
                                                @RequestParam @ApiParam(value = "数据集标签") String tags,
                                                @RequestParam @ApiParam(value = "用户密码") String password,
                                                @RequestParam @ApiParam(value = "数据集名称") String datasetName,
                                                @RequestParam @ApiParam(value = "数据集描述") String datasetDesc,
                                                HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(datasetService.importMySqlDataSource(databaseUrl, tableName, userName, password, userId, datasetName, datasetDesc,tags)) {
            return new ResponseResult(true, "001", "成功导入MySql数据集");
        }else {
            return new ResponseResult(false, "002", "导入MySql数据集失败");
        }
    }

    @ResponseBody
    @ApiOperation("获取数据表的所有字段")
    @RequestMapping(value = "/dataset/getAllFieldByTableName", method = RequestMethod.POST)
    public ResponseResult getAllFieldByTableName(@RequestParam @ApiParam(value = "数据库url") String databaseUrl,
                                                 @RequestParam @ApiParam(value = "用户名") String userName,
                                                 @RequestParam @ApiParam(value = "用户密码") String password,
                                                 @RequestParam @ApiParam(value = "表名") String tableName){
        MysqlConnection mysqlConnection =new MysqlConnection();
        List<String> columnName = new ArrayList<>();
        ResponseResult responseResult = new ResponseResult();
        Connection con =null;
        //获取用户mysql连接
        try {
            mysqlConnection.setDriver("com.mysql.jdbc.Driver");
            mysqlConnection.setUrl(databaseUrl);
            mysqlConnection.setUser(userName);
            mysqlConnection.setPassword(password);
            con = mysqlConnection.getConn();
        }catch (Exception e){
            return new ResponseResult(false,"003","MySQL连接失败");
        }
        try {
            PreparedStatement preparedStatement =con.prepareStatement("select COLUMN_NAME from information_schema.COLUMNS where table_name = ?");
            preparedStatement.setString(1,tableName);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                columnName.add(rs.getString("column_name"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        responseResult.setData(columnName);
        responseResult.setMeta(new MetaData(true, "001", "成功获取到表的所有字段"));
        return  responseResult;
    }



    @ResponseBody
    @ApiOperation("选择表的字段录入")
    @RequestMapping(value = "/dataset/importDataSourceByField", method = RequestMethod.POST)
    public ResponseResult importDataSourceByField(@RequestParam @ApiParam(value = "数据源id") Integer datasourceId,
                                                  //@RequestParam @ApiParam(value = "数据库url") String databaseUrl,
                                                  //@RequestParam @ApiParam(value = "用户名") String userName,
                                                  //@RequestParam @ApiParam(value = "用户密码") String password,
                                                  @RequestParam @ApiParam(value = "表名") String tableName,
                                                  @RequestParam @ApiParam(value = "数据集名称") String datasetName,
                                                  @RequestParam @ApiParam(value = "数据集描述") String datasetDesc,
                                                  @RequestParam @ApiParam(value = "数据集标签") String tags,
                                                  @RequestParam @ApiParam(value = "表字段") List<String> field,
                                                  HttpSession httpSession) {
        ResponseResult responseResult = new ResponseResult();
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean temp = datasetService.importMySqlDataSourceByField(datasourceId, tableName, userId, datasetName,datasetDesc,tags, field);
        if(!temp) {
            responseResult.setMeta(new MetaData(false, "002", "字段导入失败"));
            return responseResult;
        }
        responseResult.setMeta(new MetaData(true, "001", "字段导入成功"));
        return responseResult;
    }

    @ResponseBody
    @ApiOperation("自定义SQL语句实现数据录入")
    @RequestMapping(value = "/dataset/importDataSourceBySql", method = RequestMethod.POST)
    public ResponseResult customizeSQL(@RequestParam @ApiParam(value = "数据源id") Integer datasourceId,
                                       @RequestParam @ApiParam(value = "SQL语句") String sql,
                                       //@RequestParam @ApiParam(value = "数据库url") String databaseUrl,
                                       // @RequestParam @ApiParam(value = "用户名") String userName,
                                       // @RequestParam @ApiParam(value = "用户密码") String password,
                                       @RequestParam @ApiParam(value = "数据集名称") String datasetName,
                                       @RequestParam @ApiParam(value = "数据集标签") String tags,
                                       @RequestParam @ApiParam(value = "数据集描述") String datasetDesc,
                                       HttpSession httpSession
    ) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean temp = datasetService.importMysqlDataSourceBySql(datasourceId, userId, datasetName, datasetDesc,tags, sql);
        ResponseResult responseResult = new ResponseResult();
        if(!temp)
        {
            responseResult.setMeta(new MetaData(false, "002", "自定义SQL导入数据失败"));
            return responseResult;
        }
        responseResult.setMeta(new MetaData(true, "001", "自定义SQL导入数据成功"));
        return responseResult;
    }
    @ResponseBody
    @ApiOperation("上传数据集")
    @RequestMapping(value = "/dataset/insertUserDataset",method = RequestMethod.POST)
    public ResponseResult uploadDataset(@RequestParam MultipartFile file,
                                        @RequestParam String datasetName,
                                        @RequestParam String tags,
                                        @RequestParam String datasetDesc,
                                        HttpSession httpSession){

        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId =6;
        //获取上传文件的原始名
        String filename = file.getOriginalFilename();
        //获取文件的后缀名
        String suffixName = filename.substring(filename.lastIndexOf("."));

        //为上传的文件生成一个随机名字
        filename = UUID.randomUUID()+suffixName;
        String filePath = filePathConfig.getDatasetUrl()+File.separatorChar+filename;
        System.out.println(filePath);
        File datasetFile = new File(filePath);
        try {
            file.transferTo(datasetFile);
            if(!datasetFile.exists()){
                return new ResponseResult(false,"002","注册数据集失败");
            }
            else{
                //csv格式，直接上传
                if(suffixName.equals(".csv")) {

                    boolean isSuccess = datasetService.insertUserDataset(userId,datasetName,tags,filePath,datasetDesc);
                    datasetFile.delete();
                    if (isSuccess){
                        return new ResponseResult(true,"001","数据集注册成功");
                    }
                    return new ResponseResult(false,"002","数据集注册失败");
                }
                //txt格式，转为csv文件
                else if(suffixName.equals(".txt")){
                    String csvFilePath = filePathConfig.getDatasetUrl()+File.separatorChar+ UUID.randomUUID()+".csv";
                    FileUtils.txtToCsv(filePath,csvFilePath);
                    datasetFile.delete();
                    boolean isSuccess = datasetService.insertUserDataset(userId,datasetName,tags,csvFilePath,datasetDesc);
                    datasetFile.delete();
                    if (isSuccess){
                        return new ResponseResult(true,"001","数据集注册成功");
                    }
                    return new ResponseResult(false,"002","数据集注册失败");
                }
                //其他格式，删除文件
                else{
                    boolean result = datasetFile.delete();
                    return new ResponseResult(false,"003","文件格式非法");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseResult(false,"002","注册数据集失败");
    }

    @ResponseBody
    @ApiOperation("导入Api数据集")
    @RequestMapping(value = "/dataset/importApiDataset", method = RequestMethod.POST)
    public Object importApiDataset(@RequestParam String sendUrl,
                                   @RequestParam String datasetName,
                                   @RequestParam String datasetTags,
                                   @RequestParam String datasetDesc,
                                   HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = datasetService.importApiDataset(sendUrl,datasetName,userId,datasetTags,datasetDesc);
        if (isSuccess){
            return new ResponseResult(true,"001","导入Api数据集成功");
        }
        return new ResponseResult(false,"002","导入Api数据集失败，URL连接异常");
    }

    /*删除数据集--移入回收站*/
    @ResponseBody
    @ApiOperation(value = "删除数据集--移入回收站")
    @RequestMapping(value = "/dataset/deleteDataset",method = RequestMethod.POST)
    public ResponseResult deleteDatasetById(@RequestParam Integer datasetId,
                                            HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = datasetService.deleteDatasetById(datasetId);
        if (isSuccess){
            return new ResponseResult(true,"001","数据集移入回收站成功");
        }
        return new ResponseResult(false,"002","数据集删除失败");
    }

    /*从回收站单个或批量彻底删除数据集*/
    @ResponseBody
    @ApiOperation(value = "从回收站单个或批量彻底删除数据集")
    @RequestMapping(value = "/dataset/deleteDatasetCompletelyById",method = RequestMethod.POST)
    public ResponseResult deleteDatasetCompletelyById(@RequestParam String datasetIds,
                                                      HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        String[] ids = datasetIds.split(",");
        boolean isSuccess;
        for (int i=0;i<ids.length;i++){
            isSuccess = datasetService.deleteDatasetCompletelyById(Integer.parseInt(ids[i]));
            if (!isSuccess){
                return new ResponseResult(false,"002","彻底删除数据集失败");
            }
        }
        return new ResponseResult(true,"001","彻底删除数据集成功");
    }

    /*分页获取回收站中的数据集列表*/
    @ResponseBody
    @ApiOperation(value = "分页获取回收站中的数据集列表")
    @RequestMapping(value = "/dataset/getDatasetInTrash",method = RequestMethod.POST)
    public ResponseResult getDatasetInTrash(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = datasetService.getDatasetInTrash(userId,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功分页获取回收站中的数据集列表"));
        return responseResult;
    }

    /*从回收站单个或批量恢复数据集*/
    @ResponseBody
    @ApiOperation(value = "从回收站单个或批量恢复数据集")
    @RequestMapping(value = "/dataset/restoreDataset",method = RequestMethod.POST)
    public ResponseResult restoreDataset(@RequestParam String datasetIds,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        String[] ids = datasetIds.split(",");
        boolean isSuccess;
        for (int i=0;i<ids.length;i++){
            isSuccess = datasetService.restoreDataset(Integer.parseInt(ids[i]));
            if (!isSuccess){
                return new ResponseResult(false,"002","恢复数据集失败");
            }
        }
        return new ResponseResult(true,"001","恢复数据集成功");
    }
    /*按名称分页搜索公开数据集*/
    @ResponseBody
    @ApiOperation(value = "按名称分页搜索公开数据集")
    @RequestMapping(value = "/dataset/searchPublicDatasetByName",method = RequestMethod.GET)
    public ResponseResult searchPublicDatasetByName(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam(defaultValue = "test") String datasetName,
                                                    HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = datasetService.searchPublicDatasetByName(datasetName,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按名称分页搜索用户自定义数据集*/
    @ResponseBody
    @ApiOperation(value = "按名称分页搜索用户自定义数据集")
    @RequestMapping(value = "/dataset/searchUserDatasetByName",method = RequestMethod.GET)
    public ResponseResult searchUserDatasetByName(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(defaultValue = "test") String datasetName,
                                                  HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = datasetService.searchUserDatasetByName(userId,datasetName,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按tags分页搜索公开数据集*/
    @ResponseBody
    @ApiOperation(value = "按tags分页搜索公开数据集")
    @RequestMapping(value = "/dataset/searchPublicDatasetByTags",method = RequestMethod.GET)
    public ResponseResult searchPublicDatasetByTags(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam String datasetTags,
                                                    HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = datasetService.searchPublicDatasetByTags(datasetTags,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按tags分页搜索用户自定义数据集*/
    @ResponseBody
    @ApiOperation(value = "按tags分页搜索用户自定义数据集")
    @RequestMapping(value = "/dataset/searchUserDatasetByTags",method = RequestMethod.GET)
    public ResponseResult searchUserDatasetByTags(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(defaultValue = "test") String datasetTags,
                                                  HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = datasetService.searchUserDatasetByTags(userId,datasetTags,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }
    /*@ResponseBody
    @ApiOperation("从minio服务器下载数据集")
    @RequestMapping(value = "/dataset/downloadDataset",method = RequestMethod.POST)
    public ResponseResult downloadDatasetFromMinio(@RequestParam Integer datasetId,
                                               HttpSession httpSession,
                                               HttpServletResponse response) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        if(userId == null){
            return new ResponseResult(false,"500","用户未登录");
        }
        response = datasetService.downloadDatasetFromMinio(userId,datasetId,response);
        if(response.getStatus()==200) {
            return new ResponseResult(true, "001", "下载数据集成功");
        }
        return new ResponseResult(true,"002","下载数据集失败");
    }*/

    /*导出数据集*/
    @ResponseBody
    @ApiOperation(value = "导出数据集")
    @RequestMapping(value = "/dataset/downloadDataset",method = RequestMethod.GET)
    public ResponseResult downloadDataset(@RequestParam Integer datasetId,
                                          HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        File file = datasetService.downloadDataset(datasetId);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(file);
        responseResult.setMeta(new MetaData(true,"001","成功导出"));
        return responseResult;
    }

    /*获取记录用作预览*/
    @ResponseBody
    @ApiOperation(value = "获取记录用作预览")
    @RequestMapping(value = "/dataset/previewDataset",method = RequestMethod.GET)
    public ResponseResult previewDataset(@RequestParam Integer datasetId,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String, Object> data = datasetService.getPreviewList(datasetId);
        if(data.get("content")==null){
            return new ResponseResult(false,"002","获取预览信息失败");
        }
        return new ResponseResult(true,"001","成功获取预览信息",data);
    }


    @ResponseBody
    @ApiOperation("从数据源中导入数据集")
    @RequestMapping(value = "/dataset/importFromDatasource",method = RequestMethod.GET)
    public ResponseResult importFromHBaseDatasource(@RequestParam@ApiParam(value = "id") int datasourceId,
                                                    @RequestParam@ApiParam(value = "数据表名") String tableName,
                                                    @RequestParam@ApiParam(value = "dataset名称")String datasetName,
                                                    @RequestParam@ApiParam(value = "dataset描述")String datasetDesc,
                                                    @RequestParam@ApiParam(value = "数据集描述")String tags,

                                                    HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = datasetService.importFromDatasource(userId, datasourceId, tableName, datasetName, datasetDesc,tags);
        if (isSuccess) {
            return new ResponseResult(true, "001", "成功导入对应数据表");
        }
        return new ResponseResult(false, "002", "对应数据表不存在");
    }
    /*注册数据集*/
  /*  @ResponseBody
    @RequestMapping(value = "/dataset/insertUserDataset",method = RequestMethod.POST)
    public ResponseResult insertUserDataset(@RequestParam MultipartFile file,
                                        @RequestParam Integer userId,
                                        @RequestParam String datasetName,
                                        @RequestParam String tags,
                                        @RequestParam String datasetDesc,
                                        @RequestParam String columnSeparator,
                                        @RequestParam String[] familyName,
                                        @RequestParam String[] columnName,
                                        HttpSession httpSession) throws IOException {
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //获取HBase连接
        Connection connection=HBaseConnection.getConn();
        //按照数据集名称建同名hbase表
        boolean  isCreateSuccess=HBaseUtils.createTable(datasetName,connection,familyName);
        if (!isCreateSuccess){
            return new ResponseResult(true,"002","HBase建表失败");}

        //读取文件中的每一行
        BufferedReader reader = new BufferedReader(new FileReader((File) file));
        String line = null;
        while ((line = reader.readLine()) != null) {
            //按照列分隔符将每行数据分开
            String[] lineDatas=line.split(columnSeparator);
            System.out.println(columnName);
            System.out.println(lineDatas);
            //将数据一行一行插入表中，columnName的格式是--“列族名：列名”，这里columnName和lineDatas的长度应该相等，第一列数据为主键
            HBaseUtils.putDatas(datasetName,columnName,lineDatas,connection);
        }
        boolean isSuccess = datasetService.insertUserDataset(userId,datasetName,tags,datasetDesc);
        if (isSuccess){
            return new ResponseResult(true,"001","数据集注册成功");
        }
        return new ResponseResult(false,"002","数据集注册失败");
    }*/
}
