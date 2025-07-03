package com.stdc.visual.auth.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;

/**
 * @author: wang_jie
 * @data: 2022/7/8--15:38
 * @describe: 解密信息输入流
 */
@Getter
@RequiredArgsConstructor
public class DecryptHttpInputMessage implements HttpInputMessage {
    private final InputStream body;
    private final HttpHeaders headers;
}

