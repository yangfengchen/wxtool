package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/10/20.
 */
@Setter
@Getter
public class User {
    private long AppAccountFlag;
    private long ContactFlag;
    private long HeadImgFlag;
    private String HeadImgUrl;
    private long HideInputBarFlag;
    private String NickName;
    private String PYQuanPin;
    private String PYInitial;
    private String ReamrkName;
    private String RemarkPYInitial;
    private long Sex;
    private String Signature;
    private long SnsFlag;
    private long StarFriend;
    private long Uin;
    private String UserName;
    private long VerifyFlag;
    private long WebWxPluginSwitch;
}
