package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class LoginCookie{
	private String mmLang;
	private String webwxAuthTicket;
	private String webwxDataTicket;
	private String webwxuvid;
	private String wxloadtime;
	private String wxsid;
	private String wxuin;
	private String expiry;
	private String path;
	private String domain;
	private String version;
	private String lastWxuin;
}
