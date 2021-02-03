package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.sse.ProcessSseEmitters;
import com.bdilab.aiflow.mapper.ExperimentRunningMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;


/**
 * @Decription sse，server-send-events，服务端向前端发送消息，用于通知任务执行结果
 * 前端在push接口创建SseEmitter，并绑定uuid
 * TaskCompletedListener维护了一个Map，保存了uuid和SseEmitter的对应关系，使用uuid即可找到对应的SseEmitter
 * 服务端为流程创建事件，使用uuid找到对应的SseEmitter对象，使用SseEmitter.send方法推送消息
 * 当流程执行完毕，使用SseEmitter.complete方法结束SSE会话，向push接口推送执行完毕消息，
 * @Author Humphrey
 * @Date 2019/09/27
 */
@Api(value = "服务端推送事件Controller")
@Controller
@CrossOrigin
public class SseProcessController {

    @Resource
    ExperimentRunningMapper experimentRunningMapper;
    /**
     * 开启SSE会话
     * @param uuid uuid,会话唯一标识，服务端需要此id来向前端推送消息
     * @return
     */

    @ApiOperation("开启SSE会话")
    @ResponseBody
    @RequestMapping(value = "/sse/push",method = RequestMethod.GET)
    public SseEmitter push(@RequestParam @ApiParam(value = "会话id") String uuid){
        SseEmitter emitter = new SseEmitter(0L);

        try {
            ProcessSseEmitters.addSseEmitters(uuid,emitter);
        }catch (Exception e){
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @ApiOperation("SSE会话重连")
    @ResponseBody
    @RequestMapping(value = "/sse/dlProcess/reconnect",method = RequestMethod.GET)
    public SseEmitter reconnect(@RequestParam @ApiParam(value = "processLog id") Integer processLogId){
        SseEmitter sseEmitter = null;
        try {
            sseEmitter =ProcessSseEmitters.getSseEmitterByKey(experimentRunningMapper.selectExperimentRunningByRunningId(processLogId).getConversationId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return sseEmitter;
    }
}