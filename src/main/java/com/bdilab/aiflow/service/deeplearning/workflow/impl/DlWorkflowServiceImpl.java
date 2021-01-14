package com.bdilab.aiflow.service.deeplearning.workflow.impl;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.utils.JsonUtils;
import com.bdilab.aiflow.common.utils.RunCommand;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.ComponentInfoMapper;
import com.bdilab.aiflow.mapper.ComponentParameterMapper;
import com.bdilab.aiflow.model.ComponentInfo;
import com.bdilab.aiflow.model.ComponentParameter;
import com.bdilab.aiflow.model.PythonParameters;
import com.bdilab.aiflow.service.deeplearning.workflow.DlWorkflowService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * @author smile
 * @data 2021/1/6 15:38
 **/
@Service
public class DlWorkflowServiceImpl implements DlWorkflowService {

    @Autowired
    ComponentInfoMapper componentInfoMapper;
    @Autowired
    ComponentParameterMapper componentParameterMapper;
    @Autowired
    FilePathConfig filePathConfig;

    @Override
    public Map generateDLPipeline(String workflowXmlAddr,Integer userId){
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
        String filePath = filePathConfig.getPipelineCodePath()+ File.separatorChar+ UUID.randomUUID()+".py";
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
            for (String input:inputStub
                 ) {
                if(curPriorNodeList.size()!=0) {
                    pipeline += input + ",";
                }
                inputFile +="                '--"+input+"',"+input+",\n";
            }
            List<ComponentParameter> componentParameters = componentParameterMapper.selectComponentParameterByComponentId(componentId);
            for (ComponentParameter componentParameter:componentParameters
                 ) {
                pipeline+=componentParameter.getName()+",";
                inputParam+="                '--"+componentParameter.getName()+"',"+componentParameter.getName()+",\n";
            }
            if(curPriorNodeList.size()==0)
                inputParam="";
            pipeline+="config):\n"+"        super("+componentName+"Op, self).__init__(\n"+
                    "            name='"+componentName+"',\n"+
                    "            image='"+image+"',\n"+
                    "            arguments=[\n"+inputFile+
                    inputParam+"\n"+"                '--config', config,\n"+
                    "            ],\n"+
                    "            file_outputs={\n"+
                    "                'output':"+ "'/"+componentName+".txt'\n"+
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
        pipeline+=componentParams+"        config,\n):\n\n";
        return pipeline;
    }
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
        String componentName = componentInfoMapper.selectComponentInfoById(componentId).getName();
        pipeline+="    "+componentName+" = "+componentName+"Op(data_dir,";
        Integer curPriorNodeComponentId=null;
        List<String> stubList=null;
        String outputStub=null;
        ComponentInfo curPriorNodeComponent=null;
        List<ComponentParameter> componentParameterList = null;
        if(curPriorNodeList.size()!=0) {
            if (curPriorNodeList.size() == 1) {
                curPriorNodeComponentId = getComponentId(curPriorNodeList.get(0));
                curPriorNodeComponent = componentInfoMapper.selectComponentInfoById(curPriorNodeComponentId);
                outputStub = curPriorNodeComponent.getOutputStub();
                stubList = getStubList(outputStub);
                pipeline += curPriorNodeComponent.getName() + ".outputs['output'],";
            }
            //如果当前节点有多个前置结点
            else {
                for (String curPriorNode :
                        curPriorNodeList) {
                    curPriorNodeComponentId = getComponentId(curPriorNode);
                    curPriorNodeComponent = componentInfoMapper.selectComponentInfoById(curPriorNodeComponentId);
                    outputStub = curPriorNodeComponent.getOutputStub();
                    stubList = getStubList(outputStub);
                    pipeline += curPriorNodeComponent.getName() + ".outputs['output'],";
                }
            }
        }
        else {

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
}
