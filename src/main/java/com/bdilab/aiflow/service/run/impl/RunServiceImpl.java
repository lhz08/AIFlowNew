package com.bdilab.aiflow.service.run.impl;

import com.bdilab.aiflow.service.run.RunService;
import org.springframework.stereotype.Service;

/**
 * @author smile
 * @data 2020/9/21 10:14
 **/
@Service
public class RunServiceImpl implements RunService {


    @Override
    public boolean pushData(String processInstanceId,String taskId,String conversationId,String resultTable,String resultPath){




        return true;
    }

}
