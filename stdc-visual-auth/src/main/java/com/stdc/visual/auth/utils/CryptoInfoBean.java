package com.stdc.visual.auth.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: wang_jie
 * @data: 2022/7/8--15:21
 * @describe: 加密注解信息
 */
@Getter
@RequiredArgsConstructor
public class CryptoInfoBean {

    /**
     * 加密类型
     */
    private final CryptoType type;
    /**
     * 私钥
     */
    private final String secretKey;
}
