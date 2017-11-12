package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */
@Setter
@Getter
public class WxLoginOne {
    LoginParam loginParam;
    List<Cookie> cookieList;
}
