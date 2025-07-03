package com.stdc.visual.auth.config;

import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.utils.ClassUtil;
import com.stdc.visual.auth.annotation.ApiDecrypt;
import com.stdc.visual.auth.prop.ApiCryptoProperties;
import com.stdc.visual.auth.utils.ApiCryptoUtil;
import com.stdc.visual.auth.utils.CryptoInfoBean;
import com.stdc.visual.auth.utils.DecryptHttpInputMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author: wang_jie
 * @data: 2022/7/8--15:13
 * @describe: 请求体解密
 */
@Slf4j
@Order(1)
@Configuration(proxyBeanMethods = false)
@ControllerAdvice
@RequiredArgsConstructor
@ConditionalOnProperty(value = ApiCryptoProperties.PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
public class ApiDecryptRequestBodyAdvice  implements RequestBodyAdvice {

    private final ApiCryptoProperties properties;


    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return ClassUtil.isAnnotated(methodParameter.getMethod(), ApiDecrypt.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        // 判断 body 是否为空
        InputStream messageBody = inputMessage.getBody();
        if (messageBody.available() <= 0) {
            return inputMessage;
        }
        byte[] decryptedBody = null;
        CryptoInfoBean cryptoInfoBean = ApiCryptoUtil.getDecryptInfo(parameter);
        if (cryptoInfoBean != null) {
            // base64 byte array
            byte[] bodyByteArray = StreamUtils.copyToByteArray(messageBody);
            decryptedBody = ApiCryptoUtil.decryptData(properties, bodyByteArray, cryptoInfoBean);
        }
        if (decryptedBody == null) {
            BaseException.throwException("Decryption error, " +
                    "please check if the selected source data is encrypted correctly." +
                    " (解密错误，请检查选择的源数据的加密方式是否正确。)");
        }
        InputStream inputStream = new ByteArrayInputStream(decryptedBody);
        return new DecryptHttpInputMessage(inputStream, inputMessage.getHeaders());
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
