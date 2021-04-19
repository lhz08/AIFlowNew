package com.bdilab.aiflow.service.experiment;

import com.bdilab.aiflow.model.ExperimentRunningJsonResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 用来存储和组装前端制图需要的json字符串，从结果csv->jsonString
 * @author 鲢鱼QAQ
 * @date 2021/4/2
 */

public interface ExperimentRunningJsonResultService {


    /**
     * 通过实验运行id和组件id得到结果，转换成前端可用JSON字符串格式
     * @param runningId 实验运行id
     * @param componentId 组件info id，用来从数据库中获得结果文件地址
     * @param type 12热力图，13折线图
     */
    Map<String,Object> getResultJson(Integer runningId, Integer componentId, Integer type);

    /**
     * 新增json结果
     * @param fkExperimentRunningId
     * @param resultJsonString
     * @return
     */
    ExperimentRunningJsonResult createExperimentRunningJsonResult(Integer fkExperimentRunningId,String resultJsonString);


    /**
     * 获取实验运行id对应的json结果
     * @param experimentRunningId
     * @return
     */
    List<ExperimentRunningJsonResult> getExperimentRunningJsonResultByRunningId(Integer experimentRunningId);

    /*###以下方法勿删###*/

    /**
     * 新增json结果
     * @param fkExperimentRunningId
     * @param resultJsonString
     * @return
     */
    ExperimentRunningJsonResult createExperimentRunningJsonResult(Integer fkExperimentRunningId,Integer fkComponentInfoId, String resultJsonString);

    /**
     * 更新实验json结果
     * @param experimentRunningJsonResult
     */
    boolean updateExperimentRunningJsonResult(ExperimentRunningJsonResult experimentRunningJsonResult);

    /**
     * 获取id对应的json结果
     * @param experimentRunningJsonResultId
     * @return
     */
    ExperimentRunningJsonResult getExperimentRunningJsonResultById(Integer experimentRunningJsonResultId);

    /**
     * 获取试验运行id和组件id对应的json结果
     * @param fkExperimentRunningId
     * @param fkComponentInfoId
     * @return
     */
    ExperimentRunningJsonResult selectExperimentRunningJsonResultByExperimentRunningIdAndComponentInfoId(Integer fkExperimentRunningId, Integer fkComponentInfoId);

    boolean deleteExperimentRunningJsonResult(Integer experimentRunningJsonResultId);
}
