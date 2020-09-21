package com.bdilab.aiflow.example;

import java.util.ArrayList;
import java.util.List;

public class ComponentParameterExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public ComponentParameterExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
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
     * This method corresponds to the database table component_parameter
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
     * This method corresponds to the database table component_parameter
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table component_parameter
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
     * This class corresponds to the database table component_parameter
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

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
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

        public Criteria andParameterTypeIsNull() {
            addCriterion("parameter_type is null");
            return (Criteria) this;
        }

        public Criteria andParameterTypeIsNotNull() {
            addCriterion("parameter_type is not null");
            return (Criteria) this;
        }

        public Criteria andParameterTypeEqualTo(String value) {
            addCriterion("parameter_type =", value, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeNotEqualTo(String value) {
            addCriterion("parameter_type <>", value, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeGreaterThan(String value) {
            addCriterion("parameter_type >", value, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeGreaterThanOrEqualTo(String value) {
            addCriterion("parameter_type >=", value, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeLessThan(String value) {
            addCriterion("parameter_type <", value, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeLessThanOrEqualTo(String value) {
            addCriterion("parameter_type <=", value, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeLike(String value) {
            addCriterion("parameter_type like", value, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeNotLike(String value) {
            addCriterion("parameter_type not like", value, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeIn(List<String> values) {
            addCriterion("parameter_type in", values, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeNotIn(List<String> values) {
            addCriterion("parameter_type not in", values, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeBetween(String value1, String value2) {
            addCriterion("parameter_type between", value1, value2, "parameterType");
            return (Criteria) this;
        }

        public Criteria andParameterTypeNotBetween(String value1, String value2) {
            addCriterion("parameter_type not between", value1, value2, "parameterType");
            return (Criteria) this;
        }

        public Criteria andDefaultValueIsNull() {
            addCriterion("default_value is null");
            return (Criteria) this;
        }

        public Criteria andDefaultValueIsNotNull() {
            addCriterion("default_value is not null");
            return (Criteria) this;
        }

        public Criteria andDefaultValueEqualTo(String value) {
            addCriterion("default_value =", value, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueNotEqualTo(String value) {
            addCriterion("default_value <>", value, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueGreaterThan(String value) {
            addCriterion("default_value >", value, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueGreaterThanOrEqualTo(String value) {
            addCriterion("default_value >=", value, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueLessThan(String value) {
            addCriterion("default_value <", value, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueLessThanOrEqualTo(String value) {
            addCriterion("default_value <=", value, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueLike(String value) {
            addCriterion("default_value like", value, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueNotLike(String value) {
            addCriterion("default_value not like", value, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueIn(List<String> values) {
            addCriterion("default_value in", values, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueNotIn(List<String> values) {
            addCriterion("default_value not in", values, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueBetween(String value1, String value2) {
            addCriterion("default_value between", value1, value2, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andDefaultValueNotBetween(String value1, String value2) {
            addCriterion("default_value not between", value1, value2, "defaultValue");
            return (Criteria) this;
        }

        public Criteria andParameterDescIsNull() {
            addCriterion("parameter_desc is null");
            return (Criteria) this;
        }

        public Criteria andParameterDescIsNotNull() {
            addCriterion("parameter_desc is not null");
            return (Criteria) this;
        }

        public Criteria andParameterDescEqualTo(String value) {
            addCriterion("parameter_desc =", value, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescNotEqualTo(String value) {
            addCriterion("parameter_desc <>", value, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescGreaterThan(String value) {
            addCriterion("parameter_desc >", value, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescGreaterThanOrEqualTo(String value) {
            addCriterion("parameter_desc >=", value, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescLessThan(String value) {
            addCriterion("parameter_desc <", value, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescLessThanOrEqualTo(String value) {
            addCriterion("parameter_desc <=", value, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescLike(String value) {
            addCriterion("parameter_desc like", value, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescNotLike(String value) {
            addCriterion("parameter_desc not like", value, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescIn(List<String> values) {
            addCriterion("parameter_desc in", values, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescNotIn(List<String> values) {
            addCriterion("parameter_desc not in", values, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescBetween(String value1, String value2) {
            addCriterion("parameter_desc between", value1, value2, "parameterDesc");
            return (Criteria) this;
        }

        public Criteria andParameterDescNotBetween(String value1, String value2) {
            addCriterion("parameter_desc not between", value1, value2, "parameterDesc");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table component_parameter
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
     * This class corresponds to the database table component_parameter
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