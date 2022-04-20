package com.bdilab.aiflow.common.utils;

import java.util.Random;

public class RandomStr {
    public final static char[] CHARS = new char[]{'a','b','c','d','e','f','g','h','i','j',
            'k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D',
            'E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    public final static int[] NUMS = new int[]{0,1,2,3,4,5,6,7,8,9};
    /**
     *  生成随机字符串
     *
     *  useChar:  true  使用字母abcd等生成字符串   false 使用数字123等生成字符串
     */
    public static String randomStr(int length,boolean useChar){
        if(length < 1){
            return null;
        }
        Random ran = new Random();
        StringBuilder bulider = new StringBuilder();
        for(int i = 0; i < length;i++){
            if(useChar){
                int ranIndex = ran.nextInt(CHARS.length);
                bulider.append(CHARS[ranIndex]);
            }else{
                int ranIndex = ran.nextInt(NUMS.length);
                if(i == 0 && ranIndex == 0){ //首位不允许0
                    ranIndex = 1;
                }
                bulider.append(NUMS[ranIndex]);
            }
        }
        return bulider.toString();
    }
}
