package com.stdc.topology2d.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.topology2d.service.ICmpService;
import com.stdc.visual.auth.config.CacheConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cmp")
@ApiSupport(author = "dongbobo")
@Api(tags = "CMP接口对接")
@Slf4j
public class CmpController {

    @Autowired
    private ICmpService cmpService;

    @Autowired
    private CacheConfig cacheConfig;

    @Autowired
    private CacheManager cacheManager;

    @ApiOperation("查询配置版本")
    @RequestMapping(value = "/version", method = {RequestMethod.GET})
    public R version() {
        return R.data(cmpService.getVersion());
    }

    @ApiOperation("查询配置数据device-type文件")
    @RequestMapping(value = "/deviceType", method = {RequestMethod.GET})
    public R getDeviceType() {
        JSONObject data = cmpService.getDeviceType();
        return R.data(data);
    }

    @ApiOperation("查询配置数据device-corresponding文件")
    @RequestMapping(value = "/deviceCorresponding", method = {RequestMethod.GET})
    public R getDeviceCorresponding() {
        JSONArray data = cmpService.getDeviceCorresponding();
        return R.data(data);
    }

    @ApiOperation("刷新缓存")
    @RequestMapping(value = "/refresh", method = {RequestMethod.GET})
    public R refresh() {
        Cache cache = cacheManager.getCache("cmp");
        if (cache != null) {
            // 删除指定键的缓存
            cache.evict("deviceType");
            cache.evict("deviceCorresponding");
            //cache.clear();
        }
        return R.success("刷新缓存成功");
    }

    @ApiOperation("导出device-type文件")
    @RequestMapping(value = "/export/deviceType", method = {RequestMethod.POST})
    public R exportDeviceType() throws Exception {
        String path = cmpService.exportDeviceType();
        return R.data(path);
    }

    @ApiOperation("导出device-corresponding文件")
    @RequestMapping(value = "/export/deviceCorresponding", method = {RequestMethod.POST})
    public R exportDeviceCorresponding() throws Exception {
        String path = cmpService.exportDeviceCorresponding();
        return R.data(path);
    }

}

