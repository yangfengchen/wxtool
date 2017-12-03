package com.wxtools.entity;

import com.fasterxml.jackson.databind.deser.Deserializers;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by 37768 on 2017/12/2.
 */
@Setter
@Getter
public class UserResponseJson {
    private BaseResponse BaseResponse;
    private Long MemberCount;
    private List<Contact> MemberList;
    private Long Seq;
}
