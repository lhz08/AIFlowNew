package com.bdilab.aiflow.service.run.impl;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.enums.RunningStatus;
import com.bdilab.aiflow.common.hbase.HBaseConnection;
import com.bdilab.aiflow.common.hbase.HBaseUtils;
import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.common.sse.ProcessSseEmitters;
import com.bdilab.aiflow.common.utils.JsonUtils;
import com.bdilab.aiflow.common.utils.XmlUtils;
import com.bdilab.aiflow.mapper.*;
import com.bdilab.aiflow.model.*;
import com.bdilab.aiflow.model.job.ApiJob;
import com.bdilab.aiflow.model.job.ApiPeriodicSchedule;
import com.bdilab.aiflow.model.job.ApiSchedule;
import com.bdilab.aiflow.model.job.ApiTrigger;
import com.bdilab.aiflow.model.run.ApiParameter;
import com.bdilab.aiflow.model.run.ApiPipelineSpec;
import com.bdilab.aiflow.model.run.ApiRun;
import com.bdilab.aiflow.model.workflow.EpochInfo;
import com.bdilab.aiflow.service.run.RunService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author smile
 * @data 2020/9/21 10:14
 **/
@Service
public class RunServiceImpl implements RunService {

    @Value("${kubeflow.url}")
    String url;

    @Resource
    ComponentInfoMapper componentInfoMapper;
    @Resource
    ExperimentRunningMapper experimentRunningMapper;
    @Resource
    ExperimentMapper experimentMapper;
    @Resource
    WorkflowMapper workflowMapper;
    @Resource
    ComponentOutputStubMapper componentOutputStubMapper;
    @Resource
    FilePathConfig filePathConfig;
    @Resource
    ModelMapper modelMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RestTemplate restTemplate;

