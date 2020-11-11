package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.Dataset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DatasetMapper {

    //@Select("select * from dataset where type=0 and is_deleted=0")
    List<Dataset> getPublicDataset();

    List<Dataset> getUserDataset(Integer userId);

    boolean insertDataset(Dataset dataset);

    Dataset selectDatasetById(Integer datasetId);

    boolean deleteDatasetById(Integer datasetId);

    boolean deleteDatasetCompletelyById(Integer datasetId);

    List<Dataset> getDatasetInTrash(Integer userId);

    boolean restoreDataset(Integer datasetId);

    List<Dataset> fuzzySelectPublicDatasetByName(String datasetName);

    List<Dataset> fuzzySelectUserDatasetByName(@Param("userId")Integer userId, @Param("datasetName")String datasetName);

    List<Dataset> fuzzySelectPublicDatasetByTags(String datasetTags);

    List<Dataset> fuzzySelectUserDatasetByTags(@Param("userId")Integer userId, @Param("datasetTags")String datasetTags);
}