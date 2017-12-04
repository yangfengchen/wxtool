package com.wxtools.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/12/1.
 */
@Controller
public class TestCookieController {

    @RequestMapping("/testCookie")
    @ResponseBody
    public String testCookie(HttpServletRequest request, HttpServletResponse response, Model model){
        Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
        System.out.println("x1");
        if (null==cookies) {
            System.out.println("没有cookie=========");
            Cookie cookie = new Cookie("name", "name");
            cookie.setMaxAge(365 * 24 * 60 * 60);
            cookie.setPath("/");
            System.out.println("已添加===============");
            response.addCookie(cookie);
        } else {
            System.out.println("xx");
            for(Cookie cookie : cookies){
                System.out.println("name:"+cookie.getName()+",value:"+ cookie.getValue());
            }
        }
        return "test";
    }
}
