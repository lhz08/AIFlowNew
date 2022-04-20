package com.bdilab.aiflow.common.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Decription TODO
 * @Author Humphrey
 * @Date 2019/9/25 19:15
 * @Version 1.0
 **/
public class DateUtils {
    /**
     * 获取当前日期字符串，格式为20190917
     * @return
     */
    public static String getCurrentDate(){
        return new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
    }

}
