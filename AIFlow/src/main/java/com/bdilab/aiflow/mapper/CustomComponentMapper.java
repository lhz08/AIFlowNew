package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.CustomComponent;
import com.bdilab.aiflow.model.component.CustomComponentInfo;

import java.util.List;

public interface CustomComponentMapper {

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

    /**
     * 加载回收站中的组件
     * @param userId 用户id
     * @param type 要加载的组件类型
     * @return
     */
    List<CustomComponentInfo> selectComponentInTrash(Integer userId, Integer type);

    List<CustomComponentInfo> selectCustomComponentByKeyword(String keyword, int type);

    List<CustomComponentInfo> selectCustomComponentByTag(String tag, int type);

    List<CustomComponentInfo> loadCustomComponentByUserIdAndType(int userId, int type);
}