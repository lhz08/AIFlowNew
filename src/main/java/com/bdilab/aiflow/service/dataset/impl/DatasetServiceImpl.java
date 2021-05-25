package com.bdilab.aiflow.service.dataset.impl;


import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.mysql.MysqlConnection;
import com.bdilab.aiflow.common.mysql.MysqlUtils;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.common.utils.MinioFileUtils;
import com.bdilab.aiflow.mapper.DataSourceMapper;
import com.bdilab.aiflow.mapper.DatasetMapper;
import com.bdilab.aiflow.model.DataSource;
import com.bdilab.aiflow.model.Dataset;
import com.bdilab.aiflow.service.dataset.DatasetService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.minio.errors.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Date;

/**
 * @author
 * @create 2020-08-28 15:13
 */
@Service
public class DatasetServiceImpl implements DatasetService {

    @Resource
    DatasetMapper datasetMapper;

    @Autowired
    FilePathConfig filePathConfig;

    @Autowired
    DataSourceMapper dataSourceMapper;


    @Value("${minio.host}")
    private String host;

    @Value("${minio.access_key}")
    private String username;

    @Value("${minio.secret_key}")
    private String password;
    /*
     * 分页获得公开数据集信息列表
     */
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public Map<String, Object> getPublicDataset(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.getPublicDataset();
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    @Override
    public boolean uploadDatasetToMinio(MultipartFile file, String datasetName, String tags, String datasetDesc, Integer userId){
        String filename = file.getOriginalFilename();
        //获取文件的后缀名
        String suffixName = filename.substring(filename.lastIndexOf("."));
        //为上传的文件生成一个随机名字
        filename = UUID.randomUUID()+suffixName;
        MinioFileUtils minioFileUtils = new MinioFileUtils(host,username,password,false);
        String bucketName = "dataset";
        try {
            minioFileUtils.createBucket(bucketName);
            System.out.println(bucketName+file.getOriginalFilename()+filename);
            minioFileUtils.uploadFile(bucketName,file,filename);
        }catch (Exception e){
            return false;
        }
        insertUserDataset(userId,datasetName,tags,filename,datasetDesc);
        return true;
    }

    /*分页获得用户自定义数据集信息列表*/
    @Override
    public Map<String, Object> getUserDataset(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.getUserDataset(userId);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*注册数据集#{name},#{type},#{fkUserId},#{tags},#{isDeleted},#{datasetAddr},#{datasetDesc},#{createTime}*/
    @Override
    public boolean insertUserDataset(Integer userId,String datasetName, String tags, String filePath, String datasetDesc){
        Dataset dataset=new Dataset();
        dataset.setName(datasetName);
        dataset.setType(1);
        dataset.setTags(tags);
        dataset.setFkUserId(userId);
        dataset.setIsDeleted((byte) 0);
        dataset.setDatasetAddr(filePath);
        dataset.setDatasetDesc(datasetDesc);
        Date date = new Date();
        dataset.setCreateTime(date);
        dataset.setIsDeleted((byte)0);
        return datasetMapper.insertDataset(dataset);
    }

    @Override
    public boolean importApiDataset(String sendUrl, String datasetName, Integer userId, String datasetTags, String datasetDesc) {

        try {
            URL url = new URL(sendUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            httpURLConnection.connect();

            int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                InputStream inputStream = httpURLConnection.getInputStream();//得到网络的输入流
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");//编码格式
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//存入Buffer缓冲区
                String line = "";
                StringBuffer buffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }
                String filePath = filePathConfig.getDatasetUrl()+ File.separator+ UUID.randomUUID()+".csv";
                FileUtils.writeIntoCsv(buffer.toString(),filePath);
                insertUserDataset(userId,datasetName,datasetTags,filePath,datasetDesc);
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean importMySqlDataSourceByField(Integer dataSourceId,String tableName,Integer userId,String datasetName, String datasetDesc,String tags,List<String> field){
        PreparedStatement preparedStatement=null;
        DataSource dataSource = dataSourceMapper.selectDataSourceById(dataSourceId);
        String databaseUrl = dataSource.getUrl();
        String userName = dataSource.getUserName();
        String password = dataSource.getPassword();
        //测试
//        String databaseUrl = "jdbc:mysql://localhost:3306/aiflow_studio";
//        String userName = "root";
//        String password = "123456";

        String filePath = filePathConfig.getDatasetUrl()+ File.separator+ UUID.randomUUID()+".csv";
        List<String> colName = new ArrayList<>();
        boolean returnValue = false;
        File datasetFile = new File(filePath);
        Dataset dataset = new Dataset();
        MysqlConnection mysqlConnection =new MysqlConnection();
        Connection con =null;
        //获取用户mysql连接
        try {
            mysqlConnection.setDriver("com.mysql.jdbc.Driver");
            mysqlConnection.setUrl(databaseUrl);
            mysqlConnection.setUser(userName);
            mysqlConnection.setPassword(password);
            con = mysqlConnection.getConn();
        }catch (Exception e){
            logger.error("数据库连接失败");
            return false;
        }
        if (!datasetFile.exists()) {
            try {
                datasetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } try {
            String sql = "select * from  " + tableName;
            preparedStatement = con.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int colSize = metaData.getColumnCount();
            int [] a = new int[100];
            int k =0;
            String buffer = "";
            //得到所有的列名
            for(int i =1;i<=colSize;i++)
            {
                colName.add(metaData.getColumnName(i));
            }
            if(colName.containsAll(field)) {
                for (int i = 0; i < field.size(); i++) {
                    for (int j = 1; j <= colSize; j++) {
                        if (field.get(i).equals(metaData.getColumnName(j))) {
                            a[k] = j;//记录和传入字段相等的列的序号
                            k++;
                        }
                    }
                    returnValue = true;
                }
                //先写入列名
                k = 0;
                while (a[k] != 0) {
                    buffer += metaData.getColumnName(a[k]) + ",";
                    k++;
                }
                buffer += "\n";
                FileUtils.fieldwriteToCsv(buffer, filePath);
                //写完列名，buffer置为空
                buffer ="";
                k = 0;
                int totalSize = 0;
                int batchSize = 10;
                while ((rs.next())) {
                    totalSize++;
                    if(totalSize%batchSize==0){
                        //写入一次
                        FileUtils.fieldwriteToCsv(buffer,filePath);
                        //写入完毕，将buffer设为空
                        buffer ="";
                    }
                    while (a[k] != 0) {
                        buffer += rs.getString(a[k]) + ",";
                        k++;
                    }
                    k = 0;
                    buffer += "\n";
                }
                FileUtils.fieldwriteToCsv(buffer, filePath);
                dataset.setName(datasetName);
                dataset.setDatasetDesc(datasetDesc);
                //type=1表示用户上传的数据集
                dataset.setType(1);
                dataset.setFkUserId(userId);
                dataset.setIsDeleted((byte)0);
                dataset.setTags(tags);
                dataset.setDatasetAddr(filePath);
                dataset.setCreateTime(new Date());
                datasetMapper.insertDataset(dataset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysqlConnection.endConnection();
        }
        return returnValue;
    }

    @Override
    public boolean importMysqlDataSourceBySql(Integer datasourceId,Integer userId,String datasetName, String datasetDesc,String tags,String sql) {
        DataSource dataSource = dataSourceMapper.selectDataSourceById(datasourceId);

        String databaseUrl = dataSource.getUrl();
        String userName = dataSource.getUserName();
        String password = dataSource.getPassword();
        String filePath = filePathConfig.getDatasetUrl()+ File.separator+ UUID.randomUUID()+".csv";
        MysqlConnection mysqlConnection =new MysqlConnection();
        Connection con =null;
        //获取用户mysql连接
        try {
            mysqlConnection.setDriver("com.mysql.jdbc.Driver");
            mysqlConnection.setUrl(databaseUrl);
            mysqlConnection.setUser(userName);
            mysqlConnection.setPassword(password);
            con = mysqlConnection.getConn();
        }catch (Exception e){
            logger.error("数据库连接失败");
            return false;
        }
        boolean returnValue = false;

        List<String> columnName = new ArrayList<>();
        File datasetFile = new File(filePath);
        Dataset dataset = new Dataset();
        if (!datasetFile.exists()) {
            try {
                datasetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            PreparedStatement preparedStatement = null;
            preparedStatement = con.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            returnValue = true;
            int colSize = metaData.getColumnCount();
            String buffer = "";
            for (int i = 1; i <= colSize; i++) {
                columnName.add(metaData.getColumnName(i));
                buffer += metaData.getColumnName(i) + ",";
            }
            buffer += "\n";
            FileUtils.fieldwriteToCsv(buffer, filePath);
            buffer = "";
            int totalSize = 0;
            int batchSize = 10;
            while (rs.next()) {
                totalSize++;
                if (totalSize % batchSize == 0) {
                    //写入一次
                    FileUtils.fieldwriteToCsv(buffer, filePath);
                    //写入完毕，将buffer设为空
                    buffer = "";
                }
                for (int i = 1; i <= colSize; i++) {
                    buffer += rs.getString(i) + ",";
                }
                buffer += "\n";
            }
            FileUtils.fieldwriteToCsv(buffer, filePath);
            dataset.setName(datasetName);
            dataset.setDatasetDesc(datasetDesc);
            dataset.setType(1);
            dataset.setFkUserId(userId);
            dataset.setIsDeleted((byte)0);
            dataset.setTags(tags);
            dataset.setDatasetAddr(filePath);
            dataset.setCreateTime(new Date());
            datasetMapper.insertDataset(dataset);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mysqlConnection.endConnection();
        }
        return returnValue;
    }

    /*删除数据集--移入回收站*/
    @Override
    public boolean deleteDatasetById(Integer datasetId) {
        return datasetMapper.deleteDatasetById(datasetId);
    }

    /*删除数据集--彻底删除*/
    @Override
    public boolean deleteDatasetCompletelyById(Integer datasetId)  {
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        MinioFileUtils minioFileUtils = new MinioFileUtils(host,username,password,false);
        minioFileUtils.deleteFile("user"+dataset.getFkUserId(),dataset.getDatasetAddr());
//        File file = new File(dataset.getDatasetAddr());
//        file.delete();
        return datasetMapper.deleteDatasetCompletelyById(datasetId);
    }
/*    @Override
    public boolean deleteDatasetCompletelyById(Integer datasetId)  {
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        Connection connection=HBaseConnection.getConn();
        try{
        HBaseUtils.deleteTable(dataset.getName(),connection);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return datasetMapper.deleteDatasetCompletelyById(datasetId);
    }*/

    /*分页获取回收站中的数据集列表*/
    @Override
    public Map<String, Object> getDatasetInTrash(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.getDatasetInTrash(userId);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }
    /*从回收站恢复数据集*/
    @Override
    public boolean restoreDataset(Integer datasetId) {
        return datasetMapper.restoreDataset(datasetId);
    }

    /*按名称分页搜索公开数据集*/
    @Override
    public Map<String, Object> searchPublicDatasetByName(String datasetName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.fuzzySelectPublicDatasetByName(datasetName);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*按名称分页搜索用户自定义数据集*/
    @Override
    public Map<String, Object> searchUserDatasetByName(Integer userId, String datasetName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.fuzzySelectUserDatasetByName(userId,datasetName);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }
    /*按tags分页搜索公开数据集*/
    @Override
    public Map<String, Object> searchPublicDatasetByTags(String datasetTags, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.fuzzySelectPublicDatasetByTags(datasetTags);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    /*按tags分页搜索用户自定义数据集*/
    @Override
    public Map<String, Object> searchUserDatasetByTags(Integer userId, String datasetTags, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Dataset> datasetList = datasetMapper.fuzzySelectUserDatasetByTags(userId,datasetTags);
        PageInfo pageInfo = new PageInfo<>(datasetList);
        Map<String,Object> data = new HashMap<>(3);
        data.put("Dataset List",datasetList);
        data.put("Total Page Num",pageInfo.getPages());
        data.put("Total",pageInfo.getTotal());
        return data;
    }

    @Override
    public File downloadDataset(Integer datasetId) {
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        File file = new File(dataset.getDatasetAddr());
        return file;
    }

    /*获取前十条记录用作预览*/
    @Override
    public Map<String, Object> getPreviewList(Integer datasetId) {
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        String filePath=filePathConfig.getDatasetPath()+File.separatorChar+dataset.getDatasetAddr();
        Map<String, Object> data = new HashMap<>();
        List<String[]> csvContent = FileUtils.csvContentPreview1(filePath);
        data.put("content",csvContent);
        data.put("total",csvContent == null?0:csvContent.size());
        data.put("desc",dataset.getDatasetDesc());
        return data;
    }
  /*  @Override
    public Map<String, Object> getPreviewList(Integer datasetId) {
        Connection connection=HBaseConnection.getConn();
        Dataset dataset = datasetMapper.selectDatasetById(datasetId);
        Map<String, Object> data = new HashMap<>();
        List<String> hbaseContent = HBaseUtils.getTableDatalimit10(dataset.getName(),connection);
        data.put("content",hbaseContent);
        data.put("total",hbaseContent == null?0:hbaseContent.size());
        data.put("desc",dataset.getDatasetDesc());
        return data;
    }*/
  @Override
  public HttpServletResponse downloadDatasetFromMinio(Integer userId,Integer datasetId,HttpServletResponse response) {
      MinioFileUtils minioFileUtils = new MinioFileUtils(host,username,password,false);
      Dataset dataset = datasetMapper.selectDatasetById(datasetId);
      String bucketName = "dataset";
      String filePath = dataset.getDatasetAddr();
      try {
          InputStream inputStream = minioFileUtils.downLoadFile(bucketName, filePath);
          byte buf[] = new byte[1024];
          int length = 0;
          response.reset();
          response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
          response.setContentType("application/octet-stream");
          response.setCharacterEncoding("UTF-8");
          OutputStream outputStream = response.getOutputStream();
          while ((length = inputStream.read(buf)) > 0) {
              outputStream.write(buf, 0, length);
          }
          outputStream.close();
      }catch (Exception e){
          e.printStackTrace();

      }
      return response;
  }

}
