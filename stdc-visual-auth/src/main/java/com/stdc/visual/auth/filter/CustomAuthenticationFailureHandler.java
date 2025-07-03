package com.stdc.visual.auth.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.visual.auth.config.SecurityConfig;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author: wang_jie
 * @data: 2022/5/24--19:47
 * @describe: 自定义 未认证过滤器
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (ignoreUrl(request)){
            return;
        }
        unsuccessfulLog(request);
        //设置响应状态码
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //设置响应数据格式
        response.setContentType("application/json;charset=utf-8");
        //输入响应内容
        PrintWriter writer = response.getWriter();
        String json= JSON.toJSONString(R.fail(HttpServletResponse.SC_UNAUTHORIZED, ResultCode.get("not_logged_in_status")));
        writer.write(json);
        writer.flush();
    }

    /**
     * 忽略swagger白名单
     * @param request
     * @return
     */
    private Boolean ignoreUrl(HttpServletRequest request){
        for (String url : SecurityConfig.SWAGGER_URL) {
            if (request.getRequestURI().contains(url)){
                return true;
            }
        }
        return false;
    }

    /**
     * 日志记录
     * @param request
     */
    private void unsuccessfulLog(HttpServletRequest request){
        String requestUrl = Objects.requireNonNull(request).getRequestURI();
        String requestMethod = request.getMethod();
        // 构建成一条长 日志，避免并发下日志错乱
        StringBuilder reqLog = new StringBuilder(300);
        // 日志参数
        List<Object> reqArgs = new ArrayList<>();
        reqLog.append("\n\n================  Auth 登录状态异常 ================\n");
        // 打印路由
        reqLog.append("===> {}: {}");
        reqArgs.add(requestMethod);
        reqArgs.add(requestUrl);
        reqLog.append("===>\n {}");
        reqArgs.add(JSON.toJSONString(getAllRequestParam(request), SerializerFeature.PrettyFormat));
        // 打印请求 headers
        logIngHeaders(request, reqLog, reqArgs);
        reqLog.append("================   Auth 登录状态异常   ================\n");
        LogUtil.info(reqLog.toString(),reqArgs.toArray());
    }

    /**
     * 记录请求头
     * @param request       HttpServletRequest
     * @param beforeReqLog  StringBuilder
     * @param beforeReqArgs beforeReqArgs
     */
    private void logIngHeaders(HttpServletRequest request, StringBuilder beforeReqLog, List<Object> beforeReqArgs) {
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            String headerValue = request.getHeader(headerName);
            beforeReqLog.append("===Headers===  {}: {}\n");
            beforeReqArgs.add(headerName);
            beforeReqArgs.add(headerValue);
        }
    }

    /**
     * 获取请求参数
     * @param request
     * @return
     */
    private Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
            }
        }
        return res;
    }

}
