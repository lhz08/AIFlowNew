package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.EnumValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EnumValueMapper {
    /**
     * 查询一个变量的所有枚举类型
     * @param variableId
     * @return
     */
    List<EnumValue> selectEnumValueByVariableId(Integer variableId);

    /**
     * 插入新的枚举值
     * @param enumValue
     * @return
     */
    int insertEnumValue(EnumValue enumValue);

    /**
     * 查询idList中所有id对应的枚举值
     * @param idList
     * @return
     */
    int deleteEnumValues(@Param("idList") List<Integer> idList);

}