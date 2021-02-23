package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.response.MetaData;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.utils.ArchiveUtils;
import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.model.DlDataset;
import com.bdilab.aiflow.service.dataset.DlDatasetService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Controller
public class DlDatasetController {
    @Autowired
    DlDatasetService dlDatasetService;

    @Autowired
    FilePathConfig filePathConfig;

    /**
     * 注册深度学习数据集-接收格式为zip压缩包
     */
    @ResponseBody
    @ApiOperation("注册深度学习数据集")
    @RequestMapping(value = "/dlDataset/insertUserDlDataset",method = RequestMethod.POST)
    public ResponseResult uploadDataset(@RequestParam MultipartFile file,
                                        @RequestParam String datasetName,
                                        @RequestParam String tags,
                                        @RequestParam String datasetDesc,
                                        @RequestParam Integer isAnnotation,
                                        @RequestParam Integer originFileType,
                                        HttpSession httpSession){

        if(isAnnotation < 0 || isAnnotation >1 || originFileType < 0 || originFileType > 2)
            return new ResponseResult(false,"004","非法参数");

        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId = 6;
        //获取上传文件的原始名
        String filename = file.getOriginalFilename();
        //获取文件的后缀名
        String suffixName = filename.substring(filename.lastIndexOf("."));
        if (!ArchiveUtils.isArchive(suffixName)){//根据后缀名判断是否为压缩包格式
            return new ResponseResult(false,"003","文件格式错误");
        }

        //生成解压后的输出目录
        String outputDir = filePathConfig.getDatasetUrl()+File.separatorChar+UUID.randomUUID();
        File outputDirectory = new File(outputDir);
        //为上传的压缩包生成一个随机名字
        filename = UUID.randomUUID()+suffixName;
        String filePath = filePathConfig.getFileTempPath()+File.separatorChar+filename;
        File datasetFile = new File(filePath);
        try {
            file.transferTo(datasetFile);
            if(!datasetFile.exists()){
                return new ResponseResult(false,"002","注册数据集失败");
            }
            //解压
            ArchiveUtils.unArchive(datasetFile,outputDir,suffixName);
        } catch (Exception e){
            e.printStackTrace();
            //解压失败，清除上传的压缩包，以及生成的部分输出解压文件
            //由于没有日志系统，这里的Boolean返回值被忽略了
            FileUtils.delFiles(outputDirectory);
            FileUtils.delFiles(datasetFile);
            return new ResponseResult(false,"002","注册数据集失败");
        }

        try{
            //下面这一行可能会抛出异常
            boolean isSuccess = dlDatasetService.insertUserDlDataset(userId, datasetName, tags, outputDir, datasetDesc,isAnnotation,originFileType);
            if (!isSuccess){
                FileUtils.delFiles(outputDirectory);
                FileUtils.delFiles(datasetFile);
                return new ResponseResult(false,"002","注册数据集失败");
            }
        } catch (Exception e){
            e.printStackTrace();
            FileUtils.delFiles(outputDirectory);
            FileUtils.delFiles(datasetFile);
            return new ResponseResult(false,"002","注册数据集失败");
        }

        boolean isDel = FileUtils.delFiles(datasetFile);
        return new ResponseResult(true,"001","注册数据集成功");
    }

