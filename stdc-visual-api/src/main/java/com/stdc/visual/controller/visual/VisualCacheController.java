package com.stdc.visual.controller.visual;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author: wang_jie
 * @data: 2022/5/31--16:33
 * @describe: 数据缓存
 */
@RestController
@AllArgsConstructor
@RequestMapping("/custom-cache")
@ApiSupport(author = "wangjie",order = 12)
@Api(value = "数据缓存", tags = "数据缓存")
public class VisualCacheController {

    private final RedisTemplate redisTemplate;

    @Data
    @ApiModel(value = "cache", description = "缓存")
    static class KV{
        String key;
        String value;
    }

    /**
     * 新增自定义k-v
     */
    @PostMapping("/kv")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增自定义k-v", notes = "新增自定义k-v")
    public R<? extends Object> putKv(@ApiParam(value = "key", required = true) @RequestBody KV kv) {
        try {
            if ( StringUtil.isBlank(kv.key) || StringUtil.isBlank(kv.value) ){
                return R.fail("key-value不可为空");
            }
            redisTemplate.opsForValue().set(StdcVisualConstant.PROJECT_PREFIX + kv.key,kv.value);
        } catch (Exception e) {
            return R.fail("系统异常");
        }
        return R.success("新增成功");
    }

    /**
     * 获取自定义k-v
     */
    @GetMapping("/kv")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取自定义k-v", notes = "新增自定义k-v")
    public R<? extends Object> getKv(@ApiParam(value = "key", required = true) @RequestParam("key") String key) {
        Object res = null;
        try {
            if (StringUtil.isBlank(key)){
                return R.fail("key不可为空");
            }
            res = redisTemplate.opsForValue().get(StdcVisualConstant.PROJECT_PREFIX + key);
        } catch (Exception e) {
            return R.fail("系统异常");
        }
        return R.data(res);
    }

}
