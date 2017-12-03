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
public class WebwxsyncResult {
    private SyncResponseJson SyncResponseJson;
    private LoginParam loginParam;
    private List<Cookie> cookies;
    private String html;
}
