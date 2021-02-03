package com.bdilab.aiflow.service;

import com.bdilab.aiflow.common.hbase.HBaseConnection;
import org.junit.Test;
import org.springframework.stereotype.Service;

@Service
public class HbaseService {

    @Test
    public void test(){
        HBaseConnection hBaseConnection=new HBaseConnection();
        hBaseConnection.getConn();
    }
}
