package com.bdilab.aiflow.common.hbase;

import com.bdilab.aiflow.model.workflow.EpochInfo;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;


import java.io.IOException;
import java.util.*;

public class HBaseUtils {
    /**
     * 按表名删除Hbase表
     * @param tableName
     * @param connection
     * @return
     */
    public boolean deletedHbase(String tableName, Connection connection){
        try {
            Admin admin=connection.getAdmin();
            TableName name=TableName.valueOf(tableName);
            admin.disableTable(name);
            admin.deleteTable(name);
            System.out.println("删除表成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /*按表名建表*/
    public static Boolean createTable(String tableName, Connection connection, String[] familyName) {
        Admin admin = null;
        try {
            admin = connection.getAdmin();
            if (!admin.isTableAvailable(TableName.valueOf(tableName))) {
                HTableDescriptor hbaseTable = new HTableDescriptor(TableName.valueOf(tableName));
                for (int i = 0; i < familyName.length; i++) {
                    hbaseTable.addFamily(new HColumnDescriptor(familyName[i]));
                }
                admin.createTable(hbaseTable);
            } else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (admin != null) {
                    admin.close();
                }

                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return true;
    }

    /*插入数据*/
    public static boolean putDatas(String tableName, String[] columnName, String[] lineDatas, Connection connection) throws IOException {

        //获取表对象
        Table table = connection.getTable(TableName.valueOf(tableName));
        //准备数据
        Put put = new Put(Bytes.toBytes(lineDatas[0]));
        for (int i = 1; i < columnName.length; i++) {
            String[] family_colName = columnName[i].split(":");
            String familyName = family_colName[0];
            String colName = family_colName[1];
            put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(colName), Bytes.toBytes(lineDatas[i]));
        }
        // 添加数据
        table.put(put);
        table.close();
        return true;
    }

    /*按表名删除表*/
    public static void deleteTable(String tableName, Connection connection) throws IOException {
        Admin admin = connection.getAdmin();
        // 设置表的状态为无效
        admin.disableTable(TableName.valueOf(tableName));

        //删除指定的表
        admin.deleteTable(TableName.valueOf(tableName));
    }

    /*获取前十条数据*/
    public static List<String> getTableDatalimit10(String tableName, Connection connection)  {

        Scan scan = new Scan();
        scan.setCaching(1);
        Filter filter = new PageFilter(10); //
        scan.setFilter(filter);
        List<String> results=new LinkedList<>();
        try{
            Table table = connection.getTable(TableName.valueOf(tableName));
            ResultScanner scanner = table.getScanner(scan);// 执行扫描查找
            int num = 0;
            Iterator<Result> res = scanner.iterator();// 返回查询遍历器
            while (res.hasNext()) {
                Result result = res.next();
                num++;
                results.add(result.getRow().toString());
                //System.out.println("key:" + new String(result.getRow()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 按表名读取HBASE结果
     *
     * @param tableName
     * @param connection
     * @return
     */
    public static Map<String, Object> getHBaseTableData(String tableName, Connection connection) {
        LinkedList<Object> hbaseDataList = new LinkedList<>();
        LinkedList<Object> dataMeta = new LinkedList<Object>();

        //存放最大最小值信息   key-列名  value -String[0]最大值  String[1]最小值
        LinkedHashMap<String, String[]> informationhashMap = new LinkedHashMap<>();

        //存放不同分类结果
        Set<Object> set = new HashSet<>();

        //存放列信息
        LinkedList<String> coulmn = new LinkedList<>();

        //调用读取hbase表名方法并按顺序返回结果集，key-行键  value -类型不同（Float,String）的结果集
        Map<Integer, List<Object>> Number = read(tableName, informationhashMap, set, coulmn, connection);

        //最终最大最小值结果（key-列名，value-最大值，最小值）
        LinkedHashMap<String, List<Float>> MaxAndMin = new LinkedHashMap<>();

        //插入数据结果到number中
        for (int key : Number.keySet()) {
            hbaseDataList.add(Number.get(key));
        }
        List<Float> matrixMaxMin = new LinkedList<>();
        if (tableName.endsWith("matrix")) {
            Float max = Float.MIN_VALUE;
            Float min = Float.MAX_VALUE;
            for (String key : informationhashMap.keySet()) {


                String Max = String.valueOf(Math.ceil(Float.valueOf(informationhashMap.get(key)[0])));
                ///取最小值的下界
                if (Float.valueOf(Max) > max) {
                    max = Float.valueOf(Max);
                }
                String Min = String.valueOf(Math.floor(Float.valueOf(informationhashMap.get(key)[1])));
                if (Float.valueOf(Min) < min) {
                    min = Float.valueOf(Min);
                }

            }
            matrixMaxMin.add(max);
            matrixMaxMin.add(min);
        }

        //解析key，将5_Species_str，5_Species_ll_float等形式解析，将并取最大值的上界和最小值的下界存到MaxAndMin中
        for (String key : informationhashMap.keySet()) {
            ///取最大值的上界
            String max = String.valueOf(Math.ceil(Float.valueOf(informationhashMap.get(key)[0])));
            ///取最小值的下界
            String min = String.valueOf(Math.floor(Float.valueOf(informationhashMap.get(key)[1])));
            List a = new LinkedList();
            a.add(Float.valueOf(max));
            a.add(Float.valueOf(min));
            String QualifiernewArray[] = key.split("_");
            StringBuffer Qualifiernew = new StringBuffer();
            if (QualifiernewArray.length == 3)
                Qualifiernew.append(QualifiernewArray[1]);
            else {
                for (int i = 1; i < QualifiernewArray.length - 1; i++) {
                    if (i == QualifiernewArray.length - 2) {
                        Qualifiernew.append(QualifiernewArray[i]);
                    }
                    else {
                        Qualifiernew.append(QualifiernewArray[i]).append("_");
                    }

                }
            }

            MaxAndMin.put(Qualifiernew.toString(), a);
        }
        dataMeta.add(coulmn);
        if (tableName.endsWith("matrix")){
            dataMeta.add(matrixMaxMin);
        }
        else {
            dataMeta.add(MaxAndMin);
        }

        dataMeta.add(set);

        Map<String, Object> result = new HashMap<>(3);
        result.put("TableName", tableName);
        result.put("DataMeta", dataMeta);
        result.put("Data", hbaseDataList);
        return result;
    }

    /**
     * 读Hbase库，并完成排序，读取数据，存储行名等操作。
     *
     * @param tablename
     * @param hashMap
     * @param set
     * @param coulmn
     * @return
     */
    public static TreeMap read(String tablename, LinkedHashMap hashMap, Set set, LinkedList coulmn, Connection connection) {
        TreeMap<Integer, List<Object>> map = new TreeMap<>();

        try {
            Table table = connection.getTable(TableName.valueOf(tablename));
            Scan scan = new Scan();
            ResultScanner rss = table.getScanner(scan);
            Boolean count = true;
            for (Result result : rss) {
                if (count == true) {
                    for (Cell cell : result.listCells()) {
                        String Qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                        String QualifiernewArray[] = Qualifier.split("_");
                        StringBuffer Qualifiernew = new StringBuffer();
                        if (QualifiernewArray.length == 3)
                            Qualifiernew.append(QualifiernewArray[1]);
                        else {
                            for (int i = 1; i < QualifiernewArray.length - 1; i++) {
                                if (i == QualifiernewArray.length - 2)
                                    Qualifiernew.append(QualifiernewArray[i]);
                                else {
                                    Qualifiernew.append(QualifiernewArray[i]).append("_");
                                }
                            }
                        }
                        coulmn.add(Qualifiernew.toString());

                        String[] maxmin = new String[2];
                        //maxmin[0]存最大值
                        maxmin[0] = String.valueOf(Float.MIN_VALUE);
                        // maxmin[1]存最小值
                        maxmin[1] = String.valueOf(Float.MAX_VALUE);

                        hashMap.put(Bytes.toString(CellUtil.cloneQualifier(cell)), maxmin);
                    }
                }
                String rowkeynew = Bytes.toString(result.getRow());
                int rowkey = Integer.valueOf(rowkeynew);

                List<Object> list = new ArrayList<>();
                for (Cell cell : result.listCells()) {
                    String cellvalue = Bytes.toString(CellUtil.cloneValue(cell));
                    String str = Bytes.toString(CellUtil.cloneQualifier(cell));
                    if (str.contains("y_hat")) {
                        if (str.endsWith("_str")) {
                            set.add(cellvalue);
                            list.add(cellvalue);
                        } else {
                            set.add(Float.valueOf(cellvalue));
                            list.add(Float.valueOf(cellvalue));
                        }
                    } else {
                        if (str.endsWith("_str")) {
                            list.add(cellvalue);
                        } else {
                            list.add(Float.valueOf(cellvalue));

                            String[] value = (String[]) hashMap.get(Bytes.toString(CellUtil.cloneQualifier(cell)));

                            if (Float.valueOf(value[0]) < Float.valueOf(cellvalue)) {
                                value[0] = String.valueOf(cellvalue);
                            } else if (Float.valueOf(value[1]) > Float.valueOf(cellvalue)) {
                                value[1] = String.valueOf(cellvalue);
                            }
                        }
                    }
                }
                map.put(rowkey, list);
                count = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String insetEpochInfo(int processId, List<EpochInfo> epochInfos, Connection connection){
        String[] family = {"f"};
        String tableName ="dl_process_log_"+String.valueOf(processId);
        if(createTable(tableName,family,connection)){
            for(int i=1;i<epochInfos.size()+1;i++){
                putEpochInfo(String.valueOf(i),tableName,epochInfos.get(i-1),connection);
            }
            return tableName;
        }
        return null;
    }

    public static boolean putEpochInfo(String rowKey,String tableName,EpochInfo epochInfo,Connection connection){
        try {
            // 设置rowkey
            Put put = new Put(Bytes.toBytes(rowKey));
            // 获取表
            Table table = connection.getTable(TableName.valueOf(tableName));
            put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("epoch"), Bytes.toBytes(String.valueOf(epochInfo.getEpoch())));
            put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("train_loss"), Bytes.toBytes(String.valueOf(epochInfo.getTrain_loss())));
            put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("test_loss"), Bytes.toBytes(String.valueOf(epochInfo.getTest_loss())));
            put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("learning_rate"), Bytes.toBytes(String.valueOf(epochInfo.getLearning_rate())));
            put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("summary"), Bytes.toBytes(epochInfo.getSummary()));
            put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("result_image"), Bytes.toBytes(epochInfo.getResult().getOrDefault("image","")));
            put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("result_text"), Bytes.toBytes(epochInfo.getResult().getOrDefault("text","")));
            put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("result_audio"), Bytes.toBytes(epochInfo.getResult().getOrDefault("audio","")));
            table.put(put);
            return true;
        }catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
    }

    public static boolean createTable(String tableName, String[] family,Connection connection){
        try{
            Admin admin = connection.getAdmin();
            HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
            for (int i = 0; i < family.length; i++) {
                desc.addFamily(new HColumnDescriptor(family[i]));
            }
            if (admin.tableExists(TableName.valueOf(tableName))) {
                return false;
            } else {
                admin.createTable(desc);
                return true;
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }

    }

}
