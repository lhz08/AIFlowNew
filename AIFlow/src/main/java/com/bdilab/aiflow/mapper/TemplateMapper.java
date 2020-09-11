package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.Template;

import java.util.List;

public interface TemplateMapper {



    /**
     * 插入一条模板记录
     * @param template
     * @return
     */
    int insertTempalte(Template template);

    /**
     * 更新整个模板
     * @param template
     * @return
     */
    int updateTemplate(Template template);

    /**
     * 更新模板的删除情况
     * @param template
     * @return
     */
    int updateTemplateIsDeleted(Template template);

    /**
     * 更新模板的参数表
     * @param template
     * @return
     */
    int updateTemplateParamJsonString(Template template);

    /**
     * 通过实验id将实验运行id置null
     * @param experimentId
     * @return
     */
    int updateRunningIdNull(Integer experimentId);

    /**
     * 单例查找一个模板
     * @param templateId
     * @return
     */
    Template selectTemplateById(Integer templateId);

    /**
     * 获取所有模板
     * @param template
     * @return
     */
    List<Template> selectAllTemplate(Template template);

    /**
     * 根据指定关键字进行模糊搜索
     * @param template
     * @return
     */
    List<Template> fuzzySelectAllTemplate(Template template);

    /**
     * 删除一个模板
     * @param templateId
     * @return
     */
    int deleteTemplateById(Integer templateId);




}