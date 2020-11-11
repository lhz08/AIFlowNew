package com.bdilab.aiflow.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.bdilab.aiflow.model.ProcessNode;
import com.bdilab.aiflow.model.PythonParameters;
import com.google.gson.Gson;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

/**
 * @Decription XML工具类
 * @Author Humphrey
 * @Date 2019/9/9 19:54
 * @Version 1.0
 **/
public class XmlUtils {
    /**
     * 解析获得processId
     * @param fileName
     * @return
     */
    public static String parseProcessId(String fileName){
        SAXReader reader = new SAXReader();
        String processId = null;
        File file = new File(fileName);
        try {
            org.dom4j.Document document = reader.read(file);
            org.dom4j.Element root = document.getRootElement();
            processId=root.element("process").attribute( "id" ).getValue();
        }catch (DocumentException e){
            e.printStackTrace();
        }
        return processId;
    }

    /**
     * 解析xml流程文件，判断流程是否使用了model，返回使用的model的id列表
     * @param fileName
     * @return
     */
    public static List<Integer> parseModelIds(String fileName){
        SAXReader reader = new SAXReader();
        List<Integer> modelIds = new ArrayList<>();
        File file = new File(fileName);
        try {
            org.dom4j.Document document = reader.read(file);
            org.dom4j.Element root = document.getRootElement();
            org.dom4j.Element element = root.element("process");
            Iterator<Element> iterator = element.elementIterator();
            while (iterator.hasNext()){
                org.dom4j.Element e = iterator.next();
                if (e.getName().equals( "userTask")){
                    if(e.attributeValue("name").equals("importModel")){
                        org.dom4j.Element childElement = e.element("documentation");
                        if(childElement!=null) {
                            //value的格式形为： numOfNeighbors=5 weightsOfNeighbors=uniform algorithm=auto leafSize=30 p=2
                            String value = childElement.getText();
                            //用空格分割出每一个键值对
                            String[] variables = value.split(" ");
                            for (int i = 0; i < variables.length; i++) {
                                //用等号分割出属性和值
                                String[] variablePair = variables[i].split("=");
                                String variableKey = variablePair[0];
                                if (variableKey.equals("modelId")) {
                                    if (variablePair.length == 2) {
                                        Integer modelId = Integer.parseInt(variablePair[1]);
                                        modelIds.add(modelId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return modelIds;
    }

    /**
     * 取出BPMN文件中每个UserTask的参数
     * @return
     */
    public static LinkedHashMap<String, LinkedHashMap<String,String>> parseVariables(String filename){
        SAXReader reader = new SAXReader();
        LinkedHashMap<String,LinkedHashMap<String,String>> taskVariablesMap = new LinkedHashMap<String,LinkedHashMap<String,String>>();
        File file = new File(filename);
        try {
            org.dom4j.Document document = reader.read(file);
            org.dom4j.Element root = document.getRootElement();
            org.dom4j.Element element = root.element("process");
            Iterator<Element> iterator = element.elementIterator();
            while (iterator.hasNext()){
                org.dom4j.Element e = iterator.next();
                if (e.getName().equals( "userTask")){
                    LinkedHashMap<String,String> taskVariables = new LinkedHashMap<>();
                    org.dom4j.Element childElement = e.element("documentation");
                    if(childElement!=null)
                    {
                        //value的格式形为： numOfNeighbors=5 weightsOfNeighbors=uniform algorithm=auto leafSize=30 p=2
                        String value=childElement.getText();
                        //用空格分割出每一个键值对
                        String [] variables = value.split(" " );
                        for(int i=0;i<variables.length;i++) {
                            //用等号分割出属性和值
                            String[] variablePair = variables[i].split("=" );
                            String variableKey = variablePair[0];
                            String variableValue;
                            if(variablePair.length==2){
                                variableValue = variablePair[1];
                            }
                            else{
                                variableValue = null;
                            }
                            //组装入map
                            taskVariables.put(variableKey,variableValue);
                        }
                    }
                    taskVariablesMap.put( e.attributeValue("id"),taskVariables);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return taskVariablesMap;
    }

    public static boolean changeDatasetFileAddr(String filePath,String datasetAddr){
        SAXReader reader = new SAXReader();
        File file = new File(filePath);
        try {
            org.dom4j.Document document = reader.read(file);
            org.dom4j.Element root = document.getRootElement();
            org.dom4j.Element element = root.element("process");
            Iterator<Element> iterator = element.elementIterator();
            while (iterator.hasNext()){
                org.dom4j.Element e = iterator.next();
                if (e.attribute("name").getValue().equals("dataImport")){
                    org.dom4j.Element childElement = e.element("documentation");
                    childElement.setText(" addr="+datasetAddr+" ");
                    try {
                        saveDocument(document,file);
                        return true;
                    }catch (IOException io){
                        io.printStackTrace();
                        return false;
                    }
                }
            }
        }catch (DocumentException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean judgeContainsModel(String filePath){
        SAXReader reader = new SAXReader();
        File file = new File(filePath);
        if(!file.exists()){
            return false;
        }
        try {
            org.dom4j.Document document = reader.read(file);
            org.dom4j.Element root = document.getRootElement();
            org.dom4j.Element element = root.element("process");
            Iterator<Element> iterator = element.elementIterator();
            while (iterator.hasNext()){
                org.dom4j.Element e = iterator.next();
                if (e.attribute("name").getValue().equals("importModel")) {
                    return true;
                }
            }
        }catch (Exception de){
            de.printStackTrace();
        }
        return false;
    }


    public static void saveDocument(Document document, File xmlFile) throws IOException {
        Writer osWrite = new OutputStreamWriter(new FileOutputStream(xmlFile));// 创建输出流
        OutputFormat format = OutputFormat.createPrettyPrint(); // 获取输出的指定格式
        format.setEncoding("UTF-8");// 设置编码 ，确保解析的xml为UTF-8格式
        XMLWriter writer = new XMLWriter(osWrite, format);// XMLWriter
        // 指定输出文件以及格式
        writer.write(document);// 把document写入xmlFile指定的文件(可以为被解析的文件或者新创建的文件)
        writer.flush();
        writer.close();
    }

    //
    public static Map<String, ProcessNode> parseProcessNodes(String xmlFilePath){
        SAXReader reader = new SAXReader();
        Map<String,ProcessNode> processNodeMap = new LinkedHashMap<>();

        File file = new File(xmlFilePath);
        try {
            org.dom4j.Document document = reader.read(file);
            org.dom4j.Element root = document.getRootElement();
            org.dom4j.Element element = root.element("process");
            Iterator<Element> iterator = element.elementIterator();
            while (iterator.hasNext()){
                org.dom4j.Element e = iterator.next();
                //添加task到nodeMap中
                if(e.getName().equals("userTask")){
                    ProcessNode processNode =new ProcessNode();
                    processNode.setNodeId(e.attributeValue("id"));
                    processNode.setNodeName(e.attributeValue("name"));
                    processNode.setNodeType(1);
                    processNodeMap.put(processNode.getNodeId(),processNode);
                }
                if(e.getName().equals("parallelGateway")){
                    ProcessNode processNode =new ProcessNode();
                    processNode.setNodeId(e.attributeValue("id"));
                    processNode.setNodeName(e.attributeValue("name"));
                    processNode.setNodeType(0);
                    processNodeMap.put(processNode.getNodeId(),processNode);
                }
                if (e.getName().equals( "sequenceFlow")){
                    //遍历processNodeList，设置节点间的关系
                    //若a节点的id与sequenceFlow的source相同，b节点的id与sequenceFlow的target相同，则a是b的前驱,b是a的后继
                    processNodeMap.forEach((taskIdA,processNodeA)->{
                        if(taskIdA.equals(e.attributeValue("sourceRef"))){
                            processNodeMap.forEach((taskIdB,processNodeB)->{
                                if(taskIdB.equals(e.attributeValue("targetRef"))){
                                    //将a添加到b的前驱节点list中
                                    processNodeB.addPriorNode(processNodeA);
                                    //将b添加到a的后继节点list中
                                    processNodeA.addRearNode(processNodeB);
                                }
                            });
                        }
                    });
                }
            }
        }catch (DocumentException e){
            e.printStackTrace();
        }
        return processNodeMap;
    }

    /**
     * 解析xml，组装成Map<String,PythonParameters>对象，传送给python端解析参数
     * String为xml中userTask定义的id，PythonParameters为定义的python参数模型，根据不同的流程节点，有不同的取值
     * @param filePath
     * @return
     */
    public static Map<String, PythonParameters> getPythonParametersMap(String filePath){
        // 解析获得xml中定义的流程变量
        LinkedHashMap<String, LinkedHashMap<String,String>> taskVariablesMap = parseVariables(filePath);

        //解析获得xml中的节点关系
        Map<String,ProcessNode> processNodeMap = parseProcessNodes(filePath);

        //组装Map<TaskId,PythonParameters>
        Map<String, PythonParameters> pythonParametersMap = new HashMap<>(16);
        taskVariablesMap.forEach((taskId,map)->{
            PythonParameters pythonParameters = new PythonParameters();
            pythonParameters.setModelPath("");
            //设置当前taskID
            pythonParameters.setCurTaskId(taskId);
            //设置解析的变量Map
            pythonParameters.setParameters(map);

            ProcessNode curNode = processNodeMap.get(taskId);
            //前置节点为空时，无需转换
            if(curNode.getPriorNode()==null){
                //设置前置节点ID
                pythonParameters.setPriorIds(new ArrayList<>());
            }
            else{
                //将字符串形式的前直接点ID转换为List
                String[] priorIds = curNode.getPriorTaskIds().split(", ");
                List<String> priorIdList = new ArrayList<>();
                for(int i =0;i<priorIds.length;i++){
                    priorIdList.add(priorIds[i]);
                }

                //设置前置节点ID
                pythonParameters.setPriorIds(priorIdList);
            }

            //若无后置节点，设置isEnd为true
            if(curNode.getRearNode()==null){
                pythonParameters.setIsEnd(true);
                pythonParameters.setRearIds(new ArrayList<>());
            }
            //否则为false
            else{
                pythonParameters.setIsEnd(false);
                String[] rearIds = curNode.getRearTaskIds().split(", ");
                List<String> rearIdsList = new ArrayList<>();
                for(int i =0;i<rearIds.length;i++){
                    rearIdsList.add(rearIds[i]);
                }
                pythonParameters.setRearIds(rearIdsList);
            }
            pythonParameters.setProcessInstanceId("");
            //将pythonParameters添加到map中
            //processInstanceId+"-"+
            pythonParametersMap.put(taskId,pythonParameters);
        });
        return pythonParametersMap;
    }

    public static String generateXmlFile(String xml,String filePath){
        //生成xml文件
        File file = new File(filePath);
        //若当前日期文件夹不存在，创建该文件夹
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
            out.write(xml);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static void main(String args[]){
//        Gson gson = new Gson();
//
//        EpochInfo epochInfo = new EpochInfo();
//        Map<String,String> result = new HashMap<>();
//        result.put("image","/home/data/train1/36.jpg");
//        result.put("text","");
//        result.put("audio","");
//        epochInfo.setResult(result);
//        Map<String,String> originUrl = epochInfo.getResult();
//        for(String string:originUrl.keySet()){
//            System.out.println("value size: "+originUrl.get(string).length());
//            if(originUrl.get(string).length()>0){
//                System.out.println("change file path to url");
//                String newUrl = originUrl.get(string).replace("/home/data/","http://114.116.237.0"+":"+3000+"/dataset_file/");
//                originUrl.replace(string,newUrl);
//            }
//        }
//        epochInfo.setResult(originUrl);
//        System.out.println(epochInfo.getResult());
        //changeDatasetFileAddr("C:\\Users\\dell\\Desktop\\03c7c6c7-f38c-4520-8160-33c25a53b570.xml","any");
        //parseVariables("C:\\Users\\dell\\Desktop\\03c7c6c7-f38c-4520-8160-33c25a53b570.xml");
        Gson gson = new Gson();
        Map<String,ProcessNode> map = parseProcessNodes("C:\\Users\\cuishaohui\\Desktop\\fde8116f-cce0-4e7f-847e-6b2722e0a753.xml");
        Map<String,PythonParameters> map1 = getPythonParametersMap("E:\\home\\workflowXml\\20200926\\3ef66732-b3c1-4dd9-b095-79524361a3ae.xml");
        System.out.println(gson.toJson(map1));
//        JSONObject jsonObject = JSONObject.parseObject(gson.toJson(map1), Feature.OrderedField);
//        for (String key:jsonObject.keySet()
//             ) {
//            String priorIds = jsonObject.getJSONObject(key).getString("priorIds");
//            System.out.println(key+"_"+priorIds);
//        }
    }
}
