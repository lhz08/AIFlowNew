package com.bdilab.aiflow.service.pipeline.impl;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.utils.DateUtils;
import com.bdilab.aiflow.common.utils.JsonUtils;
import com.bdilab.aiflow.common.utils.RunCommand;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.ComponentInfoMapper;
import com.bdilab.aiflow.mapper.ComponentParameterMapper;
import com.bdilab.aiflow.mapper.WorkflowMapper;
import com.bdilab.aiflow.model.ComponentInfo;
import com.bdilab.aiflow.model.ComponentParameter;
import com.bdilab.aiflow.model.PythonParameters;
import com.bdilab.aiflow.service.pipeline.PipelineService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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


    //编译生成yaml
    @Override
    public Map generatePipeline(String workflowXmlAddr,Integer userId){
        List<String> toBeExecutedQueue = new ArrayList<>();
        List<String> completedQueue = new ArrayList<>();
        Map<String,String> data = new HashMap<>();
        Gson gson = new Gson();
        Map<String, PythonParameters> pythonParametersMap = XmlUtils.getPythonParametersMap(workflowXmlAddr);
        String json = gson.toJson(pythonParametersMap);
        String pipeline = generateCode(json);
        toBeExecutedQueue.add(JsonUtils.getFirstToBeExecutedComponent(json));
        while(!toBeExecutedQueue.isEmpty()){
            pipeline = executeTask(toBeExecutedQueue,json,pipeline,completedQueue);
        }
        System.out.println(pipeline);
        String filePath = filePathConfig.getPipelineCodePath()+ File.separatorChar + DateUtils.getCurrentDate()+ File.separatorChar+ UUID.randomUUID()+".py";
        File file = new File(filePath);
        try {
            if(!file.exists()){
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
        toBeExecutedQueue.clear();
        return data;
    }
    //生成组件的头文件和描述类
    //生成头文件和加载组件yaml文件,组件id为数据库中的组件id
    private String generateCode(String workflowJson){
        String componentName="";
        String image="";
        List<String> inputStub= null;
        List<String> outputStub=null;
        String inputParam = "";
        String inputFile = "";
        List<String> curPriorNodeList = null;
        String outputParam = null;
        Integer componentId;
        ComponentInfo componentInfo=null;

        List<String> componentIdList = JsonUtils.getToBeExecutedComponentQueue(workflowJson);
        String pipeline="import kfp\n" +
                "from kfp import components\n" +
                "from kfp import dsl\n" +
                "from kubernetes import client as k8s_client\n\n\n";
        for (String s:componentIdList
        ) {
            componentId = getComponentId(s);
            componentInfo=componentInfoMapper.selectComponentInfoById(componentId);
            componentName = componentInfo.getName();
            image=componentInfo.getComponentImage();
            inputStub = getStubList(componentInfo.getInputStub());
            outputStub = getStubList(componentInfo.getOutputStub());
            curPriorNodeList = JsonUtils.getPriorNodeList(s,workflowJson);
            pipeline+="class "+componentName+"Op(dsl.ContainerOp):\n\n"+"    def __init__(self,data_dir,";
            if(curPriorNodeList.size()==0){
                inputStub=null;
            }
            if(inputStub!=null) {
                for (String input : inputStub
                ) {
                    pipeline += input + ",";
                    inputFile += "                '--" + input + "'," + input + ",\n";
                }
            }
            List<ComponentParameter> componentParameters = componentParameterMapper.selectComponentParameterByComponentId(componentId);
            for (ComponentParameter componentParameter:componentParameters
            ) {
                pipeline+=componentParameter.getName()+",";
                inputParam+="                '--"+componentParameter.getName()+"',"+componentParameter.getName()+",\n";
            }
            pipeline+="config):\n"+"        super("+componentName+"Op, self).__init__(\n"+
                    "            name='"+componentName+"',\n"+
                    "            image='"+image+"',\n"+
                    "            command=[\n" +
                    "                'python3','"+componentName+".py',\n"+
                    "                '--data_dir', data_dir,\n"+
                    inputFile+
                    inputParam+
                    "\n"+"                '--config', config,\n"+
                    "            ],\n"+
                    "            file_outputs={\n"+
//                    "                '"+outputStub.get(0)+"':"+"data_dir"+ "+'/"+componentName+"/"+outputStub.get(0)+".txt'\n"+
                    "                '"+outputStub.get(0)+"':"+"'/workspace/"+outputStub.get(0)+".txt'\n"+
                    "            })\n\n";
            inputParam="";
            inputFile="";
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
            for (ComponentParameter param:componentParameters
            ) {
                String name = componentInfoMapper.selectComponentInfoById(componentId).getName();
                componentParams +="        "+name+"_"+param.getName()+",\n";
            }
        }
        pipeline+=componentParams+"        config,\n):\n\n"+"    data_dir='"+filePathConfig.getData_dir()+"'\n\n";
        return pipeline;
    }
    /**
     *生成每个任务的python代码时候，维护两个队列，一个是待执行的任务队列，另一个是执行完成的任务队列。
     * 每当生成一个任务的代码时，首先判断该任务的前置任务是否已经全部进入执行完成的任务队列，如果是，则为该任务生成代码，并将它的后置任务加入到
     * 待执行的任务队列，如果不是，则将该任务剔除，由于该任务还存在前置任务，所以该任务一定会被执行到。
     */
    //生成每个组件的代码，当前id为xml中的组件id
    //生成每个组件的pipeline代码
    private String executeTask(List<String> toBeExecutedQueue,String workflowXmlJson,String pipeline,List<String> completedQueue){
        String id = toBeExecutedQueue.get(0);
        if(!priorIdAllcompleted(id,completedQueue,workflowXmlJson)){
            toBeExecutedQueue.remove(id);
            return pipeline;
        }
        List<String> curRearNodeList = JsonUtils.getRearNodeList(id,workflowXmlJson);
        List<String> curPriorNodeList = JsonUtils.getPriorNodeList(id,workflowXmlJson);
        int componentId = getComponentId(id);
        ComponentInfo componentInfo = componentInfoMapper.selectComponentInfoById(componentId);
        String componentName = componentInfo.getName();
        pipeline+="    "+componentName+" = "+componentName+"Op(data_dir,";
        Integer curPriorNodeComponentId=null;
        List<String> stubList=null;
        String outputStub=null;
        ComponentInfo curPriorNodeComponent=null;
        List<ComponentParameter> componentParameterList = null;
        for (String curPriorNode :
                curPriorNodeList) {
            curPriorNodeComponentId = getComponentId(curPriorNode);
            curPriorNodeComponent = componentInfoMapper.selectComponentInfoById(curPriorNodeComponentId);
            outputStub = curPriorNodeComponent.getOutputStub();
            stubList = getStubList(outputStub);
            pipeline += curPriorNodeComponent.getName() + ".outputs['"+stubList.get(0)+"'],";
        }
        //拼接参数
        componentParameterList = componentParameterMapper.selectComponentParameterByComponentId(componentId);
        for (ComponentParameter componentParameter:componentParameterList) {
            pipeline+=componentName+"_"+componentParameter.getName()+",";
        }

        pipeline+="config)."+ "add_volume(k8s_client.V1Volume(name='aiflow',\n" +
                "                                                                                             nfs=k8s_client.V1NFSVolumeSource(\n" +
                "                                                                                                 path='/nfs/aiflow/',\n" +
                "                                                                                                 server='master'))).add_volume_mount(\n" +
                "        k8s_client.V1VolumeMount(mount_path='/nfs/aiflow/', name='aiflow'))\n\n";
        completedQueue.add(id);
        toBeExecutedQueue.remove(id);
        for(int i = 0;i<curRearNodeList.size();i++){
            if(!toBeExecutedQueue.contains(curRearNodeList.get(i))) {
                toBeExecutedQueue.add(curRearNodeList.get(i));
            }
        }
        if(toBeExecutedQueue.size()==0){
            pipeline+="    dsl.get_pipeline_conf().set_image_pull_secrets([k8s_client.V1ObjectReference(name=\"aiflow\")])\n\n\n"+"if __name__ == '__main__':\n" +
                    "    kfp.compiler.Compiler().compile(test_pipeline, __file__ + '.yaml')\n";
        }
        return pipeline;
    }
    //判断前置节点是否已经生成过代码
    private boolean priorIdAllcompleted(String curNode,List<String> completeQueue,String json){
        List<String> priorNodeList = JsonUtils.getPriorNodeList(curNode, json);
        for (String node:priorNodeList
        ) {
            if(!completeQueue.contains(node)){
                return false;
            }
        }
        return true;
    }

    private int getComponentId(String string){
        return Integer.parseInt(string.split("_")[1]);
    }
    //得到输入桩或输出桩的参数列表
    private List<String> getStubList(String inputStub){
        if(inputStub.equals(""))
            return null;
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
        File file = new File("G:\\home\\pipelineYaml\\2de8934e-4468-46f8-9f71-b6cfaf096783.py.yaml");
        System.out.println(file.getName());
        PipelineServiceImpl pipelineService = new PipelineServiceImpl();
        pipelineService.uploadPipeline("zmtest","zmtest",file);
    }
}
