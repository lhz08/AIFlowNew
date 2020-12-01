package com.bdilab.aiflow.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.bdilab.aiflow.model.PythonParameters;
import com.bdilab.aiflow.service.pipeline.impl.PipelineServiceImpl;
import com.google.gson.Gson;

import java.util.*;

public class JsonUtils {

    //得到每个Id和它的后置结点
    public static Map<String, String> getComponentRear(String str) {
        Map<String, String> componentRear = new LinkedHashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(str, Feature.OrderedField);
        for (String key : jsonObject.keySet()) {
            String rearStr = jsonObject.getJSONObject(key).getString("rearIdS");
            String rearSubstring = rearStr.substring(1, rearStr.length() - 1);
            componentRear.put(key, rearSubstring);
        }
        return componentRear;
    }

    //根据组件id拿到它的后置结点数组
    public static List<String> getRearNodeList(String comonentId, String json) {
        List<String> result = new ArrayList<>();
        Map<String, String> componentRear = getComponentRear(json);
        String[] rearNodeArray = componentRear.get(comonentId).split(",");
        for (String str : rearNodeArray
        ) {
            if (str.length() > 1)
                result.add(str.substring(1, str.length() - 1));
        }
        return result;
    }

    //根据组件Id拿到它的前置结点数组
    public static List<String> getPriorNodeList(String comonentId, String json) {
        List<String> result = new ArrayList<>();
        Map<String, String> componentRear = getComponentPrior(json);
        String[] priorNodeArray = componentRear.get(comonentId).split(",");
        for (String str : priorNodeArray
        ) {
            if (str.length() > 1)
                result.add(str.substring(1, str.length() - 1));
        }
        return result;
    }

    //得到每个组件和它的前置结点
    public static Map<String, String> getComponentPrior(String json) {
        Map<String, String> componentPrior = new LinkedHashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(json, Feature.OrderedField);
        for (String key : jsonObject.keySet()) {
            String priorStr = jsonObject.getJSONObject(key).getString("priorIds");
            String priorSubstring = priorStr.substring(1, priorStr.length() - 1);
            componentPrior.put(key, priorSubstring);
        }
        return componentPrior;
    }


    //解析json得到每个python组件中的变量
    public static Map<String, Map<String, String>> getpythonParams(String json) {
        Map<String, Map<String, String>> result = new HashMap<>();

        JSONObject jsonObject = JSONObject.parseObject(json, Feature.OrderedField);
        for (String key : jsonObject.keySet()) {
            Map<String, String> params = new LinkedHashMap<>();
            if (jsonObject.getJSONObject(key).getJSONObject("parameters") != null) {
                for (String key1 : jsonObject.getJSONObject(key).getJSONObject("parameters").keySet()
                ) {
                    params.put(key1, jsonObject.getJSONObject(key).getJSONObject("parameters").getString(key1));
                }
            }
            result.put(key, params);
        }
        return result;
    }

    //将所有要执行的组件写进队列中
    public static List<String> getToBeExecutedComponentQueue(String json) {
        List<String> result = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(json, Feature.OrderedField);
        for (String key : jsonObject.keySet()
        ) {
            result.add(key);
        }
        return result;
    }

    //根据组件id获取参数
    public static Map<String, String> getParamsByComponentId(String str, String componentId) {
        Map<String, Map<String, String>> componentAndParams = getpythonParams(str);
        Map<String, String> params = componentAndParams.get(componentId);
        return params;
    }

