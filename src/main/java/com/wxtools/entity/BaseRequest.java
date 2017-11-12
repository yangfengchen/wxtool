package com.wxtools.entity;

import com.wxtools.util.DeviceIdStrUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/10/21.
 */
@Setter
@Getter
public class BaseRequest {
    private String DeviceID;
    private String Sid;
    private String Skey;
    private String Uin;

    public BaseRequest(){}

    public BaseRequest(String Sid,String Skey,String Uin){
        this.DeviceID = DeviceIdStrUtil.getDeviceIdStr(15);
        this.Sid = Sid;
        this.Skey = Skey;
        this.Uin = Uin;
    }
}
