package com.wxtools.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wxtools.component.WxUrlComponent;
import com.wxtools.entity.*;
import com.wxtools.service.WxLoginService;
import com.wxtools.util.*;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
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
                }catch (Exception ex){

                }
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
        String startUrl;
        if(redirectUrl.startsWith("https://wx.qq.com")){
            startUrl = "https://wx.qq.com/";
        }else if(redirectUrl.startsWith("https://wx2.qq.com")){
            startUrl = "https://wx2.qq.com/";
        }else{
            throw new Exception("错误");
        }
        getzjHtml(startUrl,loginParam,cookies);
    }

    public void getzjHtml(String startUrl,LoginParam loginParam,List<Cookie> cookies) throws Exception {
        String url = startUrl + "cgi-bin/mmwebwx-bin/webwxinit?r="+(System.currentTimeMillis());
        url += "&pass_ticket="+loginParam.getPass_ticket()+"&lang=zh_CN";
        WebwxinitParam webwxinitParam = new WebwxinitParam();
        BaseRequest baseRequest = new BaseRequest(loginParam.getWxsid(),loginParam.getSkey(),loginParam.getWxuin());
        webwxinitParam.setBaseRequest(baseRequest);
        Gson gson = new GsonBuilder()
                //.setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        Map<String,Object> rsMap = HttpClientUtil.httpPostJson(url,gson.toJson(webwxinitParam),cookies);
        InitResponseJson initResponseJson = gson.fromJson((String)rsMap.get("html"),InitResponseJson.class);
        List<Cookie> responseCookies = (List<Cookie>)rsMap.get("cookie");
        webwxstatusnotify(startUrl,initResponseJson,loginParam,responseCookies);
        gethyHtml(startUrl,initResponseJson,loginParam,responseCookies);
    }

    //开启微信通知
    public void webwxstatusnotify(String startUrl,InitResponseJson initResponseJson,LoginParam loginParam,List<Cookie> cookies) throws Exception {
        String url = startUrl + "cgi-bin/mmwebwx-bin/webwxstatusnotify";
        WxStatusNotifyEntity wxStatusNotifyEntity = new WxStatusNotifyEntity();
        BaseRequest baseRequest = new BaseRequest(loginParam.getWxsid(),loginParam.getSkey(),loginParam.getWxuin());
        wxStatusNotifyEntity.setBaseRequest(baseRequest);
        wxStatusNotifyEntity.setFromUserName(initResponseJson.getUser().getUserName());
        wxStatusNotifyEntity.setToUserName(initResponseJson.getUser().getUserName());
        wxStatusNotifyEntity.setClientMsgId(DateUtil.getDateTime());
        Gson gson = new GsonBuilder()
                //.setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        Map<String,Object> rsMap = HttpClientUtil.httpPostJson(url,gson.toJson(wxStatusNotifyEntity),cookies);
    }

    //获取好友
    public void gethyHtml(String startUrl,InitResponseJson initResponseJson,LoginParam loginParam,List<Cookie> cookies) throws Exception {
        String url = startUrl + "cgi-bin/mmwebwx-bin/webwxgetcontact";
        url += "?seq=0&skey="+loginParam.getSkey()+"&r="+DateUtil.getDateTime();
        String html = HttpClientUtil.httpGet(url,cookies);
        //System.out.println("content:"+html);
        Gson gson = new GsonBuilder()
                //.setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        UserResponseJson userResponseJson = gson.fromJson(html,UserResponseJson.class);
        if(userResponseJson != null && userResponseJson.getMemberList() != null
                && userResponseJson.getMemberList().size() > 0){
            List<Contact> contacts = userResponseJson.getMemberList();
            /*contacts.stream().filter(contact -> contact.getVerifyFlag() == 0).forEach((Contact contact) ->
                    System.out.println(gson.toJson(contact))
            );*/
            List<Contact> hyContacts = contacts.stream()
                                        .filter((Contact contact) -> {
                                            if(contact.getVerifyFlag() == 0){
                                                return true;
                                            }else{
                                                return false;
                                            }
                                        })
                                        .collect(Collectors.toList());
            /*hyContacts.stream()
                    .forEach((Contact contact) -> System.out.println(contact.getNickName()
                    + "" + contact.getUserName()));*/
            if(hyContacts != null && hyContacts.size() > 0){

                WebwxsyncResult webwxsyncResult = webwxsync(startUrl,initResponseJson,loginParam,cookies);
                SynccheckParam synccheckParam = processSynccheckParam(startUrl,loginParam,webwxsyncResult,initResponseJson,webwxsyncResult.getCookies());
                synccheckParam = synccheck(synccheckParam);
                //System.out.println(synccheckParam.getHtml());
                if(synccheckParam.getHtml().indexOf("7") != -1){
                    twoWebwxsync(synccheckParam,synccheckParam.getCookies());
                    webwxsyncResult = twoWebwxsync(synccheckParam,synccheckParam.getCookies());
                    synccheckParam = processSynccheckParam(startUrl,loginParam,webwxsyncResult,initResponseJson,webwxsyncResult.getCookies());
                    synccheckParam = synccheck(synccheckParam);
                }

                processHyContacts(startUrl,hyContacts,synccheckParam.getCookies(),synccheckParam);
            }

        }
    }
    private SynccheckParam processSynccheckParam(String startUrl,LoginParam loginParam,WebwxsyncResult webwxsyncResult,InitResponseJson initResponseJson,List<Cookie> cookies){
        SynccheckParam synccheckParam = new SynccheckParam();
        synccheckParam.setStartUrl(startUrl);
        synccheckParam.setH(DateUtil.getDateTime());
        synccheckParam.setCookies(cookies);
        synccheckParam.setDeviceid(DeviceIdStrUtil.getDeviceIdStr(15));
        synccheckParam.setSid(loginParam.getWxsid());
        synccheckParam.setSkey(loginParam.getSkey());
        synccheckParam.setUin(loginParam.getWxuin());
        synccheckParam.setR(DateUtil.getDateTime()+500);
        synccheckParam.setPass_ticket(loginParam.getPass_ticket());
        synccheckParam.setIsgrayscale(loginParam.getIsgrayscale());
        synccheckParam.setUser(initResponseJson.getUser());
        synccheckParam.setSyncKeys(webwxsyncResult.getSyncResponseJson().getSyncCheckKey());
        List<SyncKeyMap> list = webwxsyncResult.getSyncResponseJson().getSyncCheckKey().getList();
        String syncKey = "";
        for(SyncKeyMap syncKeyMap : list){
            syncKey += syncKeyMap.getKey() +"_"+syncKeyMap.getVal()+"|";
        }
        if(syncKey.length() > 0){
            syncKey = syncKey.substring(0,syncKey.length() - 1);
        }
        synccheckParam.setSynckey(syncKey);
        return synccheckParam;
    }

    private SynccheckParam processTwoSynccheckParam(String startUrl,WebwxsyncResult webwxsyncResult,List<Cookie> cookies,SynccheckParam oldSynccheckParam){
        SynccheckParam synccheckParam = new SynccheckParam();
        synccheckParam.setStartUrl(startUrl);
        synccheckParam.setH(DateUtil.getDateTime());
        synccheckParam.setCookies(cookies);
        synccheckParam.setDeviceid(DeviceIdStrUtil.getDeviceIdStr(15));
        synccheckParam.setSid(oldSynccheckParam.getSid());
        synccheckParam.setSkey(oldSynccheckParam.getSkey());
        synccheckParam.setUin(oldSynccheckParam.getUin());
        synccheckParam.setR(DateUtil.getDateTime()+500);
        synccheckParam.setPass_ticket(oldSynccheckParam.getPass_ticket());
        synccheckParam.setIsgrayscale(oldSynccheckParam.getIsgrayscale());
        synccheckParam.setUser(oldSynccheckParam.getUser());
        synccheckParam.setSyncKeys(webwxsyncResult.getSyncResponseJson().getSyncCheckKey());
        List<SyncKeyMap> list = webwxsyncResult.getSyncResponseJson().getSyncCheckKey().getList();
        String syncKey = "";
        for(SyncKeyMap syncKeyMap : list){
            syncKey += syncKeyMap.getKey() +"_"+syncKeyMap.getVal()+"|";
        }
        if(syncKey.length() > 0){
            syncKey = syncKey.substring(0,syncKey.length() - 1);
        }
        synccheckParam.setSynckey(syncKey);
        return synccheckParam;
    }


    public SynccheckParam synccheck(SynccheckParam synccheckParam) throws Exception {
        synccheckParam.setR(synccheckParam.getR()+1);
        synccheckParam.setR(DateUtil.getDateTime());
        String url = synccheckParam.getStartUrl() +"cgi-bin/mmwebwx-bin/synccheck?r="+synccheckParam.getR()
                +"&skey="+ URLEncoder.encode(synccheckParam.getSkey(),"utf-8")+"&sid="+synccheckParam.getSid()
                +"&uin="+synccheckParam.getUin()+"&deviceid="+DeviceIdStrUtil.getDeviceIdStr(15)
                +"&synckey="+URLEncoder.encode(synccheckParam.getSynckey(),"utf-8")+"&_"+synccheckParam.getH();
        Map<String,Object> map = HttpClientUtil.httpGetByMap(url,synccheckParam.getCookies());
        synccheckParam.setCookies((List<Cookie>) map.get("cookie"));
        synccheckParam.setHtml((String)map.get("html"));
        return synccheckParam;
    }

    public WebwxsyncResult webwxsync(String startUrl,InitResponseJson initResponseJson,LoginParam loginParam,List<Cookie> cookies) throws Exception {
        String url = startUrl + "cgi-bin/mmwebwx-bin/webwxsync?sid="+loginParam.getWxsid()
                +"&skey="+loginParam.getSkey()+"&lang=zh_CN&pass_ticket="+loginParam.getPass_ticket();
        WebwxsyncParam webwxsyncParam = new WebwxsyncParam();
        BaseRequest baseRequest = new BaseRequest(loginParam.getWxsid(),loginParam.getSkey(),loginParam.getWxuin());
        webwxsyncParam.setBaseRequest(baseRequest);
        webwxsyncParam.setSyncKey(initResponseJson.getSyncKey());
        webwxsyncParam.setRr(0L-DateUtil.getDateTime());
        Gson gson = new GsonBuilder()
                //.setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        Map<String,Object> map = HttpClientUtil.httpPostJson(url,gson.toJson(webwxsyncParam),cookies);
        List<Cookie> newCookies = (List<Cookie>) map.get("cookie");
        SyncResponseJson syncResponseJson = gson.fromJson((String)map.get("html"),SyncResponseJson.class);
        WebwxsyncResult webwxsyncResult = new WebwxsyncResult();
        webwxsyncResult.setCookies(newCookies);
        webwxsyncResult.setLoginParam(loginParam);
        webwxsyncResult.setSyncResponseJson(syncResponseJson);
        return webwxsyncResult;
    }

    public WebwxsyncResult twoWebwxsync(SynccheckParam synccheckParam,List<Cookie> cookies) throws Exception {
        String url = synccheckParam.getStartUrl() + "cgi-bin/mmwebwx-bin/webwxsync?sid="+synccheckParam.getSid()
                +"&skey="+synccheckParam.getSkey()+"&lang=zh_CN&pass_ticket="+synccheckParam.getPass_ticket();
        WebwxsyncParam webwxsyncParam = new WebwxsyncParam();
        BaseRequest baseRequest = new BaseRequest(synccheckParam.getSid(),synccheckParam.getSkey(),synccheckParam.getUin());
        webwxsyncParam.setBaseRequest(baseRequest);
        webwxsyncParam.setSyncKey(synccheckParam.getSyncKeys());
        webwxsyncParam.setRr((0L - Long.valueOf(DeviceIdStrUtil.getRandomNum(9))));
        Gson gson = new GsonBuilder()
               // .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        Map<String,Object> map = HttpClientUtil.httpPostJson(url,gson.toJson(webwxsyncParam),cookies);
        List<Cookie> newCookies = (List<Cookie>) map.get("cookie");
        SyncResponseJson syncResponseJson = gson.fromJson((String)map.get("html"),SyncResponseJson.class);
        WebwxsyncResult webwxsyncResult = new WebwxsyncResult();
        webwxsyncResult.setCookies(newCookies);

        LoginParam loginParam = new LoginParam();
        loginParam.setPass_ticket(synccheckParam.getPass_ticket());
        loginParam.setSkey(synccheckParam.getSkey());
        loginParam.setWxsid(synccheckParam.getSid());
        loginParam.setWxuin(String.valueOf(synccheckParam.getUin()));

        webwxsyncResult.setLoginParam(loginParam);
        webwxsyncResult.setSyncResponseJson(syncResponseJson);

        return webwxsyncResult;
    }

    private List<Contact> processHyContacts(String startUrl,List<Contact> hyContacts,List<Cookie> cookies,SynccheckParam synccheckParam){
        //ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Contact> contactResult = Lists.newArrayList();
        try{
            List<Future<ThreadResult>> futures = Lists.newArrayList();
            for(Contact contact : hyContacts){
                SendMsgJson sendMsgJson = new SendMsgJson();

                BaseRequest baseRequest = new BaseRequest();
                baseRequest.setUin(synccheckParam.getUin());
                baseRequest.setSkey(synccheckParam.getSkey());
                baseRequest.setSid(synccheckParam.getSid());
                baseRequest.setDeviceID(DeviceIdStrUtil.getDeviceIdStr(15));

                Msg msg = new Msg();
                msg.setClientMsgId(DateUtil.getDateTime()+DeviceIdStrUtil.getRandomNum(4));
                msg.setLocalID(msg.getClientMsgId());
                msg.setContent("清理助手");
                msg.setType(1);
                msg.setFromUserName(synccheckParam.getUser().getUserName());
                msg.setToUserName(contact.getUserName());

                sendMsgJson.setBaseRequest(baseRequest);
                sendMsgJson.setScene(0);
                sendMsgJson.setMsg(msg);

                sendMsg(startUrl,sendMsgJson,contact,synccheckParam.getPass_ticket(),cookies,synccheckParam);
                /*HyContactsThread hyContactsThread = new HyContactsThread(startUrl,sendMsgJson,contact,synccheckParam.getPass_ticket(),cookies);
                Future<ThreadResult> future = executorService.submit(hyContactsThread);
                futures.add(future);*/
            }
            /*for(Future future : futures){
                ThreadResult thredResult = (ThreadResult)future.get();
                if("delete".equals(thredResult.getCode())){
                    contactResult.add(thredResult.getContact());
                }
            }*/
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
          //  executorService.shutdownNow();
        }
        return contactResult;
    }

    private SynccheckParam sendMsg(String startUrl, SendMsgJson sendMsgJson,Contact contacts,String Pass_ticket, List<Cookie> cookies,SynccheckParam synccheckParam) throws Exception {
        String url = startUrl + "cgi-bin/mmwebwx-bin/webwxsendmsg";//?lang=zh_CN&pass_ticket="+Pass_ticket
        Gson gson = new GsonBuilder()
                //.setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        List<Cookie> newCookies = Lists.newArrayList();
        BasicClientCookie bacommon = new BasicClientCookie("test","name");
        cookies.stream().forEach((Cookie cookie) ->{
            if("wxuin".equals(cookie.getName())){
                BasicClientCookie basicClientCookie
                        = new BasicClientCookie(cookie.getName()
                        ,cookie.getValue());
                basicClientCookie.setDomain(cookie.getDomain());
                basicClientCookie.setPath(cookie.getPath());
                basicClientCookie.setExpiryDate(cookie.getExpiryDate());
                newCookies.add(basicClientCookie);
                bacommon.setDomain(cookie.getDomain());
                bacommon.setPath(cookie.getPath());
                bacommon.setExpiryDate(cookie.getExpiryDate());
            }
            newCookies.add(cookie);
        });

        BasicClientCookie basicClientCookie
                = new BasicClientCookie("login_frequency","2");
        basicClientCookie.setDomain(bacommon.getDomain());
        basicClientCookie.setPath(bacommon.getPath());
        basicClientCookie.setExpiryDate(bacommon.getExpiryDate());
        newCookies.add(basicClientCookie);
        BasicClientCookie notifyCookie
                = new BasicClientCookie("MM_WX_NOTIFY_STATE","1");
        notifyCookie.setDomain(bacommon.getDomain());
        notifyCookie.setPath(bacommon.getPath());
        notifyCookie.setExpiryDate(bacommon.getExpiryDate());
        newCookies.add(notifyCookie);
        BasicClientCookie soundCookie
                = new BasicClientCookie("MM_WX_SOUND_STATE","1");
        soundCookie.setDomain(bacommon.getDomain());
        soundCookie.setPath(bacommon.getPath());
        soundCookie.setExpiryDate(bacommon.getExpiryDate());
        newCookies.add(soundCookie);
        String data = gson.toJson(sendMsgJson);

        Map<String,Object> map = HttpClientUtil.httpPostJson(url,data,newCookies);
        //System.out.println((String) map.get("html"));
       /* System.out.println("cookie:"+gson.toJson(newCookies));*/
      /* new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   Map<String,Object> map = HttpClientUtil.httpPostJson(url,data,newCookies);
                   System.out.println("消息返回"+(String) map.get("html"));
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }).start();*/
        WebwxsyncResult webwxsyncResult = twoWebwxsync(synccheckParam,synccheckParam.getCookies());
        synccheckParam.setSyncKeys(webwxsyncResult.getSyncResponseJson().getSyncCheckKey());
        webwxsyncResult = twoWebwxsync(synccheckParam,synccheckParam.getCookies());
        SynccheckParam newSynccheckParam = processTwoSynccheckParam(startUrl,webwxsyncResult,webwxsyncResult.getCookies(),synccheckParam);
        newSynccheckParam = synccheck(newSynccheckParam);
        if(webwxsyncResult.getSyncResponseJson().getAddMsgList() != null
                && webwxsyncResult.getSyncResponseJson().getAddMsgList().size() > 0){
            List<AddMsgList> addMsgLists = webwxsyncResult.getSyncResponseJson().getAddMsgList();
            List<AddMsgList> newAddMsgList = addMsgLists.stream().filter((AddMsgList addMsgList) -> {
                if(addMsgList.getToUserName().equals(sendMsgJson.getMsg().getToUserName())
                        && addMsgList.getFromUserName().equals(sendMsgJson.getMsg().getFromUserName())
                        && addMsgList.getContent().indexOf("开启了朋友验证，你还不是他（她）朋友。请先发送朋友验证请求，对方验证通过后，才能聊天。") != -1){
                    return true;
                }else{
                    return false;
                }
            }).collect(Collectors.toList());
            if(newAddMsgList != null && newAddMsgList.size() > 0){
                //修改备注
                updateReamrk(startUrl,contacts,newSynccheckParam,newCookies);
            }
        }
        return newSynccheckParam;
    }

    public void updateReamrk(String startUrl,Contact contact,SynccheckParam synccheckParam,List<Cookie> cookies) throws Exception {
        String url = startUrl + "cgi-bin/mmwebwx-bin/webwxoplog";
        UpdateReamrkEntity updateReamrkEntity = new UpdateReamrkEntity();

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setDeviceID(DeviceIdStrUtil.getDeviceIdStr(15));
        baseRequest.setSid(synccheckParam.getSid());
        baseRequest.setSkey(synccheckParam.getSkey());
        baseRequest.setUin(synccheckParam.getUin());

        updateReamrkEntity.setBaseRequest(baseRequest);
        updateReamrkEntity.setRemarkName("delete"+contact.getRemarkName());
        updateReamrkEntity.setUserName(contact.getUserName());

        Gson gson = new GsonBuilder()
                //.setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        BasicClientCookie soundCookie
                = new BasicClientCookie("refreshTimes","5");
        soundCookie.setDomain(cookies.get(0).getDomain());
        soundCookie.setPath(cookies.get(0).getPath());
        soundCookie.setExpiryDate(cookies.get(0).getExpiryDate());
        cookies.add(soundCookie);

        Map<String,Object> map = HttpClientUtil.httpPostJson(url,gson.toJson(updateReamrkEntity),cookies);
        //System.out.println(map.get("html"));
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
}
