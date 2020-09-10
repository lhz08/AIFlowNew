package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.service.dataset.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * @author
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
    @RequestMapping(value = "/dataset/getPublicDataset",method = RequestMethod.GET)
    public ResponseResult getPublicDataset(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           HttpSession httpSession){
        Map<String,Object> data = datasetService.getPublicDataset(pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取公开数据集列表"));
        System.out.println(data);
        return responseResult;
    }

    /*分页获取用户自定义数据集信息列表*/
    @ResponseBody
    @RequestMapping(value = "/dataset/getUserDataset",method = RequestMethod.GET)
    public ResponseResult getUserDataset(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam int userId,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         HttpSession httpSession){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Map<String,Object> data = datasetService.getUserDataset(userId,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功分页获取用户自定义数据集列表"));
        return responseResult;
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

    @ResponseBody
    @RequestMapping(value = "/dataset/insertUserDataset",method = RequestMethod.POST)
    public ResponseResult uploadDataset(@RequestParam MultipartFile file,
                                        @RequestParam Integer userId,
                                        @RequestParam String datasetName,
                                        @RequestParam String tags,
                                        @RequestParam String datasetDesc,
                                        HttpSession httpSession){

        //获取上传文件的原始名
        String filename = file.getOriginalFilename();
        //获取文件的后缀名
        String suffixName = filename.substring(filename.lastIndexOf("."));

        //为上传的文件生成一个随机名字
        filename = UUID.randomUUID()+suffixName;
        String filePath = filePathConfig.getDatasetUrl()+File.separatorChar+filename;
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
    /*删除数据集--移入回收站*/
    @ResponseBody
    @RequestMapping(value = "/dataset/deleteDataset",method = RequestMethod.POST)
    public ResponseResult deleteDatasetById(@RequestParam Integer datasetId,
                                            HttpSession httpSession){

        boolean isSuccess = datasetService.deleteDatasetById(datasetId);
        if (isSuccess){
            return new ResponseResult(true,"001","数据集移入回收站成功");
        }
        return new ResponseResult(false,"002","数据集删除失败");
    }

    /*删除数据集--彻底删除*/
    @ResponseBody
    @RequestMapping(value = "/dataset/deleteDatasetPermanently",method = RequestMethod.POST)
    public ResponseResult deleteDatasetCompletelyById(@RequestParam Integer datasetId,
                                                      HttpSession httpSession){
        boolean isSuccess = datasetService.deleteDatasetCompletelyById(datasetId);
        if (isSuccess){
            return new ResponseResult(true,"001","彻底删除数据集成功");
        }
        return new ResponseResult(false,"002","彻底删除数据集失败");
    }

    /*分页获取回收站中的数据集列表*/
    @ResponseBody
    @RequestMapping(value = "/dataset/getDatasetInTrash",method = RequestMethod.GET)
    public ResponseResult getDatasetInTrash(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam Integer userId,
                                            HttpSession httpSession){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId = 1;
        Map<String,Object> data = datasetService.getDatasetInTrash(userId,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功分页获取回收站中的数据集列表"));
        return responseResult;
    }

    /*从回收站恢复数据集*/
    @ResponseBody
    @RequestMapping(value = "/dataset/restoreDataset",method = RequestMethod.GET)
    public ResponseResult restoreDataset(@RequestParam Integer datasetId,
                                         HttpSession httpSession){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean  isSuccess= datasetService.restoreDataset(datasetId);
        if (isSuccess){
            return new ResponseResult(true,"001","恢复数据集成功");
        }
        return new ResponseResult(false,"002","恢复数据集失败");
    }
    /*按名称分页搜索公开数据集*/
    @ResponseBody
    @RequestMapping(value = "/dataset/searchPublicDatasetByName",method = RequestMethod.GET)
    public ResponseResult searchPublicDatasetByName(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam(defaultValue = "test") String datasetName,
                                                    HttpSession httpSession){
        Map<String,Object> data = datasetService.searchPublicDatasetByName(datasetName,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按名称分页搜索用户自定义数据集*/
    @ResponseBody
    @RequestMapping(value = "/dataset/searchUserDatasetByName",method = RequestMethod.GET)
    public ResponseResult searchUserDatasetByName(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam Integer userId,
                                                  @RequestParam(defaultValue = "test") String datasetName,
                                                  HttpSession httpSession){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId = 1;
        Map<String,Object> data = datasetService.searchUserDatasetByName(userId,datasetName,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按tags分页搜索公开数据集*/
    @ResponseBody
    @RequestMapping(value = "/dataset/searchPublicDatasetByTags",method = RequestMethod.GET)
    public ResponseResult searchPublicDatasetByTags(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam String datasetTags,
                                                    HttpSession httpSession){
        Map<String,Object> data = datasetService.searchPublicDatasetByTags(datasetTags,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按tags分页搜索用户自定义数据集*/
    @ResponseBody
    @RequestMapping(value = "/dataset/searchUserDatasetByTags",method = RequestMethod.GET)
    public ResponseResult searchUserDatasetByTags(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam Integer userId,
                                                  @RequestParam(defaultValue = "test") String datasetTags,
                                                  HttpSession httpSession){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId = 1;
        Map<String,Object> data = datasetService.searchUserDatasetByTags(userId,datasetTags,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*导出数据集*/
    @ResponseBody
    @RequestMapping(value = "/dataset/downloadDataset",method = RequestMethod.GET)
    public ResponseResult downloadDataset(@RequestParam Integer datasetId,
                                          HttpSession httpSession){
        File file = datasetService.downloadDataset(datasetId);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(file);
        responseResult.setMeta(new MetaData(true,"001","成功导出"));
        return responseResult;
    }

    /*获取记录用作预览*/
    @ResponseBody
    @RequestMapping(value = "/dataset/previewDataset",method = RequestMethod.GET)
    public ResponseResult previewDataset(@RequestParam Integer datasetId,
                                         HttpSession httpSession){
        Map<String, Object> data = datasetService.getPreviewList(datasetId);
        if(data.get("content")==null){
            return new ResponseResult(false,"002","获取预览信息失败");
        }
        return new ResponseResult(true,"001","成功获取预览信息",data);
    }
}