    ///获取map的第一个Key
    public static <K, V> K getFirstKey(Map<K, V> map) {
        K obj = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            obj = entry.getKey();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

    ///获取map的第一个value
    public static <K, V> V getFirstValue(Map<K, V> map) {
        V obj = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

    public static String getFirstToBeExecutedComponent(String json) {
        Map<String, String> componentPrior = new LinkedHashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(json, Feature.OrderedField);
        for (String key:jsonObject.keySet()
             ) {
            if(jsonObject.getJSONObject(key).getString("priorIds").equals("[]"))
                return  key;
        }
        return null;
    }

    public static String getLastToBeExecutedComponent(String json) {
        Map<String, String> componentPrior = new LinkedHashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(json, Feature.OrderedField);
        for (String key:jsonObject.keySet()
        ) {
            if(jsonObject.getJSONObject(key).getString("rearIds").equals("[]"))
                return  key;
        }
        return null;
    }
    //得到组件的执行顺序
    public static  List<String> getComponenetByOrder(String json){
        List<String> queue = new ArrayList<>();
        List<String> result = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(json, Feature.OrderedField);
        for (String key:jsonObject.keySet()
        ) {
            if(jsonObject.getJSONObject(key).getString("priorIds").equals("[]")) {
                queue.add(key);
                result.add(key);
            }
        }
        while(queue.size()!=0)
        {
            List<String> RearNodeList = getRearNodeList(queue.get(0),json);
            for(int i = 0;i<RearNodeList.size();i++){
                if(!(result.toString().contains(RearNodeList.get(i)))){
                    queue.add(RearNodeList.get(i));
                    result.add(RearNodeList.get(i));
                }
            }
            queue.remove(queue.get(0));
        }
        return result;
    }



//    /**
//     * 对于流程里面嵌套的流程组件，将该流程组件的json描述和当前流程的json合并来处理
//     *
//     */
//    public Map<String, PythonParameters> mergeJson(Map<String, PythonParameters> curProcess,String processComponentId,String xmlpath ){
//        Map<String, PythonParameters> processComponent = XmlUtils.getPythonParametersMap(xmlpath);
//        Gson gson = new Gson();
//        String processComponentJson = gson.toJson(processComponent);
//        JSONObject jsonObject = JSONObject.parseObject(processComponentJson, Feature.OrderedField);
//        String firstToBeExecutedComponent = getFirstToBeExecutedComponent(gson.toJson(processComponent));
//        String curProcessJson = gson.toJson(curProcess);
//        List<String> rearNodeList = getRearNodeList(firstToBeExecutedComponent, processComponentJson);
//        String lastToBeExecutedComponent = getLastToBeExecutedComponent(curProcessJson);
//        jsonObject = JSONObject.parseObject(curProcessJson, Feature.OrderedField);
//        String rearIds = jsonObject.getJSONObject(lastToBeExecutedComponent).getString("rearIds");
//        if(rearIds.contains(firstToBeExecutedComponent))
//        {
//            String replace = rearIds.replace(firstToBeExecutedComponent, rearNodeList.get(0));
//            curProcess.remove(lastToBeExecutedComponent);
//        }
//        processComponent.remove(firstToBeExecutedComponent);
//    }





    public static void main(String[] args) {
        Gson gson = new Gson();
        String xmlPath = "E:\\home\\workflowXml\\20201130\\16dccd1a-b30d-4266-9deb-6e73fbf1d83f.xml";
        String xmlPath1 = "E:\\home\\workflowXml\\20201130\\58ba10d2-428b-44b8-8e93-d731ecfea213.xml";
        PipelineServiceImpl pipelineService = new PipelineServiceImpl();
        Map<String, PythonParameters> pythonParametersMap = XmlUtils.getPythonParametersMap(xmlPath);
        Map<String, PythonParameters> pythonParametersMap1= XmlUtils.getPythonParametersMap(xmlPath1);
        System.out.println(gson.toJson(pythonParametersMap));
        System.out.println(gson.toJson(pythonParametersMap1));


////        pipelineService.generatePipeline(gson.toJson(pythonParametersMap));
//        // System.out.println(JsonUtils.getParamsByComponentName(gson.toJson(pythonParametersMap),"_3_5"));
//        System.out.println(getFirstToBeExecutedComponent(gson.toJson(pythonParametersMap)));
//        System.out.println(getPriorNodeList("_2_2",gson.toJson(pythonParametersMap)).get(0));
        //System.out.println(getComponenetByOrder(gson.toJson(pythonParametersMap)));


    }
}

