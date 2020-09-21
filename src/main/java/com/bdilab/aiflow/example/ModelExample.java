package com.bdilab.aiflow.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public ModelExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
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
     * This method corresponds to the database table model
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
     * This method corresponds to the database table model
     *
     * @mbg.generated Mon Aug 31 13:47:58 CST 2020
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table model
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
     * This class corresponds to the database table model
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

        public Criteria andFkUserIdIsNull() {
            addCriterion("fk_user_id is null");
            return (Criteria) this;
        }

        public Criteria andFkUserIdIsNotNull() {
            addCriterion("fk_user_id is not null");
            return (Criteria) this;
        }

        public Criteria andFkUserIdEqualTo(Integer value) {
            addCriterion("fk_user_id =", value, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdNotEqualTo(Integer value) {
            addCriterion("fk_user_id <>", value, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdGreaterThan(Integer value) {
            addCriterion("fk_user_id >", value, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("fk_user_id >=", value, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdLessThan(Integer value) {
            addCriterion("fk_user_id <", value, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdLessThanOrEqualTo(Integer value) {
            addCriterion("fk_user_id <=", value, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdIn(List<Integer> values) {
            addCriterion("fk_user_id in", values, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdNotIn(List<Integer> values) {
            addCriterion("fk_user_id not in", values, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdBetween(Integer value1, Integer value2) {
            addCriterion("fk_user_id between", value1, value2, "fkUserId");
            return (Criteria) this;
        }

        public Criteria andFkUserIdNotBetween(Integer value1, Integer value2) {
            addCriterion("fk_user_id not between", value1, value2, "fkUserId");
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

        public Criteria andIsDeletedIsNull() {
            addCriterion("is_deleted is null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNotNull() {
            addCriterion("is_deleted is not null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedEqualTo(Byte value) {
            addCriterion("is_deleted =", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotEqualTo(Byte value) {
            addCriterion("is_deleted <>", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThan(Byte value) {
            addCriterion("is_deleted >", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThanOrEqualTo(Byte value) {
            addCriterion("is_deleted >=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThan(Byte value) {
            addCriterion("is_deleted <", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThanOrEqualTo(Byte value) {
            addCriterion("is_deleted <=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIn(List<Byte> values) {
            addCriterion("is_deleted in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotIn(List<Byte> values) {
            addCriterion("is_deleted not in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedBetween(Byte value1, Byte value2) {
            addCriterion("is_deleted between", value1, value2, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotBetween(Byte value1, Byte value2) {
            addCriterion("is_deleted not between", value1, value2, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrIsNull() {
            addCriterion("model_file_addr is null");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrIsNotNull() {
            addCriterion("model_file_addr is not null");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrEqualTo(String value) {
            addCriterion("model_file_addr =", value, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrNotEqualTo(String value) {
            addCriterion("model_file_addr <>", value, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrGreaterThan(String value) {
            addCriterion("model_file_addr >", value, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrGreaterThanOrEqualTo(String value) {
            addCriterion("model_file_addr >=", value, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrLessThan(String value) {
            addCriterion("model_file_addr <", value, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrLessThanOrEqualTo(String value) {
            addCriterion("model_file_addr <=", value, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrLike(String value) {
            addCriterion("model_file_addr like", value, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrNotLike(String value) {
            addCriterion("model_file_addr not like", value, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrIn(List<String> values) {
            addCriterion("model_file_addr in", values, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrNotIn(List<String> values) {
            addCriterion("model_file_addr not in", values, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrBetween(String value1, String value2) {
            addCriterion("model_file_addr between", value1, value2, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andModelFileAddrNotBetween(String value1, String value2) {
            addCriterion("model_file_addr not between", value1, value2, "modelFileAddr");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table model
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
     * This class corresponds to the database table model
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