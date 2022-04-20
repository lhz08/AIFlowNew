package com.bdilab.aiflow.model.job;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Data
public class ApiDifferMap {
    private HashMap<Integer, String> oldmap=new HashMap<>();
    private HashMap<Integer, String> newmap;
}
