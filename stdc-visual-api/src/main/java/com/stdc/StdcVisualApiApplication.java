package com.stdc;

import Aladdin.Hasp;
import Aladdin.HaspStatus;
import com.stdc.core.tool.annotion.StdcAutoApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author: wang_jie
 * @data: 2022/5/12--17:12
 * @describe: 可视化启动类
 */
@Slf4j
@StdcAutoApplication
public class StdcVisualApiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(StdcVisualApiApplication.class, args);
        // 使用scheduleAtFixedRate方法每小时验证license执行一次任务
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()->{
//            log.info("Execute license verification program every hour ->>>>>>>>>>>");
//            AuthLicenseUtil.checkAuthLicenseStatus(configurableApplicationContext);
//        }, 0, 1, TimeUnit.HOURS);
    }

}
