package com.bdilab.aiflow.service.datasource;

import java.util.Date;
import java.util.Map;

/**
 * @Decription TODO
 * @Author lucienslei
 * @Date 2020/3/24 11:20
 * @Version 4.1
 **/
public interface DataSourceService {


    boolean importDataSource( Integer userID, String name, boolean type, String url, String username, String password, String description, Date createTime);

    /**
     * 创建数据源
     * @return
     */

    boolean deleteDataSource(int datasourceId);
    /**
     * 根据id搜索datasource
     * @return
     */


    boolean updateDataSource( int id,Integer userId, String name, boolean type, String url, String username, String password, String description, Date createTime);
    /**
     * 根据id搜索datasource,然后更新其他信息
     * @return
     */

    boolean addHBaseDatasource(Integer userId,Map<String,String> config,String name,String description);

    boolean updateHbaseDataSource(Integer userId,Integer id,Map<String,String> config,String name,String description);


    Map<String, Object> generatePreview(Integer fkUserId) ;
    

    /**
     * 搜索数据源
     * @param datasetName
     * @param pageNum
     * @param pageSize
     */
}
