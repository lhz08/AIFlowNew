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

    //根据组件名和它的后置结点数组中已经执行完的结点，返回接下来可以执行的组件列表。
    public static List<String> getExecutableComponent(String componentName, String executedComponent, String json) {
        List<String> list = getRearNodeList(componentName, json);
        List<String> result = getRearNodeList(componentName, json);
        for (String s : list
        ) {
            if (!s.equals(executedComponent)) {
                result.remove(s);
            } else {
                result.remove(executedComponent);
                break;
            }
        }

        return result;
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

    public static void main(String[] args) {
        Gson gson = new Gson();
        String xmlPath = "C:\\Users\\cuishaohui\\Desktop\\f47d448d-364c-4521-881d-820a50710b00.xml";
        PipelineServiceImpl pipelineService = new PipelineServiceImpl();
        Map<String, PythonParameters> pythonParametersMap = XmlUtils.getPythonParametersMap(xmlPath);
        System.out.println(gson.toJson(pythonParametersMap));
//        pipelineService.generatePipeline(gson.toJson(pythonParametersMap));
        // System.out.println(JsonUtils.getParamsByComponentName(gson.toJson(pythonParametersMap),"_3_5"));
        System.out.println(getFirstToBeExecutedComponent(gson.toJson(pythonParametersMap)));
        System.out.println(getPriorNodeList("_2_2",gson.toJson(pythonParametersMap)).get(0));
    }
}
