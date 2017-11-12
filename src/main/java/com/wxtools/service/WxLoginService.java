package com.wxtools.service;

/**
 * Created by Administrator on 2017/10/15.
 */
public interface WxLoginService {
    //获取微信uuid
    String getUuid() throws Exception;

    String getQrCode(String uuid) throws Exception;

    String getWxLogin(String uuid) throws Exception;

    String redirectUrl(String uuid) throws Exception;

    void getWxStatus(String uuid,String redirectUrl) throws Exception;
}
