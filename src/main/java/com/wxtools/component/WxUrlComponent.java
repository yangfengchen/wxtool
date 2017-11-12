package com.wxtools.component;

import com.wxtools.util.DateUtil;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/10/15.
 */
@Component
public class WxUrlComponent {
    public String getEndUrl(){
        return "&_"+ DateUtil.getDateTime();
    }
}
