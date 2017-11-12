package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */
@Setter
@Getter
public class Webwxgetcontact {
    private BaseResponse BaseResponse;
    private long MemberCount;
    private List<Contact> MemberList;
    private long Seq;
}
