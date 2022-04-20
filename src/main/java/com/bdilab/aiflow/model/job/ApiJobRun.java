package com.bdilab.aiflow.model.job;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ApiJobRun {
    private String page_token;
    private Integer page_size;
    private String sort_by;
    private ApiJobRunResourceReference apiJobRunResourceReference;
    private String filter;
}
