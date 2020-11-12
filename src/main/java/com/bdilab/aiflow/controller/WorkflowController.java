package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.workflow.WorkflowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


@Controller
@CrossOrigin
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 保存流程
     * @param workflowName
     * @param tagString
     * @param workflowDesc
     * @param workflowXml
     * @param ggeditorObjectString
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/createAndSaveWorkflow", method = RequestMethod.POST)
    public ResponseResult createAndSaveWorkflow(@RequestParam String workflowName,
                                                @RequestParam String tagString,
                                                @RequestParam String workflowDesc,
                                                @RequestParam String workflowXml,
                                                @RequestParam String ggeditorObjectString,
                                                HttpSession httpSession
                                              ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());

        //在点击新建后立即新建一条流程记录
        //Workflow workflow = workflowService.CreateWorkflow(workflowName,tagString,workflowDesc,Integer.parseInt(httpSession.getAttribute("username").toString()));
        Workflow workflow = workflowService.createAndSaveWorkflow(workflowName,tagString,workflowDesc,workflowXml,ggeditorObjectString,userId);

        Map<String,Object> data = new HashMap<>(1);
        data.put("workflowId",workflow.getId());
        ResponseResult responseResult = new ResponseResult(true,"001", "流程新建成功");
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * 修改流程（只能修改未曾创建实验的流程）
     *
     * @param workflowId
     * @param workflowXml
     * @param ggeditorObjectString
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/updateWorkflow", method = RequestMethod.POST)
    public ResponseResult updateWorkflow(@RequestParam Integer workflowId,
                                         @RequestParam String workflowXml,
                                         @RequestParam String ggeditorObjectString,
                                         HttpSession httpSession
                                      ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = workflowService.updateWorkflow(workflowId,workflowXml,ggeditorObjectString,userId);
        if(isSuccess){
            return new ResponseResult(true,"001","流程修改成功");
        }
        return new ResponseResult(false,"002","流程修改失败");
    }

    /**
     * 根据流程id下载其pipeline文件
     * @param workflowId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/downloadWorkflow", method = RequestMethod.POST)
    public ResponseResult downloadWorkflow(@RequestParam Integer workflowId,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Workflow workflow = workflowService.selectWorkflowById(workflowId);
        String pipelineFilePath = workflow.getGeneratePipelineAddr();
        if(pipelineFilePath != null){
            File pipelineFile = new File(pipelineFilePath);
            if(pipelineFile.exists()){
                // 配置文件下载
                try {
                    response.setHeader("content-type", "application/octet-stream");
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(pipelineFilePath, "UTF-8"));
                    //下载缓冲
                    byte[] buffer = new byte[1024];
                    FileInputStream fileInputStream = new FileInputStream(pipelineFile);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    OutputStream outputStream= response.getOutputStream();
                    int i = bufferedInputStream.read(buffer);
                    while(i!= -1){
                        outputStream.write(buffer,0,i);
                        i=bufferedInputStream.read(buffer);
                    }
                    bufferedInputStream.close();
                    fileInputStream.close();
                    return new ResponseResult(true,"001","流程文件下载成功");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return new ResponseResult(false,"002","文件不存在");
        }
        return new ResponseResult(false,"003","不存在pipeline文件路径");
    }

    @ResponseBody
    @RequestMapping(value = "/workflow/selectWorkflowById", method = RequestMethod.POST)
    public ResponseResult selectWorkflowById(@RequestParam Integer workflowId,
                                             HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Workflow workflow = workflowService.selectWorkflowById(workflowId);
        Map<String,Object> data = new HashMap<>();
        data.put("workflow",workflow);
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(data);
        if(workflow!=null){
            responseResult.setSuccess(true);
            responseResult.setCode("001");
            responseResult.setMessage("流程搜索成功");
            return responseResult;
        }
        responseResult.setSuccess(true);
        responseResult.setCode("002");
        responseResult.setMessage("没有搜索到该流程");
        return responseResult;
    }


    /**
     * 展示所有未删除的流程和下属实验
     * @param pageNum
     * @param pageSize
     * @return WorkflowVOList
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/selectAllWorkflowByUserId", method = RequestMethod.POST)
    public ResponseResult selectAllWorkflowByUserId(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    HttpSession httpSession
                                                    ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Workflow workflow = new Workflow();
        workflow.setFkUserId(userId);
        workflow.setIsDeleted(Byte.parseByte("0"));
        Map<String,Object> data = workflowService.selectAllWorkflowByUserIdAndIsDeleted(workflow,pageNum,pageSize);

        ResponseResult responseResult = new ResponseResult(true,"001", "流程列表搜索成功");
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * 搜索栏，根据流程名称搜索，得到workflowVO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/searchWorkflowByName", method = RequestMethod.POST)
    public ResponseResult searchWorkflowByName(@RequestParam(required = false) String workflowName,
                                         @RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         HttpSession httpSession
                                         ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());

        Map<String,Object> data = workflowService.searchWorkflowByName(workflowName,pageNum,pageSize,userId);
        ResponseResult responseResult = new ResponseResult(true,"001", "流程条件搜索成功");
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * 搜索栏，根据流程标签搜索，得到workflowVO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/searchWorkflowByTags", method = RequestMethod.POST)
    public ResponseResult searchWorkflowByTags(@RequestParam(required = false) String workflowTags,
                                         @RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         HttpSession httpSession
    ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());

        Map<String,Object> data = workflowService.searchWorkflowByTags(workflowTags,pageNum,pageSize,userId);
        ResponseResult responseResult = new ResponseResult(true,"001", "流程条件搜索成功");
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * 展示所有已经删除的流程，不含实验信息
     * @param pageNum
     * @param pageSize
     * @return WorkflowList
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/selectDeletedWorkflow", method = RequestMethod.POST)
    public ResponseResult selectDeletedWorkflow(@RequestParam(defaultValue = "1") int pageNum,
                                                @RequestParam(defaultValue = "10") int pageSize,
                                                HttpSession httpSession
                                             ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Workflow workflow = new Workflow();
        workflow.setFkUserId(userId);
        workflow.setIsDeleted(Byte.parseByte("1"));
        Map<String,Object> data = workflowService.selectAllWorkflowOnlyByUserIdAndIsDeleted(workflow,pageNum,pageSize);

        ResponseResult responseResult = new ResponseResult(true,"001", "流程列表搜索成功");
        responseResult.setData(data);
        return responseResult;
    }


    /**
     * 克隆流程
     * @param workflowId
     * @param workflowName
     * @param tagString
     * @param workflowDesc
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/cloneWorkflow", method = RequestMethod.POST)
    public ResponseResult cloneWorkflow(@RequestParam Integer workflowId,
                                        @RequestParam String workflowName,
                                        @RequestParam String tagString,
                                        @RequestParam String workflowDesc,
                                        HttpSession httpSession
                                        ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Workflow newWorkflow=workflowService.cloneWorkflow(workflowId,workflowName,tagString,workflowDesc,userId);

        Map<String,Object> data=new HashMap<>();
        data.put("newWorkflowId",newWorkflow.getId());
        ResponseResult responseResult = new ResponseResult(true,"001", "流程克隆成功");
        responseResult.setData(data);
        return responseResult;
    }


    /**
     * 根据id删除流程，如果未删除，会将流程、模板、实验和运行都标isdeleted=1
     * 如果isdeleted=1，会将流程、模板、实验和运行都完全删除
     * @param workflowId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/workflow/deleteWorkflow", method = RequestMethod.POST)
    public ResponseResult deleteWorkflow(@RequestParam Integer workflowId,
                                         HttpSession httpSession){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        Workflow workflow= workflowService.selectWorkflowById(workflowId);
        if(workflow.getIsDeleted()==0) {
            boolean isSuccess=workflowService.deleteWorkflow(workflow);
            if(isSuccess){
                return new ResponseResult(true,"001","流程删除成功");
            }
            return new ResponseResult(false,"002","流程删除失败");
        }
        else if(workflow.getIsDeleted()==1){
            boolean isSuccess=workflowService.deleteWorkflowTotal(workflow);
            if(isSuccess){
                return new ResponseResult(true,"001","流程彻底删除成功");
            }
            return new ResponseResult(false,"002","流程彻底删除失败");
        }
        ResponseResult responseResult = new ResponseResult(true,"003", "isDeleted判断错误");
        return responseResult;
    }

    @ResponseBody
    @RequestMapping(value = "/workflow/restoreWorkflow", method = RequestMethod.POST)
    public ResponseResult restoreWorkflow(@RequestParam Integer workflowId,
                                          HttpSession httpSession
                                        ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        boolean isSuccess=workflowService.restoreWorkflow(workflowId);
        if(isSuccess){
            return new ResponseResult(true,"001","流程还原成功");
        }
        return new ResponseResult(true,"002","流程还原失败");
    }
}
