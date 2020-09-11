package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.Dataset;

import java.util.List;

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

    List<Dataset> fuzzySelectUserDatasetByName(Integer userId, String datasetName);

    List<Dataset> fuzzySelectPublicDatasetByTags(String datasetTags);

    List<Dataset> fuzzySelectUserDatasetByTags(Integer userId, String datasetTags);
}