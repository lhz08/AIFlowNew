package com.bdilab.aiflow.service.pipeline.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.common.utils.JsonUtils;
import com.bdilab.aiflow.common.utils.RunCommand;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.ComponentInfoMapper;
import com.bdilab.aiflow.mapper.ComponentParameterMapper;
import com.bdilab.aiflow.mapper.WorkflowMapper;
import com.bdilab.aiflow.model.ComponentParameter;
import com.bdilab.aiflow.model.PythonParameters;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.pipeline.PipelineService;
import com.google.gson.Gson;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;


@Service
public class PipelineServiceImpl implements PipelineService {

    @Autowired
    RestTemplate restTemplate;

    @Resource
    ComponentInfoMapper componentInfoMapper;
    @Resource
    ComponentParameterMapper componentParameterMapper;
    @Resource
    FilePathConfig filePathConfig;
    @Resource
    WorkflowMapper workflowMapper;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private int getComponentId(String string){
        return Integer.parseInt(string.split("_")[1]);
    }

    //生成每个组件的代码，组件id为xml中的组件id
    private String executeTask(List<String> queue,String json,String pipeline){
        String id = queue.get(0);
        //拿到当前结点的后置结点和前置结点list，拼接当前结点的python代码，将它的后置结点数组添加到待执行的队列中，得到它的所有前置节点的输出，作为当前结点的输入。
        List<String> curRearNodeList = JsonUtils.getRearNodeList(id,json);
        List<String> curPriorNodeList = JsonUtils.getPriorNodeList(id,json);
        int componentId = getComponentId(id);
        String componentName = componentInfoMapper.selectComponentInfoById(componentId).getName();
        pipeline+="    _"+componentName+"_op"+"="+componentName+"_op(\n";
        //判断组件输入桩中有几个参数，从前置节点中读出输出，作为当前节点的输入。
        List<String> inputStubList = getStubList(componentInfoMapper.selectComponentInfoById(componentId).getInputStub());//得到当前执行节点的输入列表
        if(curPriorNodeList.size()!=0)
        {
            Integer curPriorNodeComponentId = getComponentId(curPriorNodeList.get(0));
            List<String> curPriorNodeOutputStubList = getStubList(componentInfoMapper.selectComponentInfoById(curPriorNodeComponentId).getOutputStub());
            if(curPriorNodeList.size()==1) {
                for (int i=0;i<inputStubList.size();i++)
                {
                    pipeline+="        "+inputStubList.get(i)+"=_"+getComponentName(curPriorNodeList.get(0))+"_op.outputs"+"['"+curPriorNodeOutputStubList.get(i)+"'],\n";
                }
                List<ComponentParameter> componentParameters = componentParameterMapper.selectComponentParameterByComponentId(componentId);
                for(int i=0;i<componentParameters.size();i++){
                    pipeline+="        "+componentParameters.get(i).getName()+"="+componentName+"_"+componentParameters.get(i).getName()+",\n";
                }
                pipeline+="        config"+"=config\n    "+")"+".after(_"+getComponentName(curPriorNodeList.get(0))+"_op)"+".set_display_name('"+componentName+"')\n\n";

            }
            else {
                pipeline += "        " + inputStubList.get(0) + "=_" + getComponentName(curPriorNodeList.get(0)) + "_op.outputs" + "['"+getStubList(componentInfoMapper.selectComponentInfoById(getComponentId(curPriorNodeList.get(0))).getOutputStub()).get(1) + "'],\n";
                pipeline += "        " + inputStubList.get(1) + "=_" + getComponentName(curPriorNodeList.get(1)) + "_op.outputs" + "['"+getStubList(componentInfoMapper.selectComponentInfoById(getComponentId(curPriorNodeList.get(1))).getOutputStub()).get(0) + "'],\n";
                pipeline+="        config"+"=config\n    "+")";
                for(int i=0;i<curPriorNodeList.size();i++){
                    pipeline+=".after(_"+getComponentName(curPriorNodeList.get(i))+"_op)";
                }
                pipeline+=".set_display_name('"+componentName+"')\n\n";

            }
        }
        else{
            List<ComponentParameter> componentParameters = componentParameterMapper.selectComponentParameterByComponentId(componentId);
            for(int i=0;i<componentParameters.size();i++){
                pipeline+="        "+componentParameters.get(i).getName()+"="+componentName+"_"+componentParameters.get(i).getName()+",\n";
            }
            pipeline+="        config"+"=config\n    )";
            pipeline+=".set_display_name('"+componentName+"')\n\n";
        }
        queue.remove(id);
        for(int i = 0;i<curRearNodeList.size();i++){
            if(!(queue.toString().contains(curRearNodeList.get(i)))){
                queue.add(curRearNodeList.get(i));
            }
        }


        if(queue.size()==0)
            pipeline+="    dsl.get_pipeline_conf().set_image_pull_secrets([k8s_client.V1ObjectReference(name=\"aiflow\")])\n\n\n"+"if __name__ == '__main__':\n" +
                    "    kfp.compiler.Compiler().compile(test_pipeline, __file__ + '.yaml')\n";
        return pipeline;
    }
    //生成头文件和加载组件yaml文件,组件id为数据库中的组件id
    public String generateCode(String json){
        String componentName="";
        Integer componentId;
        List<String> componentIdList = JsonUtils.getToBeExecutedComponentQueue(json);
        String pipeline="import kfp\n" +
                "from kfp import components\n" +
                "from kfp import dsl\n" +
                "from kubernetes import client as k8s_client\n\n\n";
        for (String s:componentIdList
        ) {
            componentId = getComponentId(s);
            componentName = componentInfoMapper.selectComponentInfoById(componentId).getName();
            pipeline+=componentName+"_op"+"="+"components.load_component_from_file"+"('"+filePathConfig.getComponentYamlPath()+File.separatorChar+componentName+".yaml')\n";
        }
        pipeline+="\n\n@dsl.pipeline(\n" +
                "    name='test',\n" +
                "    description='split data test pipeline.'\n" +
                ")\n" +
                "def test_pipeline(\n";
        //名称和描述根据前端传过来的参数进行修改,此处传数据集地址以及每个组件的参数，每个组件的输入由上游组件确定。
        String componentParams="";
        for (String s:componentIdList
        ) {
            componentId=getComponentId(s);
            List<ComponentParameter> componentParameters = componentParameterMapper.selectComponentParameterByComponentId(componentId);
            Map<String, String> paramsByComponentId = JsonUtils.getParamsByComponentId(json, s);
            for (ComponentParameter param:componentParameters
            ) {
                String name = componentInfoMapper.selectComponentInfoById(componentId).getName();
                componentParams +="        "+name+"_"+param.getName()+",\n";
            }
        }
        pipeline+=componentParams+"        config,\n):\n\n";
        return pipeline;
    }