    @Value("${nfs.path}")
    private String nfsPath;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.ip}")
    private String serverIp;

    @Override
    public boolean pushData(String runningId,String taskId,String conversationId,String resultTable) {
        Gson gson = new Gson();
        //用runningid查到
        Integer experimentId = experimentRunningMapper.selectExperimentRunningByRunningId(Integer.parseInt(runningId)).getFkExperimentId();
        Experiment experiment = experimentMapper.selectExperimentById(experimentId);
        Workflow workflow = workflowMapper.selectWorkflowById(experiment.getFkWorkflowId());
        String xmlPath = workflow.getWorkflowXmlAddr();


        String json = gson.toJson(XmlUtils.getPythonParametersMap(xmlPath));
        List<String> taskList = JsonUtils.getComponenetByOrder(json);

        if(!taskId.equals(getComponentId(taskList.get(taskList.size()-1)))) {
            System.out.println(resultTable);
            setComponentOutputStub(runningId, taskId, resultTable);
            if(!sendEvent(taskId, conversationId,getNextNode(taskId,taskList,json))) {
                return false;
            }
            return true;
        }
        //将最后一个组件的执行结果存表
        setComponentOutputStub(runningId, taskId, resultTable);
        //推送最后一个组件执行状态
        sendEvent(taskId,conversationId,null);
        logger.info("所有结点执行完毕");
        long t1=System.currentTimeMillis();
        System.out.println("所有节点运行结束"+t1);
        Map<String, Object> pushFinishData = new HashMap<>();
        pushFinishData.put("processName", workflow.getName());
        pushFinishData.put("status", "finished");
        pushFinishData.put("processLogId", runningId);
        ResponseResult responseResult1 = new ResponseResult(true, "004", "完成流程：",pushFinishData);
        ProcessSseEmitters.sendEvent(conversationId,responseResult1);
       // 结束sse会话
        ProcessSseEmitters.getSseEmitterByKey(conversationId).complete();
       // 清除sse对象
        ProcessSseEmitters.removeSseEmitterByKey(conversationId);
        ExperimentRunning experimentRunning = new ExperimentRunning();
        experimentRunning.setId(Integer.parseInt(runningId));
        experimentRunning.setRunningStatus(RunningStatus.RUNNINGSUCCESS.getValue());
        experimentRunning.setEndTime(new Date());
        experimentRunningMapper.updateExperimentRunning(experimentRunning);
        return true;

    }

    @Override
    public void pushEpochInfo(Integer experimentRunningId, EpochInfo epochInfo, String modelFilePath){
        ExperimentRunning experimentRunning = experimentRunningMapper.selectExperimentRunningByRunningId(experimentRunningId);
        String conversationId = experimentRunning.getConversationId();

        Gson gson = new Gson();
        logger.info("Get EpochInfo:"+gson.toJson(epochInfo));
        for(String string:epochInfo.getResult().keySet()){
            logger.info("value size: "+epochInfo.getResult().get(string).length());
            if(epochInfo.getResult().get(string).length()>0){
                String strings[] = epochInfo.getResult().get(string).split(", ");
                String newUrl = "";
                for(String str:strings){
                    logger.info("change file path to url");
                    newUrl = newUrl+","+str.replace(nfsPath,serverIp+":"+serverPort+"/dataset_file/");
                }
                epochInfo.getResult().replace(string,newUrl);
            }
        }
        //将数据点写入redis
        String key = conversationId+"_"+experimentRunningId;
        stringRedisTemplate.opsForList().leftPush(key,gson.toJson(epochInfo));
        //设置缓存数据过期时间为3天
        stringRedisTemplate.expire(key,3, TimeUnit.DAYS);
        //推送消息
//        ProcessSseEmitters.sendEvent(conversationId,epochInfo);
        logger.info("Get EpochInfo:"+gson.toJson(epochInfo));
        if(epochInfo.getEnd()) {
//            //结束sse会话
//            try {
//                ProcessSseEmitters.getSseEmitterByKey(conversationId).complete();
//            } catch (NullPointerException npe) {
//                npe.printStackTrace();
//            }

            //清除sse对象
//            ProcessSseEmitters.removeSseEmitterByKey(conversationId);
            //将redis中数据点输入至HBase
            List<String> epochInfoStrings = stringRedisTemplate.opsForList().range(key, 0, stringRedisTemplate.opsForList().size(key) - 1);
            List<EpochInfo> epochInfos = epochInfoStrings.stream().map(a -> gson.fromJson(a, EpochInfo.class)).collect(Collectors.toList());
            String tableName = HBaseUtils.insetEpochInfo(experimentRunningId, epochInfos, HBaseConnection.getConn());
            logger.info("tableName: "+tableName);
            System.out.println("tableName: "+tableName);
            Model model = modelMapper.selectModelByExperimentRunningId(experimentRunningId);
            if(model==null){
                //创建模型
                createModel(experimentRunningId,modelFilePath);
                model = modelMapper.selectModelByExperimentRunningId(experimentRunningId);
            }

            model.setBasicConclusion(epochInfo.getBasic_conclusion());
            model.setCreateTime(new Date());
            model.setTestLoss(epochInfo.getTest_loss());
            model.setTrainLoss(epochInfo.getTrain_loss());
            modelMapper.updateModel(model);

            //更新运行信息
            experimentRunning.setEndTime(new Date());

            experimentRunning.setFkDlResultTableName(tableName);
            experimentRunning.setFkModelId(model.getId());
            experimentRunning.setRunningStatus(RunningStatus.RUNNINGSUCCESS.getValue());
            experimentRunningMapper.updateExperimentRunning(experimentRunning);
        }
    }
    @Override
    public void createModel(Integer experimentRunningId, String modelFilePath) {
        Model model = new Model();
        Experiment experiment = experimentMapper.selectExperimentById(experimentRunningMapper.selectExperimentRunningByRunningId(experimentRunningId).getFkExperimentId());
        model.setFkRunningId(experimentRunningId);
        model.setFkUserId(experiment.getFkUserId());
        model.setModelFileAddr(modelFilePath);
        model.setIsSaved(0);
        model.setCreateTime(new Date());
        model.setIsDeleted((byte) 0);
        modelMapper.insertModel(model);
    }

    @Override
    public String getComponentId(String string){
        return string.split("_")[1];
    }


    private boolean sendEvent(String taskId,String conversationId,List<String> nextTaskId){
        Map<String,Object> pushData = new HashMap<>();
        String taskName=componentInfoMapper.selectComponentInfoById(Integer.parseInt(taskId)).getName();
        List<String> nextTaskName = null;
        if(nextTaskId!=null) {
            nextTaskName = new ArrayList<>(nextTaskId.size()) ;
            for(int i =0;i<nextTaskId.size();i++) {
                nextTaskName.add(i,componentInfoMapper.selectComponentInfoById(Integer.parseInt(getComponentId(nextTaskId.get(i)))).getComponentDesc());
            }
        }
        pushData.put("taskName",taskName);
        pushData.put("nextTask",nextTaskName);
        ResponseResult responseResult = new ResponseResult(true,"001","完成任务：",pushData);
        ProcessSseEmitters.sendEvent(conversationId,responseResult);
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" 完成任务："+taskName);
        return true;
    }

    private List<String> getNextNode(String curComponeneId,List<String> taskList,String json){
//        for(int i = 0 ;i<taskList.size();i++){
//            if (getComponentId(taskList.get(i)).equals(curComponeneId)) {
//                taskList = taskList.subList(i, taskList.size());
//                System.out.println(componentInfoMapper.selectComponentInfoById(Integer.parseInt(curComponeneId)).getName()+"  asf"+taskList.toString());
//                break;
//            }
//        }
//        return getComponentId(taskList.get(1));
        for (String s:taskList
        ) {
            if (getComponentId(s).equals(curComponeneId)){
                curComponeneId = s;
            }
        }
        return JsonUtils.getRearNodeList(curComponeneId,json);
    }


    private void setComponentOutputStub(String runningId,String taskId,String resultTable){
        Gson gson = new Gson();
        List<Map<String, String>> resultTableList = null;
        if(resultTable!="") {
            resultTableList = gson.fromJson(resultTable, List.class);

            for (Map<String, String> map : resultTableList) {
                ComponentOutputStub componentOutputStub = new ComponentOutputStub();
                componentOutputStub.setFkRunningId(Integer.parseInt(runningId));
                componentOutputStub.setFkComponentInfoId(Integer.parseInt(taskId));
                componentOutputStub.setOutputFileAddr(map.get("result_path").replace("'", ""));
                componentOutputStub.setOutputFileType(map.get("result_type"));
                componentOutputStub.setOutputTableName(map.get("result_path").replace("'", ""));
                if (map.containsKey("graph_type")) {
                    componentOutputStub.setGraphType(Integer.parseInt(map.get("graph_type").trim()));
                } else {
                    componentOutputStub.setGraphType(0);
                }
                componentOutputStubMapper.insert(componentOutputStub);
            }
        }
    }
    @Override
    public String createRun(String pipelineId, String pipelineName, Map<String,Object> parameter) {
        ApiRun apiRun = new ApiRun();
        ApiPipelineSpec apiPipelineSpec = new ApiPipelineSpec();
        int paramLength = parameter.size();
        Object[] parameters = new Object[paramLength];
        int i = 0;
        for(Map.Entry<String,Object> entry:parameter.entrySet()){
            ApiParameter apiParameter = new ApiParameter();
            apiParameter.setName(entry.getKey());
            apiParameter.setValue(entry.getValue().toString());
            parameters[i] = apiParameter;
            i++;
        }
        apiRun.setName("run");
        apiRun.setDescription("desc");
        apiPipelineSpec.setPipelineId(pipelineId);
        apiPipelineSpec.setPipelineName(pipelineName);
        apiPipelineSpec.setParameters(parameters);
        apiRun.setPipeline_spec(apiPipelineSpec);

        Gson gson = new Gson();
        String json = gson.toJson(apiRun);
        System.out.println(json);

        String kubeflowUrl = url+"pipeline/apis/v1beta1/runs";
        System.out.println("kubeflowUrl== "+kubeflowUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ApiRun> request = new HttpEntity(apiRun, (MultiValueMap)headers);
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(kubeflowUrl, request, String.class, new Object[0]);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue == 200){
            //获取返回结果中的运行id
            Gson gson1 = new Gson();
            Map<String,Map<String,String>> map = gson1.fromJson(responseEntity.getBody(), Map.class);
            Map<String,String> run = map.get("run");
            return run.get("id");
        }
        return null;
    }

    @Override
    public String createCycleRun(String pipelineId, String pipelineName, Map<String,Object> parameter, ApiSchedule apiSchedule) {
        ApiJob apiJob = new ApiJob();
        ApiPipelineSpec apiPipelineSpec = new ApiPipelineSpec();
        int paramLength = parameter.size();
        Object[] parameters = new Object[paramLength];
        int i = 0;
        for(Map.Entry<String,Object> entry:parameter.entrySet()){
            ApiParameter apiParameter = new ApiParameter();
            apiParameter.setName(entry.getKey());
            apiParameter.setValue(entry.getValue().toString());
            parameters[i] = apiParameter;
            i++;
        }
        apiJob.setName("job");
        apiJob.setDescription("desc");
        apiJob.setMax_concurrency("10");
        apiJob.setEnabled(true);
        ApiTrigger apiTrigger = new ApiTrigger();
        ApiPeriodicSchedule apiPeriodicSchedule = new ApiPeriodicSchedule();
        apiPeriodicSchedule.setStart_time(apiSchedule.getStart_time());
        apiPeriodicSchedule.setEnd_time(apiSchedule.getEnd_time());
        apiPeriodicSchedule.setInterval_second(apiSchedule.getScheduleTime());
        apiTrigger.setPeriodic_schedule(apiPeriodicSchedule);
        apiJob.setTrigger(apiTrigger);
        apiPipelineSpec.setPipelineId(pipelineId);
        apiPipelineSpec.setPipelineName(pipelineName);

        apiPipelineSpec.setParameters(parameters);
        apiJob.setPipeline_spec(apiPipelineSpec);

        Gson gson = new Gson();
        String json = gson.toJson(apiJob);
        System.out.println(json);

        String kubeflowUrl = url+"pipeline/apis/v1beta1/jobs";
        System.out.println("kubeflowUrl== "+kubeflowUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ApiJob> request = new HttpEntity(apiJob, (MultiValueMap)headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(kubeflowUrl, request, String.class, new Object[0]);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        if (statusCodeValue == 200){
            //获取返回结果中的运行id
            Gson gson1 = new Gson();
            Map<String,String> map = gson1.fromJson(responseEntity.getBody(), Map.class);
            String jobId=map.get("id");
            return jobId;
        }
        return null;
    }
    @Override
    public boolean deleteRunById(String runId) {

        String kubeflowUrl = url + "pipeline/apis/v1beta1/runs/" + runId;
        restTemplate.delete(kubeflowUrl);
        return true;
    }

    @Override
    public void reportFailure(Integer experimentRunningId,String errorMessage){
        ExperimentRunning experimentRunning =experimentRunningMapper.selectExperimentRunningByRunningId(experimentRunningId);
        experimentRunning.setEndTime(new Date());
        experimentRunning.setRunningStatus(RunningStatus.RUNNINGFAIL.getValue());
        experimentRunningMapper.updateExperimentRunning(experimentRunning);
        Map<String,Object> pushData = new HashMap<>();
        pushData.put("status","failed");
        pushData.put("message",errorMessage);
        String conversationId = experimentRunning.getConversationId();
        ProcessSseEmitters.sendEvent(conversationId,pushData);

        //结束sse会话
        ProcessSseEmitters.getSseEmitterByKey(conversationId).complete();
        //清除sse对象
        ProcessSseEmitters.removeSseEmitterByKey(conversationId);
    }
}
