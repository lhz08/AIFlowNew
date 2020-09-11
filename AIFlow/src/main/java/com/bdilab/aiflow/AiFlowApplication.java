package com.bdilab.aiflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bdilab.aiflow.mapper")
public class AiFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiFlowApplication.class, args);
	}

}
