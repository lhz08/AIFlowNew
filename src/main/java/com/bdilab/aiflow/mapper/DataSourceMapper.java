package com.bdilab.smartanalyseplatform.mapper;

import com.bdilab.smartanalyseplatform.model.DataSource;
import com.bdilab.smartanalyseplatform.model.Dataset;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * @param datasourceId
     * @return
     */
    boolean updateDataSource(DataSource datasource);



    DataSource selectDataSourceById(int id);



    List<DataSource> selectAllDataSource(Integer fkUserId);
   


}