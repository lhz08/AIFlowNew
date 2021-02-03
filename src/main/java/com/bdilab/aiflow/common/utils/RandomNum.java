package com.bdilab.aiflow.common.utils;

import java.util.Random;

/**
 * @author smile
 * @data 2020/11/28 16:11
 **/
public class RandomNum {

    public static int  generateRandomNum() {
        Random random = new Random();
        int ends = random.nextInt(99);
        //如果不足两位，前面补0
        return ends;
    }
}