    /*按名称分页搜索公开深度学习数据集*/
    @ResponseBody
    @ApiOperation(value = "按名称分页搜索公开深度学习数据集")
    @RequestMapping(value = "/dlDataset/searchPublicDlDatasetByName",method = RequestMethod.GET)
    public ResponseResult searchPublicDatasetByName(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam String datasetName,
                                                    HttpSession httpSession){
        Map<String,Object> data = dlDatasetService.searchPublicDlDatasetByName(datasetName,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按名称分页搜索用户自定义深度学习数据集*/
    @ResponseBody
    @ApiOperation(value = "按名称分页搜索用户自定义深度学习数据集")
    @RequestMapping(value = "/dlDataset/searchUserDlDatasetByName",method = RequestMethod.GET)
    public ResponseResult searchUserDatasetByName(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam String datasetName,
                                                  HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId = 6;
        Map<String,Object> data = dlDatasetService.searchUserDlDatasetByName(userId,datasetName,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按tags分页搜索公开深度学习数据集*/
    @ResponseBody
    @ApiOperation(value = "按tags分页搜索深度学习公开数据集")
    @RequestMapping(value = "/dlDataset/searchPublicDlDatasetByTags",method = RequestMethod.GET)
    public ResponseResult searchPublicDatasetByTags(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam String datasetTags,
                                                    HttpSession httpSession){
        Map<String,Object> data = dlDatasetService.searchPublicDlDatasetByTags(datasetTags,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*按tags分页搜索用户自定义深度学习数据集*/
    @ResponseBody
    @ApiOperation(value = "按tags分页搜索用户自定义深度学习数据集")
    @RequestMapping(value = "/dlDataset/searchUserDlDatasetByTags",method = RequestMethod.GET)
    public ResponseResult searchUserDatasetByTags(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam String datasetTags,
                                                  HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId = 6;
        Map<String,Object> data = dlDatasetService.searchUserDlDatasetByTags(userId,datasetTags,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取搜索结果"));
        return responseResult;
    }

    /*导出深度学习数据集*/
    @ResponseBody
    @ApiOperation(value = "导出深度学习数据集")
    @RequestMapping(value = "/dlDataset/downloadDlDataset", method = RequestMethod.POST)
    public ResponseResult downloadDlDataset(@RequestParam Integer datasetId,
                                       HttpServletResponse response,
                                       HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId = 6;
        DlDataset dlDataset = dlDatasetService.getDlDatasetByPrimaryId(datasetId);
        if(dlDataset==null)return new ResponseResult(false,"004","数据集不存在");
        if(dlDataset.getType()==1 && !dlDataset.getFkUserId().equals(userId))return new ResponseResult(false,"003","无权限导出");

        String fileName = dlDataset.getName()+ "-"+ datasetId + ArchiveUtils.FORMAT_ZIP;// 文件名
        File file = new File(dlDataset.getDatasetAddr());
        if(!file.exists())return new ResponseResult(false,"003","数据集已损坏");
        try{
            // 设置返回文件名
            response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            // 压缩并向response中写入
            ArchiveUtils.zip(file,response.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
            return new ResponseResult(false,"002","导出失败");
        }
        return new ResponseResult(true,"001","导出成功");
    }


    /*
     * 分页获得公开数据集信息列表
     */
    @ResponseBody
    @ApiOperation(value = "分页获得公开数据集列表")
    @RequestMapping(value = "/dlDataset/getPublicDlDataset",method = RequestMethod.GET)
    public ResponseResult getPublicDataset(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           HttpSession httpSession){
        Map<String,Object> data = dlDatasetService.getPublicDldataset(pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功获取公开数据集列表"));
        return responseResult;
    }

    /*分页获取用户自定义数据集信息列表*/
    @ResponseBody
    @ApiOperation("分页获取用户自定义数据集")
    @RequestMapping(value = "/dlDataset/getUserDlDataset",method = RequestMethod.GET)
    public ResponseResult getUserDataset(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId =6;
        Map<String,Object> data = dlDatasetService.getUserDldataset(userId,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功分页获取用户自定义数据集列表"));
        return responseResult;
    }
    /*删除数据集--移入回收站*/
    @ResponseBody
    @ApiOperation(value = "删除数据集--移入回收站")
    @RequestMapping(value = "/dlDataset/deleteDlDataset",method = RequestMethod.POST)
    public ResponseResult deleteDatasetById(@RequestParam Integer datasetId,
                                            HttpSession httpSession){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = dlDatasetService.deleteDldatasetById(datasetId);
        if (isSuccess){
            return new ResponseResult(true,"001","数据集移入回收站成功");
        }
        return new ResponseResult(false,"002","数据集删除失败");
    }

    /*从回收站单个或批量彻底删除数据集*/
    @ResponseBody
    @ApiOperation(value = "从回收站单个或批量彻底删除数据集")
    @RequestMapping(value = "/dlDataset/deleteDlDatasetCompletelyById",method = RequestMethod.POST)
    public ResponseResult deleteDatasetCompletelyById(@RequestParam String datasetIds,
                                                      HttpSession httpSession){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        String[] ids = datasetIds.split(",");
        boolean isSuccess;
        for (int i=0;i<ids.length;i++){
            isSuccess = dlDatasetService.deleteDldatasetCompletelyById(Integer.parseInt(ids[i]));
            if (!isSuccess){
                return new ResponseResult(false,"002","彻底删除数据集失败");
            }
        }
        return new ResponseResult(true,"001","彻底删除数据集成功");
    }

    /*分页获取回收站中的数据集列表*/
    @ResponseBody
    @ApiOperation(value = "分页获取回收站中的数据集列表")
    @RequestMapping(value = "/dlDataset/getDlDatasetInTrash",method = RequestMethod.POST)
    public ResponseResult getDatasetInTrash(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId=6;
        Map<String,Object> data = dlDatasetService.getDldatasetInTrash(userId,pageNum,pageSize);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        responseResult.setMeta(new MetaData(true,"001","成功分页获取回收站中的数据集列表"));
        return responseResult;
    }

    /*从回收站单个或批量恢复数据集*/
    @ResponseBody
    @ApiOperation(value = "从回收站单个或批量恢复数据集")
    @RequestMapping(value = "/dlDataset/restoreDlDataset",method = RequestMethod.POST)
    public ResponseResult restoreDataset(@RequestParam String datasetIds,
                                         HttpSession httpSession){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        String[] ids = datasetIds.split(",");
        boolean isSuccess;
        for (int i=0;i<ids.length;i++){
            isSuccess = dlDatasetService.restoreDldataset(Integer.parseInt(ids[i]));
            if (!isSuccess){
                return new ResponseResult(false,"002","恢复数据集失败");
            }
        }
        return new ResponseResult(true,"001","恢复数据集成功");
    }

    @ResponseBody
    @ApiOperation(value = "返回图片数据集中所有的图片相对地址")
    @RequestMapping(value = "/dlDataset/getImagePaths",method = RequestMethod.POST)
    public ResponseResult getDlDatasetImagePaths(@RequestParam Integer datasetId,
                                                    HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId=6;
        DlDataset dlDataset = dlDatasetService.getDlDatasetByPrimaryId(datasetId);
        if(dlDataset==null)return new ResponseResult(false,"004","数据集不存在");
        if(dlDataset.getType() == 1 && !dlDataset.getFkUserId().equals(userId))
            return new ResponseResult(false,"003","无权限查看该数据集");
        File directory = new File(dlDataset.getDatasetAddr());
        if(!directory.exists())return new ResponseResult(false,"002","数据集已损毁");
        ArrayList<String> imagePaths = dlDatasetService.getImagePaths(directory, dlDataset.getDatasetAddr());
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(imagePaths);
        responseResult.setMeta(new MetaData(true,"001","成功获取图片列表"));
        return responseResult;
    }

    /*restful风格访问图片资源*/
    @ResponseBody
    @ApiOperation(value = "图片访问接口")
    @RequestMapping(value = "/dlDataset/imagePreview/{datasetId}/{relativePath}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getDlDatasetImage(@PathVariable(value = "datasetId") Integer datasetId,
                           @PathVariable(value = "relativePath") String relativePath,
                           HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        //Integer userId=6;
        DlDataset dlDataset = dlDatasetService.getDlDatasetByPrimaryId(datasetId);
        if(dlDataset==null)return null;
        if(dlDataset.getType() == 1 && !dlDataset.getFkUserId().equals(userId)) return null;

        String absolutePath = dlDataset.getDatasetAddr() + File.separator + relativePath;
        File file = new File(absolutePath);
        byte[] bytes = null;
        try{
            FileInputStream inputStream = new FileInputStream(file);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return bytes;
    }

}
