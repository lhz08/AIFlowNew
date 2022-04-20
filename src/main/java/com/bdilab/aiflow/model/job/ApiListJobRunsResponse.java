package com.bdilab.aiflow.model.job;

import com.bdilab.aiflow.model.run.ApiRun;
import lombok.Data;

@Data
public class ApiListJobRunsResponse {
    private ApiRun[] apiRuns;
    private Integer total_size;
    private String next_page_token;
}
