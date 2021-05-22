package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.DataSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DataSourceMapper {
    /**
     * 查询所有经典数据集
     * @return
     */

    boolean insertDataSource(DataSource datasource);

    /**
     * 插入一个数据源
     * @param datasourceId
     * @return
     */


    int deleteDataSourceById(int datasourceId);
    /**
     * 根据id搜索datasource
     * @param
     * @return
     */
    boolean updateDataSource(DataSource datasource);



    DataSource selectDataSourceById(int id);



    List<DataSource> selectAllDataSource(Integer fkUserId);
   


}