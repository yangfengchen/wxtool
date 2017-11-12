package com.wxtools.test;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.wxtools.util.HttpClientUtil;

/**
 * Created by Administrator on 2017/10/16.
 */
public class LoginTest {
    public static void main(String[] args) {
        try{
            String uuid = "YeQzE5EzdA==";
            String tip = "1";
            while(true){
                String urlString = "http://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login?tip=%s&uuid=%s&_=%s";
                urlString = String.format(urlString, tip, uuid, System.currentTimeMillis());
                try{
                    String html = HttpClientUtil.httpGet(urlString,null);
                    System.out.println("==============");
                    System.out.println(html);
                    System.out.println("==============");
                    Thread.sleep(1000);
                }catch (Exception ex){}
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
