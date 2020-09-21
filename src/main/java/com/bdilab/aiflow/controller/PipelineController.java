package com.bdilab.aiflow.controller;

import com.bdilab.aiflow.service.pipeline.PipelineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Api(value = "pipeline控制器")
@Controller
@CrossOrigin
public class PipelineController {
    @Autowired
    PipelineService pipelineService;

    @ResponseBody
    @ApiOperation("生成pipeline")
    @RequestMapping(value = "/pipeline/generatePipeline",method = RequestMethod.POST)
    public boolean generatePipeline(@RequestParam @ApiParam(value = "xml文件地址") String xmlPath,
                                    @RequestParam @ApiParam(value = "流程名") String workflowName,
                                    HttpSession httpSession) {
        Integer userId = Integer.parseInt(httpSession.getAttribute("user_id").toString());
        pipelineService.generatePipeline(userId,xmlPath,workflowName);
        return true;
    }

}
