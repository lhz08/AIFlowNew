package com.bdilab.aiflow.service.template;

import com.bdilab.aiflow.model.Experiment;
import com.bdilab.aiflow.model.Template;
import com.bdilab.aiflow.model.Workflow;

import java.util.Map;

public interface TemplateService {

    /**
     * 将实验标记为模板
     * @param experimentId
     * @param templateName
     * @param tags
     * @param templateDesc
     * @param userId
     * @return
     */
    boolean markExperimentToTemplate(Integer experimentId, String templateName, String tags, String templateDesc, Integer userId);

    /**
     * 从模板创建实验
     * @param templateId
     * @param experimentName
     * @param experimentDesc
     * @return
     */
    Experiment createExperimentFromTemplate(Integer templateId, String experimentName, String experimentDesc);

    /**
     * 修改模板的名称、描述和标签
     * @param templateId
     * @param tamplateName
     * @param templateTags
     * @param templateDesc
     * @return
     */
    boolean updateTemplate(Integer templateId, String tamplateName, String templateTags, String templateDesc);

    /**
     * 单例查找模板
     * @param templateId
     * @return
     */
    Template selectTemplateById(Integer templateId);

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
