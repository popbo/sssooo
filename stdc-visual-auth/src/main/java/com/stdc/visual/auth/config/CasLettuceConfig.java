package com.stdc.visual.auth.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

/**
 * @author: wang_jie
 * @data: 2023/1/10--15:51
 * @describe: 配置连接统一认证 redis 库的lettuce客户端
 */
//@Configuration
public class CasLettuceConfig {

//    @Bean(name = "casRedisCommands")
    public RedisAsyncCommands<String,String> CasRedisCommands(){
        RedisURI redisuri = RedisURI.builder()
//                .withHost("10.255.111.800000").withPort(6379)
//                .withPassword("stdc@000925")
                .build();
        RedisClient casRedisClient = RedisClient.create(redisuri);
        //获取异步操作命令工具
        RedisAsyncCommands<String, String> async = casRedisClient.connect().async();
        return async;
    }

}
