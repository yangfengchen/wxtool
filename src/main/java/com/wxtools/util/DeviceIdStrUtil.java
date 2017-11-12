package com.wxtools.util;

/**
 * Created by Administrator on 2017/10/21.
 */
public class DeviceIdStrUtil {

    public static String getDeviceIdStr(int count){
        return "e"+getRandomNum(count);
    }

    public static String getRandomNum(int count){
        String deviceId = "";
        int num = 0;
        for(int i = 0 ; i < count ; i++){
            num = (int)(Math.random()*10);
            deviceId += num;
        }
        return deviceId;
    }
}
