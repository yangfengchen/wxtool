package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by 37768 on 2017/12/3.
 */
@Setter
@Getter
public class UpdateReamrkEntity {
    private String UserName;
    private long CmdId = 2;
    private String RemarkName;
    private BaseRequest BaseRequest;
}
