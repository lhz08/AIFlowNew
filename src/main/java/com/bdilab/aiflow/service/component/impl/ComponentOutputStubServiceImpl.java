package com.bdilab.aiflow.service.component.impl;

import com.bdilab.aiflow.common.hbase.HBaseConnection;
import com.bdilab.aiflow.common.hbase.HBaseUtils;
import com.bdilab.aiflow.mapper.ComponentOutputStubMapper;
import com.bdilab.aiflow.model.ComponentOutputStub;
import com.bdilab.aiflow.service.component.ComponentOutputStubService;
import org.apache.hadoop.hbase.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ComponentOutputStubServiceImpl implements ComponentOutputStubService {

    @Autowired
    ComponentOutputStubMapper componentOutputStubMapper;

    @Override
    public boolean deleteOutputByRunningId(Integer runningId) throws Exception{
        //删除文件或者hbase表
        //要先从输出表中分类获取
        List<ComponentOutputStub> List=componentOutputStubMapper.selectByRunningId(runningId);
        for(ComponentOutputStub componentOutputStub:List){
            if(componentOutputStub.getOutputFileType()==0){
                //表结构
                String tableName=componentOutputStub.getOutputTableName();
                //删除hbase表的操作
                //System.out.println("进入hbase删除");
                try{
                    Connection connection=HBaseConnection.getConn();
                    HBaseUtils hBaseUtils=new HBaseUtils();
                    hBaseUtils.deletedHbase(componentOutputStub.getOutputTableName(),connection);
                    connection.close();
                    boolean isSuccess=componentOutputStubMapper.deleteOutputById(componentOutputStub.getId());
                    if(!isSuccess){
                        return false;
                    }
                    //System.out.println("hbase删除成功");
                }catch(Exception e){
                    return false;
                }
            }else{
                //文件路径
                String filePath=componentOutputStub.getOutputFileAddr();
                //删除文件操作
                File file=new File(filePath);
                if(!file.exists()){
                    //System.out.println("该文件夹不存在");
                }else{
                    boolean isSuccess=file.delete();
                    if(!isSuccess){
                        return false;
                    }
                }
                boolean isSuccess=componentOutputStubMapper.deleteOutputById(componentOutputStub.getId());
                if(!isSuccess){
                    return false;
                }
            }
        }
        return true;

    }
}
