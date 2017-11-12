package com.wxtools.test;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.wxtools.entity.LoginParam;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2017/11/2.
 */
public class Test1 {
    public static void main(String[] args) throws DocumentException {
        String xml = "<error><ret>0</ret><message></message><skey>@crypt_32698e6f_7e6d4cfa46eed57eb1f8723477466a14</skey><wxsid>p4bsb4y6LhQP8SiS</wxsid><wxuin>792326215</wxuin><pass_ticket>Q4RvnA%2BKzWdJygWXBvavTCZmp5hjE7brVQ7TThjwXxinyZgoMG0jRbPiNnlSDRKd</pass_ticket><isgrayscale>1</isgrayscale></error>";
        presHtml(xml);
    }

    public static void presHtml(String html) throws DocumentException {
        Document document = DocumentHelper.parseText(html);
        Element root = document.getRootElement();
        Object obj = null;
        try {
            obj=LoginParam.class.newInstance();//创建对象
            List<Element> properties=root.elements();
            for(Element pro:properties){
                String propertyname=pro.getName();
                String propertyvalue=pro.getText();
                propertyname = propertyname.substring(0,1).toUpperCase()+propertyname.substring(1);
                Method m = obj.getClass().getMethod("set"+propertyname,String.class);
                m.invoke(obj,propertyvalue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(obj != null && obj instanceof LoginParam){
            LoginParam loginParam = (LoginParam) obj;
            System.out.println(loginParam.getPass_ticket());
        }


    }
}
