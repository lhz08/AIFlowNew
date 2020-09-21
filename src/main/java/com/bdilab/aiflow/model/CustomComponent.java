package com.bdilab.aiflow.model;

import lombok.Data;

import java.util.Date;

@Data
public class CustomComponent {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column custom_component.id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column custom_component.fk_user_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private Integer fkUserId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column custom_component.fk_component_info_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private Integer fkComponentInfoId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column custom_component.is_deleted
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private Byte isDeleted;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column custom_component.type
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private Byte type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column custom_component.source_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private String sourceId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column custom_component.create_time
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column custom_component.id
     *
     * @return the value of custom_component.id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column custom_component.id
     *
     * @param id the value for custom_component.id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column custom_component.fk_user_id
     *
     * @return the value of custom_component.fk_user_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public Integer getFkUserId() {
        return fkUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column custom_component.fk_user_id
     *
     * @param fkUserId the value for custom_component.fk_user_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setFkUserId(Integer fkUserId) {
        this.fkUserId = fkUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column custom_component.fk_component_info_id
     *
     * @return the value of custom_component.fk_component_info_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public Integer getFkComponentInfoId() {
        return fkComponentInfoId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column custom_component.fk_component_info_id
     *
     * @param fkComponentInfoId the value for custom_component.fk_component_info_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setFkComponentInfoId(Integer fkComponentInfoId) {
        this.fkComponentInfoId = fkComponentInfoId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column custom_component.is_deleted
     *
     * @return the value of custom_component.is_deleted
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public Byte getIsDeleted() {
        return isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column custom_component.is_deleted
     *
     * @param isDeleted the value for custom_component.is_deleted
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column custom_component.type
     *
     * @return the value of custom_component.type
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column custom_component.type
     *
     * @param type the value for custom_component.type
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column custom_component.source_id
     *
     * @return the value of custom_component.source_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column custom_component.source_id
     *
     * @param sourceId the value for custom_component.source_id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column custom_component.create_time
     *
     * @return the value of custom_component.create_time
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column custom_component.create_time
     *
     * @param createTime the value for custom_component.create_time
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}