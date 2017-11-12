package com.wxtools.test;

import com.google.gson.Gson;
import com.wxtools.entity.TestEntity;

import java.util.Random;

/**
 * Created by Administrator on 2017/10/19.
 */
public class Test {
    public static void main(String[] args) {
        String deviceId = "";
        int num = 0;
       for(int i = 0 ; i < 15 ; i++){
           num = (int)(Math.random()*10);
           deviceId += num;
       }
        System.out.println(deviceId);
        String str = "{\"Cover\":\"111\",\"digest\":\"123\",\"MPSubscribe\":\"12\"}";
        Gson gson = new Gson();
        TestEntity testEntity = gson.fromJson(str,TestEntity.class);
        System.out.println(gson.toJson(testEntity));

        System.out.println(0&8);
    }
}
