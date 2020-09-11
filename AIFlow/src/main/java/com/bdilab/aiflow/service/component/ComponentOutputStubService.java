package com.bdilab.aiflow.service.component;

public interface ComponentOutputStubService {

    /**
     * 删除组件输出ByRunningId
     * @param runningId
     */
    boolean deleteOutputByRunningId(Integer runningId) throws Exception;
}
