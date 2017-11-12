package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/11/2.
 * <error><ret>0</ret><message></message><skey>@crypt_32698e6f_7e6d4cfa46eed57eb1f8723477466a14</skey><wxsid>p4bsb4y6LhQP8SiS</wxsid><wxuin>792326215</wxuin><pass_ticket>Q4RvnA%2BKzWdJygWXBvavTCZmp5hjE7brVQ7TThjwXxinyZgoMG0jRbPiNnlSDRKd</pass_ticket><isgrayscale>1</isgrayscale></error>
 */
@Setter
@Getter
public class LoginParam {
    private String ret;
    private String message;
    private String skey;
    private String wxsid;
    private String wxuin;
    private String pass_ticket;
    private String isgrayscale;
}
