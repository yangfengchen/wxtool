package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */
@Setter
@Getter
public class InitResponseJson {
    private BaseResponse BaseResponse;
    private String ChatSet;
    private long ClickReportInterval;
    private long ClientVersion;
    private List<Contact> ContactList;
    private long count;
    private long GrayScale;
    private long InviteStartCount;
    private long MPSubscribeMsgCount;
    //private List<MpSubscribeMsg> MPSubscribeMsgList;
    private String SKey;
    private SyncKey SyncKey;
    private Long SystemTime;
    private User User;
}
