package com.wxtools.util;

import com.wxtools.entity.LoginCookie;
import org.apache.http.cookie.Cookie;
import java.util.List;

public class CommonUtil {

	public static LoginCookie parseLoginResult(List<Cookie> cookies) throws Exception{
		try{
			LoginCookie loginCookie = new LoginCookie();
			cookies.stream()
					.forEach((Cookie cookie) -> {
						switch (cookie.getName()){
							case "mm_lang":
								loginCookie.setMmLang(cookie.getValue());
								break;
							case "webwx_auth_ticket":
								loginCookie.setWebwxAuthTicket(cookie.getValue());
								break;
							case "webwx_data_ticket":
								loginCookie.setWebwxDataTicket(cookie.getValue());
								break;
							case "webwxuvid":
								loginCookie.setWebwxuvid(cookie.getValue());
								break;
							case "wxloadtime":
								loginCookie.setWxloadtime(cookie.getValue());
								break;
							case "wxsid":
								loginCookie.setWxsid(cookie.getValue());
								break;
							case "wxuin":
								loginCookie.setWxuin(cookie.getValue());
								break;
							case "last_wxuin":
								loginCookie.setLastWxuin(cookie.getValue());
								break;
						}
					});
			if(loginCookie.getLastWxuin() != null && loginCookie.getLastWxuin().length() > 0){
					loginCookie.setWxuin("xuin="+loginCookie.getWxuin());
			}
			loginCookie.setPath("/");
			loginCookie.setDomain("wx2.qq.com");
			loginCookie.setVersion("0");
			return loginCookie;
		}catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	
}
