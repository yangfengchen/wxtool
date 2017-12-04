package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by 37768 on 2017/12/2.
 */
@Setter
@Getter
public class SyncResponseJson {
    private BaseResponse BaseResponse;
    private long AddMsgCount;
    private SyncKey SyncKey;
    private SyncKey SyncCheckKey;
    private List<AddMsgList> AddMsgList;
}
