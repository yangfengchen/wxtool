package com.wxtools.thread;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wxtools.entity.Contact;
import com.wxtools.entity.SendMsgJson;
import com.wxtools.entity.ThreadResult;
import com.wxtools.util.HttpClientUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by 37768 on 2017/12/2.
 */
public class HyContactsThread implements Callable<ThreadResult> {

    private String startUrl;
    private Contact contacts;
    private List<Cookie> cookies;
    private SendMsgJson sendMsgJson;
    private String Pass_ticket;


    public HyContactsThread(String startUrl, SendMsgJson sendMsgJson,Contact contacts,String Pass_ticket, List<Cookie> cookies){
        this.startUrl = startUrl;
        this.contacts = contacts;
        this.cookies = cookies;
        this.Pass_ticket = Pass_ticket;
        this.sendMsgJson = sendMsgJson;
    }

    @Override
    public ThreadResult call() throws Exception {
        String url = startUrl + "cgi-bin/mmwebwx-bin/webwxsendmsg?lang=zh_CN&pass_ticket="+Pass_ticket;
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        List<Cookie> newCookies = Lists.newArrayList();
        cookies.stream().forEach((Cookie cookie) ->{
            if("wxuin".equals(cookie.getName())){
                BasicClientCookie basicClientCookie
                        = new BasicClientCookie(cookie.getName()
                        ,cookie.getValue());
                newCookies.add(basicClientCookie);
            }
            newCookies.add(cookie);
        });

        BasicClientCookie basicClientCookie
                = new BasicClientCookie("login_frequency","2");
        newCookies.add(basicClientCookie);
        BasicClientCookie notifyCookie
                = new BasicClientCookie("MM_WX_NOTIFY_STATE","1");
        newCookies.add(notifyCookie);
        BasicClientCookie soundCookie
                = new BasicClientCookie("MM_WX_SOUND_STATE","1");
        newCookies.add(soundCookie);
        String data = gson.toJson(sendMsgJson);
       /* System.out.println("cookie:"+gson.toJson(newCookies));*/
        Map<String,Object> map = HttpClientUtil.httpPostJson(url,data,newCookies);
        System.out.println((String) map.get("html"));
        ThreadResult threadResult = new ThreadResult();
        threadResult.setCode("SUCCESS");
        threadResult.setContact(contacts);
        return threadResult;
    }
}
