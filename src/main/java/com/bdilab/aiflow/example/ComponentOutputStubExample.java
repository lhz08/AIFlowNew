package com.bdilab.aiflow.example;

import java.util.ArrayList;
import java.util.List;

public class ComponentOutputStubExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public ComponentOutputStubExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdIsNull() {
            addCriterion("fk_component_info_id is null");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdIsNotNull() {
            addCriterion("fk_component_info_id is not null");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdEqualTo(Integer value) {
            addCriterion("fk_component_info_id =", value, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdNotEqualTo(Integer value) {
            addCriterion("fk_component_info_id <>", value, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdGreaterThan(Integer value) {
            addCriterion("fk_component_info_id >", value, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("fk_component_info_id >=", value, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdLessThan(Integer value) {
            addCriterion("fk_component_info_id <", value, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdLessThanOrEqualTo(Integer value) {
            addCriterion("fk_component_info_id <=", value, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdIn(List<Integer> values) {
            addCriterion("fk_component_info_id in", values, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdNotIn(List<Integer> values) {
            addCriterion("fk_component_info_id not in", values, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdBetween(Integer value1, Integer value2) {
            addCriterion("fk_component_info_id between", value1, value2, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkComponentInfoIdNotBetween(Integer value1, Integer value2) {
            addCriterion("fk_component_info_id not between", value1, value2, "fkComponentInfoId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdIsNull() {
            addCriterion("fk_running_id is null");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdIsNotNull() {
            addCriterion("fk_running_id is not null");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdEqualTo(Integer value) {
            addCriterion("fk_running_id =", value, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdNotEqualTo(Integer value) {
            addCriterion("fk_running_id <>", value, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdGreaterThan(Integer value) {
            addCriterion("fk_running_id >", value, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("fk_running_id >=", value, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdLessThan(Integer value) {
            addCriterion("fk_running_id <", value, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdLessThanOrEqualTo(Integer value) {
            addCriterion("fk_running_id <=", value, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdIn(List<Integer> values) {
            addCriterion("fk_running_id in", values, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdNotIn(List<Integer> values) {
            addCriterion("fk_running_id not in", values, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdBetween(Integer value1, Integer value2) {
            addCriterion("fk_running_id between", value1, value2, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andFkRunningIdNotBetween(Integer value1, Integer value2) {
            addCriterion("fk_running_id not between", value1, value2, "fkRunningId");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeIsNull() {
            addCriterion("output_file_type is null");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeIsNotNull() {
            addCriterion("output_file_type is not null");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeEqualTo(String value) {
            addCriterion("output_file_type =", value, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeNotEqualTo(String value) {
            addCriterion("output_file_type <>", value, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeGreaterThan(String value) {
            addCriterion("output_file_type >", value, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeGreaterThanOrEqualTo(String value) {
            addCriterion("output_file_type >=", value, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeLessThan(String value) {
            addCriterion("output_file_type <", value, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeLessThanOrEqualTo(String value) {
            addCriterion("output_file_type <=", value, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeLike(String value) {
            addCriterion("output_file_type like", value, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeNotLike(String value) {
            addCriterion("output_file_type not like", value, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeIn(List<String> values) {
            addCriterion("output_file_type in", values, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeNotIn(List<String> values) {
            addCriterion("output_file_type not in", values, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeBetween(String value1, String value2) {
            addCriterion("output_file_type between", value1, value2, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputFileTypeNotBetween(String value1, String value2) {
            addCriterion("output_file_type not between", value1, value2, "outputFileType");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameIsNull() {
            addCriterion("output_table_name is null");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameIsNotNull() {
            addCriterion("output_table_name is not null");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameEqualTo(String value) {
            addCriterion("output_table_name =", value, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameNotEqualTo(String value) {
            addCriterion("output_table_name <>", value, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameGreaterThan(String value) {
            addCriterion("output_table_name >", value, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameGreaterThanOrEqualTo(String value) {
            addCriterion("output_table_name >=", value, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameLessThan(String value) {
            addCriterion("output_table_name <", value, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameLessThanOrEqualTo(String value) {
            addCriterion("output_table_name <=", value, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameLike(String value) {
            addCriterion("output_table_name like", value, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameNotLike(String value) {
            addCriterion("output_table_name not like", value, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameIn(List<String> values) {
            addCriterion("output_table_name in", values, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameNotIn(List<String> values) {
            addCriterion("output_table_name not in", values, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameBetween(String value1, String value2) {
            addCriterion("output_table_name between", value1, value2, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputTableNameNotBetween(String value1, String value2) {
            addCriterion("output_table_name not between", value1, value2, "outputTableName");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrIsNull() {
            addCriterion("output_file_addr is null");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrIsNotNull() {
            addCriterion("output_file_addr is not null");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrEqualTo(String value) {
            addCriterion("output_file_addr =", value, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrNotEqualTo(String value) {
            addCriterion("output_file_addr <>", value, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrGreaterThan(String value) {
            addCriterion("output_file_addr >", value, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrGreaterThanOrEqualTo(String value) {
            addCriterion("output_file_addr >=", value, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrLessThan(String value) {
            addCriterion("output_file_addr <", value, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrLessThanOrEqualTo(String value) {
            addCriterion("output_file_addr <=", value, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrLike(String value) {
            addCriterion("output_file_addr like", value, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrNotLike(String value) {
            addCriterion("output_file_addr not like", value, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrIn(List<String> values) {
            addCriterion("output_file_addr in", values, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrNotIn(List<String> values) {
            addCriterion("output_file_addr not in", values, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrBetween(String value1, String value2) {
            addCriterion("output_file_addr between", value1, value2, "outputFileAddr");
            return (Criteria) this;
        }

        public Criteria andOutputFileAddrNotBetween(String value1, String value2) {
            addCriterion("output_file_addr not between", value1, value2, "outputFileAddr");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table component_output_stub
     *
     * @mbg.generated do_not_delete_during_merge Mon Aug 31 13:47:58 CST 2020
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table component_output_stub
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}