package com.wxtools.controller;

import com.wxtools.service.WxLoginService;
import com.wxtools.vo.ReturnMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/10/15.
 */
@Controller
@EnableAutoConfiguration
public class WxTollsController {
    private Logger logger = LoggerFactory.getLogger(WxTollsController.class);

    @Resource
    private WxLoginService wxLoginService;

    @RequestMapping("/index")
    public String index(Model model){
        try{
            String uuid = wxLoginService.getUuid();
            String qrCodeUrl = wxLoginService.getWxLogin(uuid);
            model.addAttribute("qrCodeUrl",qrCodeUrl);
            model.addAttribute("uuid",uuid);
            return "login";
        }catch (Exception e){
            logger.error("访问首页出错!",e);
            e.printStackTrace();
        }
        return "error";
    }

    @RequestMapping("/login")
    @ResponseBody
    public ReturnMsg login(@RequestParam String uuid){
        ReturnMsg returnMsg = new ReturnMsg();
        try{
            String str = wxLoginService.redirectUrl(uuid);
            if("prep".equals(str)){
                returnMsg.setCode("302");
            }else if("handle".equals(str)){
                returnMsg.setCode("307");
                returnMsg.setMessage("请在手机上进行确认!");
            }else{
                returnMsg.setCode("200");
                returnMsg.setObj(str);
                returnMsg.setMessage("操作成功!");
            }
        }catch (Exception e){
            returnMsg.setCode("300");
            returnMsg.setMessage("获取微信授权失败,请刷新页面重试!");
            logger.error("访问登录页出错!",e);
            e.printStackTrace();
        }
        return returnMsg;
    }

    @RequestMapping("/homeIndex")
    public String homeIndex(@RequestParam(value="uuid", required=false) String uuid,
                            @RequestParam(value="url", required=false)  String url, HttpServletRequest request){
        try{
            wxLoginService.getWxStatus(uuid,url);
        }catch (Exception e){
            logger.error("获取微信信息出错!",e);
            e.printStackTrace();
        }
        return "logins";
    }
}
