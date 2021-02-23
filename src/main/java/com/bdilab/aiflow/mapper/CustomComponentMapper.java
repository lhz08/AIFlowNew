package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.CustomComponent;
import com.bdilab.aiflow.model.component.CustomComponentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomComponentMapper {

    /**
     * 根据id查找指定CustomComponent
     * @param componentId
     * @return
     */
    CustomComponent selectCustomComponentById(Integer componentId);

    /**
     * 在custom_component表中插入自定义组件信息
     * @param customComponent 自定义组件信息
     * @return
     */
    int insertCustomComponent(CustomComponent customComponent);

    /**
     * 删除自定义组件到回收站
     * @param componentId 要删除的自定义组件id
     * @return
     */
    int deleteComponent(Integer componentId);

    /**
     * 根据组件id获取component_info表中的外键id
     * @param componentIds 要获取外键的组件id列表
     * @return
     */
    List<Integer> selectFkComponentInfoIds(List<Integer> componentIds);

    /**
     * 永久删除custom_component表中的自定义组件信息
     * @param componentIds 要永久删除的自定义组件id列表
     * @return
     */
    int deleteComponentPermanently(List<Integer> componentIds);

    /**
     * 还原回收站中的组件
     * @param componentIds 要还原的组件id列表
     * @return
     */
    int restoreComponent(List<Integer> componentIds);

    List<CustomComponentInfo> selectCustomComponentByKeyword(@Param("keyword")String keyword, @Param("type")int type);

    List<CustomComponentInfo> selectCustomComponentByTag(@Param("tag")String tag, @Param("type")int type);
    CustomComponent selectCustomComponentByFkComponentId(int fkComponentInfoId);
    List<CustomComponent> getCustomComponentByUserIdAndType(@Param("userId")int userId, @Param("type")int type,@Param("isDeleted") int isDeleted);
    List<CustomComponent> loadCustomComponentByUserIdAndType(@Param("userId")int userId);
}