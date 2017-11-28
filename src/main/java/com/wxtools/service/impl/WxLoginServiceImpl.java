package com.wxtools.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.wxtools.component.WxUrlComponent;
import com.wxtools.entity.*;
import com.wxtools.service.WxLoginService;
import com.wxtools.util.*;
import org.apache.http.cookie.Cookie;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import sun.net.www.http.HttpClient;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/10/15.
 */
@Service
public class WxLoginServiceImpl implements WxLoginService{

    @Resource
    private WxUrlComponent wxUrlComponent;

    @Override
    public String getUuid() throws Exception {
        String url = "https://login.wx.qq.com/jslogin?appid=wx782c26e4c19acffb&fun=new&lang=zh_CN";
        url += wxUrlComponent.getEndUrl();
        String resultStr = HttpClientUtil.httpGet(url,null);
        String[] result = resultStr.split(";");
        String resultUuid = result[1].replace(" window.QRLogin.uuid = ", "").replace("\"", "").trim();;
        return resultUuid;
    }

    @Override
    public String getQrCode(String uuid) throws Exception{
        if (uuid == null || "".equals(uuid)) {
            return null;
        }
        String qrCodeUrl = "https://login.weixin.qq.com/qrcode/" + uuid;
        return qrCodeUrl;
    }

    @Override
    public String getWxLogin(String uuid) throws Exception {
        String qrCodeUrl = getQrCode(uuid);
        return qrCodeUrl;
    }

