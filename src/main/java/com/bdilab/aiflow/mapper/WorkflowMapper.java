package com.bdilab.aiflow.mapper;


import com.bdilab.aiflow.model.Workflow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkflowMapper {

    /**
     *
     * @param workflow
     * @return
     */
    int insertWorkflow(Workflow workflow);


    /**
     * 更新流程
     * @param workflow
     * @return
     */
    int updateWorkflow(Workflow workflow);

    /**
     * 更新流程的删除情况
     * @param workflow
     * @return
     */
    int updateWorkflowIsDeleted(Workflow workflow);

    /**
     * 通过流程id获取流程
     * @param workflowId
     * @return
     */
    Workflow selectWorkflowById(Integer workflowId);

    /**
     * 获取符合条件的所有流程
     * @param workflow
     * @return
     */
    List<Workflow> selectAllWorkflow(Workflow workflow);

    /*
    删除一个流程
     */
    int deleteWorkflowById(Integer workflowId);

    List<Workflow> selectWorkflowByUserId(Integer userId);

}