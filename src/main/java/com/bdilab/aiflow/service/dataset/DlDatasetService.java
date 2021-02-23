package com.bdilab.aiflow.service.dataset;

import com.bdilab.aiflow.model.DlDataset;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public interface DlDatasetService {
    boolean insertUserDlDataset(Integer userId, String datasetName, String tags, String datasetAddr, String datasetDesc,Integer isAnnotation,Integer originFileType);

    Map<String, Object> searchPublicDlDatasetByName(String datasetName, int pageNum, int pageSize);

    Map<String, Object> searchUserDlDatasetByName(Integer userId, String datasetName, int pageNum, int pageSize);

    Map<String, Object> searchPublicDlDatasetByTags(String datasetTags, int pageNum, int pageSize);

    Map<String, Object> searchUserDlDatasetByTags(Integer userId, String datasetTags, int pageNum, int pageSize);

    DlDataset getDlDatasetByPrimaryId(Integer id);

    HttpServletResponse downloadDlDataset(File file, HttpServletResponse response) throws IOException;


    Map<String, Object> getPublicDldataset(int pageNum, int pageSize);

    Map<String, Object> getUserDldataset(Integer userId, int pageNum, int pageSize);

    boolean deleteDldatasetById(Integer datasetId);

    boolean deleteDldatasetCompletelyById(int datasetId);

    Map<String, Object> getDldatasetInTrash(Integer userId, int pageNum, int pageSize);

    boolean restoreDldataset(int datasetId);


    ArrayList<String> getImagePaths(File file,String prePath);

}
