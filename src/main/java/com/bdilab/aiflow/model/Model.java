package com.bdilab.aiflow.model;

import java.util.Date;

public class Model {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column model.id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column model.name
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column model.fk_user_id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    private Integer fkUserId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column model.fk_running_id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    private Integer fkRunningId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column model.is_deleted
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    private Byte isDeleted;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column model.model_file_addr
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    private String modelFileAddr;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column model.create_time
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column model.model_desc
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    private String modelDesc;
    private Integer fkComponentId;
    private Integer isSaved;

    public String getBasicConclusion() {
        return basicConclusion;
    }

    public void setBasicConclusion(String basicConclusion) {
        this.basicConclusion = basicConclusion;
    }

    public Double getTestLoss() {
        return testLoss;
    }

    public void setTestLoss(Double testLoss) {
        this.testLoss = testLoss;
    }

    public Double getTrainLoss() {
        return trainLoss;
    }

    public void setTrainLoss(Double trainLoss) {
        this.trainLoss = trainLoss;
    }

    private String basicConclusion;
    private Double testLoss;

    private Double trainLoss;

    public Integer getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(Integer isSaved) {
        this.isSaved = isSaved;
    }

    public Integer getFkComponentId() {
        return fkComponentId;
    }

    public void setFkComponentId(Integer fkComponentId) {
        this.fkComponentId = fkComponentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model.id
     *
     * @return the value of model.id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model.id
     *
     * @param id the value for model.id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model.name
     *
     * @return the value of model.name
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model.name
     *
     * @param name the value for model.name
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model.fk_user_id
     *
     * @return the value of model.fk_user_id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public Integer getFkUserId() {
        return fkUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model.fk_user_id
     *
     * @param fkUserId the value for model.fk_user_id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public void setFkUserId(Integer fkUserId) {
        this.fkUserId = fkUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model.fk_running_id
     *
     * @return the value of model.fk_running_id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public Integer getFkRunningId() {
        return fkRunningId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model.fk_running_id
     *
     * @param fkRunningId the value for model.fk_running_id
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public void setFkRunningId(Integer fkRunningId) {
        this.fkRunningId = fkRunningId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model.is_deleted
     *
     * @return the value of model.is_deleted
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public Byte getIsDeleted() {
        return isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model.is_deleted
     *
     * @param isDeleted the value for model.is_deleted
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model.model_file_addr
     *
     * @return the value of model.model_file_addr
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public String getModelFileAddr() {
        return modelFileAddr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model.model_file_addr
     *
     * @param modelFileAddr the value for model.model_file_addr
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public void setModelFileAddr(String modelFileAddr) {
        this.modelFileAddr = modelFileAddr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model.create_time
     *
     * @return the value of model.create_time
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model.create_time
     *
     * @param createTime the value for model.create_time
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model.model_desc
     *
     * @return the value of model.model_desc
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public String getModelDesc() {
        return modelDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model.model_desc
     *
     * @param modelDesc the value for model.model_desc
     *
     * @mbggenerated Thu Sep 03 11:47:01 CST 2020
     */
    public void setModelDesc(String modelDesc) {
        this.modelDesc = modelDesc;
    }
}