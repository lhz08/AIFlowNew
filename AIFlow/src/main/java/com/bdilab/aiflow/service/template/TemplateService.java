package com.bdilab.aiflow.service.template;

import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.model.Workflow;

import java.util.Map;

public interface TemplateService {

    /**
     * 向表中插入新模板
     * @param template
     */
    Template createTemplate(Template template);

    /**
     * 从模板创建实验
     * @param template
     * @param userId
     * @param workflowName
     * @param workflowTags
     * @param workflowDesc
     * @param experimentName
     * @param experimentDesc
     * @return
     */
    Map<String,Object> createExperiment(Template template, Integer userId, String workflowName, String workflowTags, String workflowDesc, String experimentName, String experimentDesc);

    /**
     * 单例查找模板
     * @param templateId
     * @return
     */
    Template selectTemplateById(Integer templateId);

    /**
     * 更新模板的参数表
     * @param template
     * @return
     */
    boolean updateTemplateParamJsonString(Template template);

    /**
     * 将模板的实验运行外键置NULL
     * @param experimentId
     */
    boolean setRunningIdNull(Integer experimentId);

    /**
     * 删除模板
     * @param template
     * @return
     */
    boolean deleteTemplate(Template template);

    /**
     * 查找符合条件的所有模板
     * @param template
     * @param pageNum
     * @param pageSize
     * @return
     */
    Map<String,Object> selectAllTemplate(Template template, int pageNum, int pageSize);

    /**
     * 模糊搜索所有模板
     * @param template
     * @param pageNum
     * @param pageSize
     * @return
     */
    Map<String,Object> fuzzySelectAllTemplate(Template template, int pageNum, int pageSize);

    /**
     * 还原一个在回收站中的模板
     * @param templateId
     * @return
     */
    boolean restoreTemplate(Integer templateId);






}
