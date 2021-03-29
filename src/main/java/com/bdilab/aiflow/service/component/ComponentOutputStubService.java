package com.bdilab.aiflow.service.component;

import com.bdilab.aiflow.model.ComponentOutputStub;

import java.util.List;
import java.util.Map;

public interface ComponentOutputStubService {

    /**
     * 通过实验运行id和组件id得到结果地址，如果有type指定，则会精确搜索
     * @param runningId
     * @param fkComponentId
     * @param graphType
     * @return
     */
    Map<String,Object> getOutputFileAddr(Integer runningId,Integer fkComponentId,Integer graphType);

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
