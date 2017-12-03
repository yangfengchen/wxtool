package com.wxtools.util;

import org.apache.http.cookie.Cookie;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.logging.SimpleFormatter;

/**
 * Created by 37768 on 2017/12/2.
 */
public class TestHtppClient {

    public static void main(String[] args) {
        System.out.println("xx");
        /*String url = "http://127.0.0.1:8080/testCookieIndex";
        try {
            List<Cookie> cookieList = HttpClientUtil.httpGetCookie(url);
            url = "http://127.0.0.1:8080/testCookie";
            Map<String,Object> map = HttpClientUtil.httpGetByMap(url,cookieList);
            List<Cookie> cookies = (List<Cookie>) map.get("cookie");
            if(cookies != null && cookies.size() > 0){
                System.out.println("xx");
            }else{
                System.out.println("xx11");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        System.out.println(DateUtil.getDateTime() << 4);
        //15122856863790707
        //1512286045320
        System.out.println(DateUtil.getDateTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(new Date(1512286045320L));
    }

}
