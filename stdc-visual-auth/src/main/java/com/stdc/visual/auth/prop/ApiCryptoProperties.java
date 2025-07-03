package com.stdc.visual.auth.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: wang_jie
 * @data: 2022/7/8--15:16
 * @describe:
 */
@Data
@Component
@ConfigurationProperties(ApiCryptoProperties.PREFIX)
public class ApiCryptoProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "stdc.api.crypto";

    /**
     * 是否开启 api 签名
     */
    private Boolean enabled = Boolean.TRUE;

    /**
     * url的参数签名，传递的参数名。例如：/user?data=签名后的数据
     */
    private String paramName = "data";

    /**
     * aes 密钥
     */
    private String aesKey;

    /**
     * des 密钥
     */
    private String desKey;

    /**
     * rsa 私钥
     */
    private String rsaPrivateKey;
}
