package com.bdilab.aiflow.service.run.impl;

import com.bdilab.aiflow.model.run.ApiParameter;
import com.bdilab.aiflow.model.run.ApiPipelineSpec;
import com.bdilab.aiflow.model.run.ApiRun;
import com.bdilab.aiflow.service.run.RunService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author smile
 * @data 2020/9/21 10:14
 **/
@Service
public class RunServiceImpl implements RunService {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public boolean pushData(String processInstanceId,String taskId,String conversationId,String resultTable,String resultPath){

        return true;
    }

    @Override
    public String createRun(String pipelineId, String pipelineName, Map<String,Object> parameter) {
        ApiRun apiRun = new ApiRun();
        ApiPipelineSpec apiPipelineSpec = new ApiPipelineSpec();
        int paramLength = parameter.size();
        Object[] parameters = new Object[paramLength];
        int i = 0;
        Gson configGson = new Gson();
        for(Map.Entry<String,Object> entry:parameter.entrySet()){
            ApiParameter apiParameter = new ApiParameter();
            /*if(entry.getKey().equals("config")){
                System.out.println(entry.getValue().toString());
                Map configMap =  configGson.fromJson(entry.getValue().toString(), Map.class);
                apiParameter.setName(entry.getKey());
                apiParameter.setValue(configMap.toString());
                System.out.println("config");
            }else{
                apiParameter.setName(entry.getKey());
                apiParameter.setValue(entry.getValue().toString());
            }*/
            apiParameter.setName(entry.getKey());
            apiParameter.setValue(entry.getValue().toString());
            parameters[i] = apiParameter;
            i++;
        }

        //pipelineId=a33595a6-e1db-4bc8-90ee-e3ad0de16d2d
        //pipelineName=helloworldTest

        //pipelineId=df51c437-301f-4fbd-9623-229e8967f50f
        //pipelineName=?????
        apiRun.setName("run");
        apiRun.setDescription("desc");
        apiPipelineSpec.setPipelineId(pipelineId);
        apiPipelineSpec.setPipelineName(pipelineName);
        apiPipelineSpec.setParameters(parameters);
        apiRun.setPipeline_spec(apiPipelineSpec);

        Gson gson = new Gson();
        String json = gson.toJson(apiRun);
        System.out.println(json);

        String url = "http://120.27.69.55:31380/pipeline/apis/v1beta1/runs";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ApiRun> request = new HttpEntity<>(apiRun,headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url,request,String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue == 200){
            return responseEntity.getBody();
        }
        return null;
    }
}
