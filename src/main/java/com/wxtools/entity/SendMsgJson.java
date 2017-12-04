package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by 37768 on 2017/12/2.
 */
@Setter
@Getter
public class SendMsgJson {
    private BaseRequest BaseRequest;
    private long Scene;
    private Msg Msg;
}