    @Override
    public Map generatePipeline(String workflowXmlAddr, Integer userId){
        List<String> queue = new ArrayList<>();
        Map<String,String> data = new HashMap<>();
        Gson gson = new Gson();
        Map<String, PythonParameters> pythonParametersMap = XmlUtils.getPythonParametersMap(workflowXmlAddr);
        String json = gson.toJson(pythonParametersMap);
        String pipeline=generateCode(json);
        queue.add(JsonUtils.getFirstToBeExecutedComponent(json));
        while(queue.size()!=0){
            pipeline = executeTask(queue,json,pipeline);
        }
        String filePath = filePathConfig.getPipelineCodePath()+ File.separatorChar+ UUID.randomUUID()+".py";
        File file = new File(filePath);
        try {
            if(!file.exists())
            {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bufferedWriter.write(pipeline);
            bufferedWriter.close();
            RunCommand.exeCmd("python "+filePath);
            if(filePath.contains(".py")) {
                String pipelineYamlAddr = filePath+".yaml";
                File file1  =  new File(pipelineYamlAddr);
                if(!file1.exists())
                    throw new IOException("编译失败");

                data.put("pipelineYamlAddr",pipelineYamlAddr);
                data.put("generatePipelineAddr",filePath);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        queue.clear();
        return data;
    }
    //得到输入桩或输出桩的参数列表
    private List<String> getStubList(String inputStub){
        List<String> inputStubList = new ArrayList<>();
        inputStub=inputStub.substring(1,inputStub.length()-1);
        String[] s = inputStub.split(",");
        for (String s1:s
        ) {
            String[] s2 = s1.split(":");
            inputStubList.add(s2[0]);
        }
        return inputStubList;
    }
    //根据前端xml中的组件id获得组件名
    private String getComponentName(String componentId){
        String componentName = componentInfoMapper.selectComponentInfoById(getComponentId(componentId)).getName();
        return componentName;
    }
    //得到组件的输入或者输出桩大小
    private int getInputStubSize(String componentId){
        int stubSize = getStubList(componentInfoMapper.selectComponentInfoById(getComponentId(componentId)).getInputStub()).size();
        return stubSize;
    }
    private int getOutputStubSize(String componentId){
        int stubSize = getStubList(componentInfoMapper.selectComponentInfoById(getComponentId(componentId)).getOutputStub()).size();
        return stubSize;
    }


    @Override
    public String uploadPipeline(String name, String description, File file) {

        System.out.println(file.getAbsoluteFile());
        String url = "http://120.27.69.55:31380/pipeline/apis/v1beta1/pipelines/upload?name=" + name + "&description=" + description;
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        FileSystemResource fileSystemResource = null;

        //将File类型转换成API需要的MultipartFile类型
        /*try {
            FileInputStream fileInputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(),file.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);
            fileSystemResource = new FileSystemResource(FileUtils.transferToFile(multipartFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

*/
        fileSystemResource = new FileSystemResource(file);
        map.add("uploadfile",fileSystemResource);

        HttpEntity<MultiValueMap<String, Object>> params = new HttpEntity<>(map);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, params, String.class);

        if(responseEntity.getStatusCodeValue() != 200){
            return null;
        }
        Gson gson = new Gson();
        Map<String,String> map1 = gson.fromJson(responseEntity.getBody(),Map.class);
        String pipelineId = map1.get("id");
        return pipelineId;
    }

    @Override
    public String getPipelineById(String pipelineId) {
        //b5e588e3-062e-4e9c-b1c6-eddabea88c89
        String url = "http://120.27.69.55:31380/pipeline/apis/v1beta1/pipelines/" + pipelineId;
        Map<String, Object> paramMap = new HashMap<>();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url,String.class,paramMap);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        System.out.println("statusCodeValue = " + statusCodeValue);
        if(statusCodeValue == 200){
            return responseEntity.getBody();
        }else {
            return null;
        }
    }

    @Override
    public boolean deletePipelineById(String pipelineId) {
        String url = "http://120.27.69.55:31380/pipeline/apis/v1beta1/pipelines/" + pipelineId;
        restTemplate.delete(url);
        return true;
    }

    public static void main(String[] args) {
        File file = new File("E:\\home\\pipelineCode\\f1f88534-6a05-41de-bdfc-7907565e5f6e.py.yaml");
        System.out.println(file.getName());
        PipelineServiceImpl pipelineService = new PipelineServiceImpl();
        pipelineService.uploadPipeline("1asf","asfas",file);
    }
}
