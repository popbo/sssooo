package com.stdc.visual.auth.annotation;

import java.lang.annotation.*;

/**
 * @author: wang_jie
 * @data: 2022/6/6--14:26
 * @describe: 检验权限是否开启
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthEnable {
}
