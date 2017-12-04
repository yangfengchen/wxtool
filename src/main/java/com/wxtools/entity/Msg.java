package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by 37768 on 2017/12/2.
 */
@Setter
@Getter
public class Msg {
    private int Type;
    private String FromUserName;
    private String Content;
    private String ToUserName;
    private String LocalID;
    private String ClientMsgId;
}
