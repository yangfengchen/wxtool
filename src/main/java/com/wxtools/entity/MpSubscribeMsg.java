package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */
@Setter
@Getter
public class MpSubscribeMsg {
    private long MPArticleCount;
    //private List<Mparticle> MPArticleList;
    private String NickName;
    private Long Time;
    private String UserName;

}
