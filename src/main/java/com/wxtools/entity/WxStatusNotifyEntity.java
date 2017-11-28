package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by 37768 on 2017/11/26.
 */
@Setter
@Getter
public class WxStatusNotifyEntity {
    private BaseRequest BaseRequest;
    private String Code = "3";
    private String FromUserName;
    private String ToUserName;
    private long ClientMsgId;
}
