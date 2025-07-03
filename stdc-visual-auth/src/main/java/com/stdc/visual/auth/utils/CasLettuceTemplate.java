package com.stdc.visual.auth.utils;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.concurrent.ExecutionException;

/**
 * @author: wang_jie
 * @data: 2023/1/10--16:04
 * @describe:
 */
@Component
public class CasLettuceTemplate {

//    @Autowired
//    private RedisAsyncCommands<String,String> casRedisCommands;

    /**
     * 通过 key 获取缓存 String 值
     * @param key
     * @return
     */
    public String getCacheString(final String key) {
        String V = null;
//        try {
//            V = casRedisCommands.get(key).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        return V;
    }

}