    @Override
    public String redirectUrl(String uuid) throws Exception {
        try{
                String urlString = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login?tip=1&uuid=%s&_=%s";
                urlString = String.format(urlString, uuid, DateUtil.getDateTime());
                try{
                    String html = HttpClientUtil.httpGet(urlString,null);
                    String[] result = html.split(";");
                    if (result[0].replace("window.code=", "").equals("201")) {
                        return "handle";
                    }else if (result[0].replace("window.code=", "").equals("200")) {
                        return result[1].replace("window.redirect_uri=", "").replace("\"", "").trim();
                    }
                }catch (Exception ex){}
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "prep";
    }

    @Override
    public void getWxStatus(String uuid, String redirectUrl) throws Exception {
        WxLoginOne wxLoginOne = getWxLoginParam(redirectUrl);
        LoginParam loginParam = wxLoginOne.getLoginParam();
        List<Cookie> cookies = wxLoginOne.getCookieList();
        getzjHtml(loginParam,cookies);
    }

    public void getzjHtml(LoginParam loginParam,List<Cookie> cookies) throws Exception {
        String url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r="+(System.currentTimeMillis());
        url += "&pass_ticket="+loginParam.getPass_ticket()+"&lang=zh_CN";
        WebwxinitParam webwxinitParam = new WebwxinitParam();
        BaseRequest baseRequest = new BaseRequest(loginParam.getWxsid(),loginParam.getSkey(),loginParam.getWxuin());
        webwxinitParam.setBaseRequest(baseRequest);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        Map<String,Object> rsMap = HttpClientUtil.httpPostJson(url,gson.toJson(webwxinitParam),cookies);
        InitResponseJson initResponseJson = gson.fromJson((String)rsMap.get("html"),InitResponseJson.class);
        List<Cookie> responseCookies = (List<Cookie>)rsMap.get("cookie");
        if(initResponseJson.getBaseResponse().getRet() != 0){
            url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r="+(System.currentTimeMillis());
            url += "&pass_ticket="+loginParam.getPass_ticket()+"&lang=zh_CN";
            rsMap = HttpClientUtil.httpPostJson(url,gson.toJson(webwxinitParam),cookies);
            initResponseJson = gson.fromJson((String)rsMap.get("html"),InitResponseJson.class);
            responseCookies = (List<Cookie>)rsMap.get("cookie");
        }
        System.out.println("初始:"+(String)rsMap.get("html"));
        webwxstatusnotify(initResponseJson,loginParam,cookies);
        gethyHtml(loginParam,responseCookies);
    }

    //开启微信通知
    public void webwxstatusnotify(InitResponseJson initResponseJson,LoginParam loginParam,List<Cookie> cookies) throws Exception {
        String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify";
        WxStatusNotifyEntity wxStatusNotifyEntity = new WxStatusNotifyEntity();
        BaseRequest baseRequest = new BaseRequest(loginParam.getWxsid(),loginParam.getSkey(),loginParam.getWxuin());
        wxStatusNotifyEntity.setBaseRequest(baseRequest);
        wxStatusNotifyEntity.setFromUserName(initResponseJson.getUser().getUserName());
        wxStatusNotifyEntity.setToUserName(initResponseJson.getUser().getUserName());
        wxStatusNotifyEntity.setClientMsgId(DateUtil.getDateTime());
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        Map<String,Object> rsMap = HttpClientUtil.httpPostJson(url,gson.toJson(wxStatusNotifyEntity),cookies);
        System.out.println("初始化:"+(String)rsMap.get("html"));
    }

    public void gethyHtml(LoginParam loginParam,List<Cookie> cookies) throws Exception {
        String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact";
        url += "?seq=0&skey="+loginParam.getSkey()+"&r="+DateUtil.getDateTime();
        System.out.println(url);
        String html = HttpClientUtil.httpGet(url,cookies);
        System.out.println("content:"+html);
    }

    public WxLoginOne getWxLoginParam(String redirectUrl) throws Exception {
        String url = redirectUrl+"&fun=new&version=v2";
        Map<String,Object> map= HttpClientUtil.httpGetOneByMap(url);
        String html = (String)map.get("html");
        List<Cookie> cookies = (List<Cookie>) map.get("cookie");
        LoginParam loginParam = presHtml(html);
        WxLoginOne wxLoginOne = new WxLoginOne();
        wxLoginOne.setCookieList(cookies);
        wxLoginOne.setLoginParam(loginParam);
        return wxLoginOne;
    }

    public LoginParam presHtml(String html) throws DocumentException {
        Document document = DocumentHelper.parseText(html);
        Element root = document.getRootElement();
        Object obj = null;
        try {
            obj=LoginParam.class.newInstance();//创建对象
            List<Element> properties=root.elements();
            for(Element pro:properties){
                String propertyname=pro.getName();
                String propertyvalue=pro.getText();
                propertyname = propertyname.substring(0,1).toUpperCase()+propertyname.substring(1);
                Method m = obj.getClass().getMethod("set"+propertyname,String.class);
                m.invoke(obj,propertyvalue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoginParam loginParam = null;
        if(obj != null && obj instanceof LoginParam){
            loginParam = (LoginParam) obj;
        }
        return loginParam;
    }

    /*
 List<Cookie> cookieList = HttpClientUtil.httpGetCookie(redirectUrl+"&fun=new&version=v2");
        System.out.println("一次cookie"+gson.toJson(cookieList));
        cookieList = getWebwxstatreport(cookieList);
        LoginCookie loginCookie = CommonUtil.parseLoginResult(cookieList);
        InitResponseObj initResponseObj = getResponseJson(cookieList,loginCookie);
        InitResponseJson initResponseJson = initResponseObj.getInitResponseJson();
        System.out.println("initResponse"+gson.toJson(initResponseJson));
        Webwxgetcontact webwxgetcontact = webwxgetcontact(initResponseJson.getSKey(),initResponseObj.getCookieList());
        System.out.println(gson.toJson(webwxgetcontact));


    private List<Cookie> getWebwxstatreport(List<Cookie> cookieList) throws Exception {
        //String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxstatreport?fun=new";
        String url = "https://wx2.qq.com";
        Map<String,Object> rsMap = HttpClientUtil.httpGetByMap(url,cookieList);
        return (List<Cookie>) rsMap.get("cookie");
    }

    private Webwxgetcontact webwxgetcontact(String skey,List<Cookie> cookieList) throws Exception {
        Gson gson = new Gson();
        String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact?r=%s&seq=0&skey=%s";
        url = String.format(url,DeviceIdStrUtil.getRandomNum(13),skey);
        String html = HttpClientUtil.httpGet(url,cookieList);
        //System.out.println("Webwxgetcontact页面"+gson.toJson(html));
        Webwxgetcontact webwxgetcontact = gson.fromJson(html,Webwxgetcontact.class);
        *//*webwxgetcontact.getMemberList()
                .forEach((Contact contact) -> System.out.println(gson.toJson(contact)));*//*
        JdbcUtil.insertDb(webwxgetcontact.getMemberList());
        return webwxgetcontact;
    }

    private InitResponseObj getResponseJson(List<Cookie> cookieList,LoginCookie loginCookie) throws Exception {
        WebwxinitParam webwxinitParam = new WebwxinitParam();
        BaseRequest baseRequest = new BaseRequest(loginCookie.getWxsid(),"",loginCookie.getWxuin());
        webwxinitParam.setBaseRequest(baseRequest);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r=-"+DeviceIdStrUtil.getRandomNum(10);
        Map<String,Object> rsMap = HttpClientUtil.httpPostJson(url,gson.toJson(webwxinitParam),cookieList);
        System.out.println("InitResponseJson页面"+gson.toJson((String)rsMap.get("html")));
        InitResponseJson initResponseJson = gson.fromJson((String)rsMap.get("html"),InitResponseJson.class);
        InitResponseObj initResponseObj = new InitResponseObj();
        initResponseObj.setInitResponseJson(initResponseJson);
        initResponseObj.setCookieList((List<Cookie>)rsMap.get("cookie"));
        return initResponseObj;
    }*/

}
