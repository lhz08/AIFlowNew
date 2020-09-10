package com.bdilab.aiflow.service.component;

import com.bdilab.aiflow.model.component.ComponentCreateInfo;
import com.bdilab.aiflow.model.component.CustomComponentInfo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomComponentService {

    /**
     * 保存自定义组件
     * @param userId 用户id
     * @param componentFile 要保存的yaml文件，流程组件没有yaml文件
     * @param componentCreateInfo 专门用来保存组件创建信息的对象
     * @return
     */
    Boolean saveComponent(Integer userId, MultipartFile componentFile, ComponentCreateInfo componentCreateInfo);

    /**
     * 删除自定义组件到回收站
     * @param componentId 要删除的自定义组件id
     * @return
     */
    Boolean deleteComponent(Integer componentId);

    /**
     * 永久删除custom_component表中的自定义组件信息
     * @param componentIds 要永久删除的自定义组件id列表
     * @return
     */
    Boolean deleteComponentPermanently(List<Integer> componentIds);

    /**
     * 还原回收站中的组件
     * @param componentIds 要还原的组件id列表
     * @return
     */
    Boolean restoreComponent(List<Integer> componentIds);

    /**
     * 查询自定义组件
     * @param tag 根据标签查询用户自定义组件
     * @return
     */
    PageInfo<CustomComponentInfo> selectComponentByTag(String tag, int type, int pageNum, int pageSize);
    /**
     * 查询自定义组件
     * @param keyword 根据关键字查询用户自定义组件
     * @return
     */
    PageInfo<CustomComponentInfo> selectComponentByKeyword(String keyword, int type, int pageNum, int pageSize);
    PageInfo<CustomComponentInfo> loadCustomComponentByUserIdAndType(int userId, int pageNum, int pageSize, int type);
}
