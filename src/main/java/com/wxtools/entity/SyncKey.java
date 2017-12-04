package com.wxtools.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/20.
 */
@Setter
@Getter
public class SyncKey {
    private long Count;
    private List<SyncKeyMap> List;
}
