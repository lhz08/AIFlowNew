package com.bdilab.aiflow.service.component;

import com.bdilab.aiflow.model.ComponentOutputStub;

import java.util.List;
import java.util.Map;

public interface ComponentOutputStubService {

    /**
     * 删除组件输出ByRunningId
     * @param runningId
     */
    boolean deleteOutputByRunningId(Integer runningId) throws Exception;

    /**
     * 获取组件运行的结果
     *
     */
    List<ComponentOutputStub> getComponentResult(Integer runningId, Integer componentId);

    Map<String,Object> previewResult(Integer componentOutputStubId,Integer userId);
}
