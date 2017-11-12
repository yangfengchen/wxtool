package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by Administrator on 2017/10/24.
 */
@Setter
@Getter
public class InitResponseObj {
    private InitResponseJson initResponseJson;
    private List<Cookie> cookieList;
}
