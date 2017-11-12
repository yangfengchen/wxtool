package com.wxtools.test;

/**
 * Created by Administrator on 2017/10/15.
 */
public class TestSplit {
    public static void main(String[] args) {
        String str = "window.QRLogin.code = 200; window.QRLogin.uuid = \"oYzZ5sr9Pw==\";";
        String[] result = str.split(";");
        String redirectUri = result[1].replace(" window.QRLogin.uuid = ", "").replace("\"", "").trim();;
        System.out.println(redirectUri);
    }
}
