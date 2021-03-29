package com.bdilab.aiflow.service.component.impl;

import com.bdilab.aiflow.common.config.FilePathConfig;
import com.bdilab.aiflow.common.hbase.HBaseConnection;
import com.bdilab.aiflow.common.hbase.HBaseUtils;
import com.bdilab.aiflow.common.utils.FileUtils;
import com.bdilab.aiflow.mapper.ComponentInfoMapper;
import com.bdilab.aiflow.mapper.ComponentOutputStubMapper;
import com.bdilab.aiflow.mapper.GraphTypeMapper;
import com.bdilab.aiflow.model.ComponentInfo;
import com.bdilab.aiflow.model.ComponentOutputStub;
import com.bdilab.aiflow.model.GraphType;
import com.bdilab.aiflow.service.component.ComponentOutputStubService;
import org.apache.hadoop.hbase.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComponentOutputStubServiceImpl implements ComponentOutputStubService {

    @Resource
    ComponentOutputStubMapper componentOutputStubMapper;
    @Resource
    FilePathConfig filePathConfig;
    @Resource
    GraphTypeMapper graphTypeMapper;


    @Override
    public Map<String,Object> getOutputFileAddr(Integer runningId,Integer fkComponentId,Integer graphType){
        //type为空或者为0表示没有写入图类型信息，只根据两个id查
        Map<String,Object> messageMap=new HashMap<>(2);
        String outputFileAddr;
        try{
            if(graphType==null||graphType==0){
                List<ComponentOutputStub> componentOutputStubList = componentOutputStubMapper.selectByRunningId(runningId, fkComponentId);
                outputFileAddr=componentOutputStubList.get(0).getOutputFileAddr();
            }
            else{
                List<ComponentOutputStub> componentOutputStubList = componentOutputStubMapper.selectByRunningComponentAndType(runningId, fkComponentId,graphType);
                outputFileAddr=componentOutputStubList.get(0).getOutputFileAddr();
            }
            messageMap.put("outputFileAddr",outputFileAddr);
            messageMap.put("isSuccess",true);
            return messageMap;
        }
        catch(Exception e){
            e.printStackTrace();
            messageMap.put("isSuccess",false);
            return messageMap;
        }

    }

    @Override
    public boolean deleteOutputByRunningId(Integer runningId) throws Exception{
        //删除文件或者hbase表
        //要先从输出表中分类获取
        List<ComponentOutputStub> List=componentOutputStubMapper.selectByRunningId(runningId,null);
        for(ComponentOutputStub componentOutputStub:List){
//            if(componentOutputStub.getOutputFileType()==0){
//                //表结构
//                String tableName=componentOutputStub.getOutputTableName();
//                //删除hbase表的操作
//                //System.out.println("进入hbase删除");
//                try{
//                    Connection connection=HBaseConnection.getConn();
//                    HBaseUtils hBaseUtils=new HBaseUtils();
//                    hBaseUtils.deletedHbase(componentOutputStub.getOutputTableName(),connection);
//                    connection.close();
//                    boolean isSuccess=componentOutputStubMapper.deleteOutputById(componentOutputStub.getId());
//                    if(!isSuccess){
//                        return false;
//                    }
//                    //System.out.println("hbase删除成功");
//                }catch(Exception e){
//                    return false;
//                }
//            }else{
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
//        }
        return true;

    }

    @Override
    public List<ComponentOutputStub> getComponentResult(Integer runningId,Integer componentId) {
        return componentOutputStubMapper.selectByRunningId(runningId,componentId);
    }

    @Override
    public Map<String, Object> previewResult(Integer componentOutputStubId,Integer userId) {
        ComponentOutputStub componentOutputStub =   componentOutputStubMapper.selectById(componentOutputStubId);
        String filePath =  filePathConfig.getComponentResultPath()+"user"+userId+componentOutputStub.getOutputFileAddr();
        //String filePath = filePathConfig.getComponentResultPath()+"user"+userId;
        System.out.println(filePath);
        List<String[]> csvContent = FileUtils.csvContentPreview(filePath);
        Map<String,Object> data = new HashMap<>();
        Map<String,String> graph = new HashMap<>();
        if(componentOutputStub.getGraphType()!=0) {
            GraphType graphType = graphTypeMapper.selectById(componentOutputStub.getGraphType());
            graph.put("graph_id", graphType.getId().toString());
            graph.put("graph_name", graphType.getGraphTypeName());
            graph.put("graph_desc", graphType.getGraphDesc());
        }
        data.put("content",csvContent);
        data.put("total",csvContent == null?0:csvContent.size());
        data.put("graph",graph);
        return data;
    }
}
