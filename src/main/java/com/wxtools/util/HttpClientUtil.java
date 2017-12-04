package com.wxtools.util;

import com.google.common.collect.Maps;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/15.
 */
public class HttpClientUtil {
    static {
        System.setProperty("jsse.enableSNIExtension", "false");
    }
    private static  int SOCKET_TIMEOUT = 30000;
    private static  int CONNECTION_TIMEOUT = 50000;
    private static  int CONNECTION_REQUEST_TIMEOUT = 50000;

    public static String httpGet(String url,List<Cookie> cookieList) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();
        CookieStore cookieStore = null;
        if(cookieList != null){
            cookieStore = setCookieParam(cookieList);
            context.setCookieStore(cookieStore);
            httpclient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();
            //.setDefaultRequestConfig(globalConfig)
        }
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
            httpget.setConfig(getRequestConfit());
            CloseableHttpResponse response = httpclient.execute(httpget);
            //System.out.println(response.toString());
            HttpEntity httpEntity = response.getEntity();
            if(cookieStore != null){
                cookieStore.getCookies().stream().forEach((Cookie cookie) -> System.out.println(cookie.toString()));
            }
            if(httpEntity != null){
                return EntityUtils.toString(httpEntity,"UTF-8");
            }else{
                throw new Exception("请求出错,请检查网络!");
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String,Object> httpGetOneByMap(String url) throws Exception {
        Map<String,Object> rsMap = Maps.newHashMap();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        BasicCookieStore cookieStore  =  new BasicCookieStore();
        try {
            httpclient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
            httpget.setConfig(getRequestConfit());
            CloseableHttpResponse response = httpclient.execute(httpget);
            HttpEntity httpEntity = response.getEntity();
            if(cookieStore != null){
                cookieStore.getCookies().stream().forEach((Cookie cookie) -> System.out.println(cookie.toString()));
                rsMap.put("cookie",cookieStore.getCookies());
            }
            if(httpEntity != null){
                rsMap.put("html",EntityUtils.toString(httpEntity,"UTF-8"));
                return rsMap;
            }else{
                throw new Exception("请求出错,请检查网络!");
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String,Object> httpGetByMap(String url, List<Cookie> cookieList) throws Exception {
        Map<String,Object> rsMap = Maps.newHashMap();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        BasicCookieStore cookieStore  = null;
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();
        if(cookieList != null){
            cookieStore = setCookieParam(cookieList);

            context.setCookieStore(cookieStore);
            httpclient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();
        }
        try {

            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
            httpget.setConfig(getRequestConfit());
            CloseableHttpResponse response = httpclient.execute(httpget,context);
            //System.out.println(response.toString());
            HttpEntity httpEntity = response.getEntity();
            if(cookieStore != null){
                cookieStore.getCookies().stream().forEach((Cookie cookie) -> System.out.println(cookie.toString()));
                rsMap.put("cookie",cookieStore.getCookies());
            }
            if(httpEntity != null){
                rsMap.put("html",EntityUtils.toString(httpEntity,"UTF-8"));
                return rsMap;
            }else{
                throw new Exception("请求出错,请检查网络!");
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static List<Cookie> httpGetCookie(String url) throws Exception {
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
            httpget.setConfig(getRequestConfit());
            CloseableHttpResponse response = httpclient.execute(httpget);
            //System.out.println(response.toString());
            HttpEntity httpEntity = response.getEntity();
            //System.out.println("返回参数:"+EntityUtils.toString(httpEntity,"UTF-8"));
            EntityUtils.consume(httpEntity);
            /*HttpUriRequest login = RequestBuilder.post()
                    .setUri(new URI("https://wx.qq.com/"))
                    .build();
            CloseableHttpResponse response2 = httpclient.execute(login);
            HttpEntity entity = response2.getEntity();
            System.out.println("Login form get: " + response2.getStatusLine());

            EntityUtils.consume(entity);*/
            return cookieStore.getCookies();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String,Object> httpPostJson(String url,String json,List<Cookie> cookies) throws Exception {
        Map<String,Object> rsMap = Maps.newHashMap();
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();

        BasicCookieStore cookieStore =setCookieParam(cookies);
        context.setCookieStore(cookieStore);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            HttpPost httppost = new HttpPost(url);
            httppost.setConfig(getRequestConfit());
            httppost.setHeader("Content-Type","application/json;charset=UTF-8");
            StringEntity se = new StringEntity(json, Charset.forName("utf-8"));
            se.setContentEncoding("utf-8");
            //System.out.println("参数:"+json);
            httppost.setEntity(se);
            CloseableHttpResponse response = httpclient.execute(httppost,context);
            //System.out.println(response.toString());
            HttpEntity httpEntity = response.getEntity();

            if(httpEntity != null){
                rsMap.put("html",EntityUtils.toString(httpEntity,"UTF-8"));
                rsMap.put("cookie",cookieStore.getCookies());
                return rsMap;
            }else{
                throw new Exception("请求出错,请检查网络!");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static RequestConfig getRequestConfit(){
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .build();
        return requestConfig;
    }

    public static BasicCookieStore setCookieParam(List<Cookie> cookieList){
        BasicCookieStore cookieStore = new BasicCookieStore();
        if(cookieList != null && cookieList.size() > 0){
            cookieList.stream()
                    .distinct()
                    .forEach((Cookie cookie) -> cookieStore.addCookie(cookie));
        }
        return cookieStore;
    }
}
