package com.bdilab.aiflow.service.workflow;

import com.bdilab.aiflow.model.Workflow;

import java.util.Map;

public interface WorkflowService {

    /**
     * 新建流程
     * @param workflowName 流程名
     * @param tagString tag字符串
     * @param workflowDesc 流程描述
     * @param userId 用户id
     * @return 流程模型
     */
    Workflow createAndSaveWorkflow(String workflowName, String tagString, String workflowDesc, String workflowXml, String ggeditorObjectString, Integer userId);

    /**
     * 克隆流程
     * @param workflowId
     * @param workflowName
     * @param tagString
     * @param workflowDesc
     * @param userId
     * @return
     */
    Workflow cloneWorkflow(Integer workflowId,String workflowName, String tagString, String workflowDesc, Integer userId);

    /**
     * 修改workflow
     * @param workflowId
     * @param workflowXml
     * @param ggeditorObjectString
     * @return
     */
    boolean updateWorkflow(Integer workflowId, String workflowXml, String ggeditorObjectString, Integer userId);

    /**
     * 搜索单一实验
     * @param workflowId
     * @return
     */
    Workflow selectWorkflowById(Integer workflowId);

    /**
     * 根据userId和isDeleted，组装workflowVO List，包括每个流程和其下属实验
     * @param searchWorkflow
     * @param pageNum
     * @param pageSize
     * @return workflowVO List
     */
    Map<String,Object> selectAllWorkflowByUserIdAndIsDeleted(Workflow searchWorkflow, int pageNum, int pageSize);

    /**
     * 精准搜索流程
     * @param workflow
     * @param experimentName
     * @param pageNum
     * @param pageSize
     * @return
     */
    Map<String,Object> searchWorkflow(Workflow workflow, String experimentName, int pageNum, int pageSize);

    /**
     * 根据userId和isDeleted，组装workflowVO List，包括每个流程的单独信息
     * @param searchWorkflow
     * @param pageNum
     * @param pageSize
     * @return workflowVO List
     */
    Map<String,Object> selectAllWorkflowOnlyByUserIdAndIsDeleted(Workflow searchWorkflow, int pageNum, int pageSize);


    /**
     * 放入流程回收站
     * @param workflow
     * @return
     */
    boolean deleteWorkflow(Workflow workflow);

    /**
     * 彻底删除流程
     * @param workflow
     * @return
     */
    boolean deleteWorkflowTotal(Workflow workflow);

    /**
     * 还原流程
     * @param fkWorkflowId
     */
    boolean restoreWorkflow(Integer fkWorkflowId);


}
