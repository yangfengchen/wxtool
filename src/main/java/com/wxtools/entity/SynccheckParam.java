package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by 37768 on 2017/12/2.
 */
@Setter
@Getter
public class SynccheckParam {
    private String startUrl;
    private long r;
    private String skey;
    private String sid;
    private String uin;
    private String deviceid;
    private String synckey;
    private long h;
    private List<Cookie> cookies;
    private String pass_ticket;
    private String isgrayscale;
    private User user;
    private SyncKey syncKeys;
    private String html;
}
