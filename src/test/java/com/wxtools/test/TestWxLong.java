package com.wxtools.test;

import com.wxtools.util.HttpClientUtil;

/**
 * Created by Administrator on 2017/10/18.
 */
public class TestWxLong {
    public static void main(String[] args) {
        String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=A2gIqLCgGHbF4cas21P3zaHa@qrticket_0&uuid=wd39BdhadA==&lang=zh_CN&scan=1508427792";
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
