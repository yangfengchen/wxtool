package com.wxtools.test;


import com.wxtools.util.HttpClientUtil;

import java.util.Date;

/**
 * Created by Administrator on 2017/10/15.
 */
public class IndexTest {
    public static void main(String[] args) {
        String url = "https://login.wx.qq.com/jslogin?appid=wx782c26e4c19acffb&fun=new&lang=zh_CN&_="+(new Date().getTime());
        try {
            String html = HttpClientUtil.httpGet(url,null);
            System.out.println("==========");
            System.out.println(html);
            System.out.println("============");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
