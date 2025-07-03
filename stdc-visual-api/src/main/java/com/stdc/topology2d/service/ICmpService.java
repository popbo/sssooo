package com.stdc.topology2d.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.topology2d.entity.po.CmpData;
import com.stdc.topology2d.entity.po.CmpVersion;

public interface ICmpService extends IService<CmpData> {

    CmpVersion getVersion();

    JSONObject getDeviceType();

    JSONArray getDeviceCorresponding();

    String exportDeviceType() throws Exception;

    String exportDeviceCorresponding() throws Exception;

}
