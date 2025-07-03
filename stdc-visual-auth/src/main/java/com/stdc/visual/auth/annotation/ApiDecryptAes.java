package com.stdc.visual.auth.annotation;

import com.stdc.visual.auth.utils.CryptoType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author: wang_jie
 * @data: 2022/7/8--15:19
 * @describe: 解密
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiDecrypt(CryptoType.AES)
public @interface ApiDecryptAes {
    /**
     * Alias for {@link ApiDecrypt#secretKey()}.
     *
     * @return {String}
     */
    @AliasFor(annotation = ApiDecrypt.class)
    String secretKey() default "";
}
