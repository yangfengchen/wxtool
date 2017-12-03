package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by 37768 on 2017/12/3.
 */
@Setter
@Getter
public class AddMsgList {
    private String MsgId;
    private String FromUserName;
    private String ToUserName;
    private String MsgType;
    private String Content;
}
