package com.stdc.visual.auth.annotation;

import com.stdc.visual.auth.utils.CryptoType;

import java.lang.annotation.*;

/**
 * @author: wang_jie
 * @data: 2022/7/8--15:22
 * @describe: 加密
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiEncrypt {

    /**
     * 加密类型
     *
     * @return 类型
     */
    CryptoType value();

    /**
     * 私钥，用于某些需要单独配置私钥的方法，没有时读取全局配置的私钥
     *
     * @return 私钥
     */
    String secretKey() default "";

}
