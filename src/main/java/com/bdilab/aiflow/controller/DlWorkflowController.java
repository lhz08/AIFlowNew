package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.common.response.ResponseResult;
import com.bdilab.aiflow.model.Workflow;
import com.bdilab.aiflow.service.deeplearning.workflow.DlWorkflowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin
@Api(value="深度学习WorkflowController")
@RequestMapping(value = "/dlWorkflow")
public class DlWorkflowController {


    @Autowired
    DlWorkflowService dlWorkflowService;

    @ResponseBody
    @ApiOperation(value = "保存流程")
    @RequestMapping(value = "/createAndSaveWorkflow", method = RequestMethod.POST)
    public ResponseResult createAndSaveWorkflow(@RequestParam String workflowName,
                                                @RequestParam String tagString,
                                                @RequestParam String workflowDesc,
                                                @RequestParam String workflowXml,
                                                @RequestParam String ggeditorObjectString,
                                                HttpSession httpSession
    ){
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());


        return new ResponseResult();
    }

    @ResponseBody
    @ApiOperation(value = "test")
    @RequestMapping(value = "/createAndSaveWorkflow", method = RequestMethod.GET)
    public ResponseResult test(){
        dlWorkflowService.generateDLPipeline("E:\\home\\workflowXml\\6c8aaba0-529e-44d2-9cdc-053b51b10e9d.xml",6);
        return new ResponseResult();
    }

}
