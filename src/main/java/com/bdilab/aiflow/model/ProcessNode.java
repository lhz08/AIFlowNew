package com.bdilab.aiflow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @Decription 流程节点，用于获取task间的关系，包括前驱节点，后继节点
 * @Author Humphrey
 * @Date 2019/9/29 16:40
 * @Version 1.0
 **/
public class ProcessNode {

    /**
     * 节点id
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 前驱节点
     */
    private List<ProcessNode> priorNodes;

    /**
     * 后继节点
     */
    private List<ProcessNode> rearNodes;

    /**
     * node类型,0-网关，1-task
     */
    private Integer nodeType;

    public List<ProcessNode> getPriorNode() {
        return priorNodes;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public void setPriorNode(List<ProcessNode> priorNode) {
        this.priorNodes = priorNode;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    public List<ProcessNode> getRearNode() {
        return rearNodes;
    }

    public void setRearNode(List<ProcessNode> rearNode) {
        this.rearNodes = rearNode;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void addPriorNode(ProcessNode node){
        if(priorNodes ==null){
            priorNodes = new ArrayList<>();
        }
        priorNodes.add(node);
    }

    public void addRearNode(ProcessNode node){
        if(rearNodes ==null){
            rearNodes = new ArrayList<>();
        }
        rearNodes.add(node);
    }

    /**
     * 获取前置任务节点id，使用“，”隔开多个id
     * @return
     */
    public String getPriorTaskIds(){
        if(priorNodes==null){
            return null;
        }
        String taskIds = "";
        for(ProcessNode node:priorNodes){
            if(node.getNodeType()==1){
                taskIds= taskIds+node.getNodeId()+", ";
            }
            else{
                String preTaskIds = node.getPriorTaskIds();
                if(preTaskIds!=null){
                    taskIds = taskIds+preTaskIds;
                }
            }
        }
        return taskIds;
    }


    /**
     * 获取后置任务节点id，使用“，”隔开多个id
     * @return
     */
    public String getRearTaskIds(){
        if(rearNodes==null){
            return null;
        }
        String taskIds = "";
        for(ProcessNode node:rearNodes){
            if(node.getNodeType()==1){
                taskIds= taskIds+node.getNodeId()+", ";
            }
            else{
                String preTaskIds = node.getRearTaskIds();
                if(preTaskIds!=null){
                    taskIds = taskIds+preTaskIds;
                }
            }
        }
        return taskIds;
    }

}
