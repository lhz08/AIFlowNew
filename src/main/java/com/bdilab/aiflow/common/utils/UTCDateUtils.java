package com.bdilab.aiflow.common.utils;


import cn.hutool.core.date.DateUtil;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UTCDateUtils {

    public static Date dateInvert(String dateStr) throws ParseException {
      //  String dataStr1="2022-04-18T07:40:00Z";
      //  dateStr = dateStr.replace("Z", "UTC");
        //需要转化的时间格式
        SimpleDateFormat dfN = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
        dfN.setTimeZone(TimeZone.getTimeZone("UTC"));
        //目标时间格式
        SimpleDateFormat dfO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //先转化为GMT格式
        Date date1 = dfN.parse(dateStr);
        //String类型目标格式
        String str1 = dfO.format(date1);
        //Date目标格式
        Date dateTime = DateUtil.parseDateTime(str1);
        return dateTime;
    }
    public static Date adddateInvert(Date dateStr,int delay) throws ParseException {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime((Date) dateStr);//将当前时间设置进去
        //根据需要去加减时间
        rightNow.add(Calendar.SECOND,+delay);
        return rightNow.getTime();
    }
   /* public static void main(String[] args) throws ParseException {
        //System.out.println(dateInvert("2022-04-18T07:40:00.000Z"));
        System.out.println((Date) new Date());

        System.out.println(adddateInvert((Date) new Date(),80));
    }*/

}
