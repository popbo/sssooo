package com.stdc.visual.auth.prop.sso;

import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author: wang_jie
 * @data: 2022/10/26--14:07
 * @describe: 单点登录远程请求接口信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "stdc.security.sso.cas")
public class SsoConfigProperties {

    /**
     * 单点登录地址
     */
    private String serviceUrl;

    /**
     * 通过用户名获取用户-角色信息
     */
    private String roleUrl;


}
