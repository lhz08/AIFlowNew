package com.bdilab.aiflow.model;

import lombok.Data;

@Data
public class ComponentInfo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.name
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.tags
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private String tags;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.is_custom
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private Byte isCustom;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.component_yaml_addr
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private String componentYamlAddr;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.input_stub
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private String inputStub;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.output_stub
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private String outputStub;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.graph_type_ids
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private String graphTypeIds;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column component_info.component_desc
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    private String componentDesc;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.id
     *
     * @return the value of component_info.id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.id
     *
     * @param id the value for component_info.id
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.name
     *
     * @return the value of component_info.name
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.name
     *
     * @param name the value for component_info.name
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.tags
     *
     * @return the value of component_info.tags
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public String getTags() {
        return tags;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.tags
     *
     * @param tags the value for component_info.tags
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.is_custom
     *
     * @return the value of component_info.is_custom
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public Byte getIsCustom() {
        return isCustom;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.is_custom
     *
     * @param isCustom the value for component_info.is_custom
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setIsCustom(Byte isCustom) {
        this.isCustom = isCustom;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.component_yaml_addr
     *
     * @return the value of component_info.component_yaml_addr
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public String getComponentYamlAddr() {
        return componentYamlAddr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.component_yaml_addr
     *
     * @param componentYamlAddr the value for component_info.component_yaml_addr
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setComponentYamlAddr(String componentYamlAddr) {
        this.componentYamlAddr = componentYamlAddr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.input_stub
     *
     * @return the value of component_info.input_stub
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public String getInputStub() {
        return inputStub;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.input_stub
     *
     * @param inputStub the value for component_info.input_stub
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setInputStub(String inputStub) {
        this.inputStub = inputStub;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.output_stub
     *
     * @return the value of component_info.output_stub
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public String getOutputStub() {
        return outputStub;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.output_stub
     *
     * @param outputStub the value for component_info.output_stub
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setOutputStub(String outputStub) {
        this.outputStub = outputStub;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.graph_type_ids
     *
     * @return the value of component_info.graph_type_ids
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public String getGraphTypeIds() {
        return graphTypeIds;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.graph_type_ids
     *
     * @param graphTypeIds the value for component_info.graph_type_ids
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setGraphTypeIds(String graphTypeIds) {
        this.graphTypeIds = graphTypeIds;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column component_info.component_desc
     *
     * @return the value of component_info.component_desc
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public String getComponentDesc() {
        return componentDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column component_info.component_desc
     *
     * @param componentDesc the value for component_info.component_desc
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    public void setComponentDesc(String componentDesc) {
        this.componentDesc = componentDesc;
    }
}