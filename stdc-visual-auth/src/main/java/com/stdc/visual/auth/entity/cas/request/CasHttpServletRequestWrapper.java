package com.stdc.visual.auth.entity.cas.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * @author: wang_jie
 * @data: 2022/10/27--10:15
 * @describe:
 */
public class CasHttpServletRequestWrapper  extends HttpServletRequestWrapper {

    //存储请求头
    private Map<String, String> extraHeaders;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public CasHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        extraHeaders = new HashMap<>();
    }
    /**
     * 添加请求头
     * @param name
     * @param value
     */
    public void addHeader(String name, String value){
        extraHeaders.put(name, value);
    }

    /**
     * 重写获取请求头方法
     * @param name
     * @return 请求头 value
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (extraHeaders.containsKey(name)){
            value = extraHeaders.get(name);
        }
        return value;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Enumeration<String> enumeration = super.getHeaderNames();
        Set<String> extraSet = new HashSet<>(extraHeaders.keySet());
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            extraSet.add(name);
        }
        return Collections.enumeration(extraSet);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> header = Collections.list(super.getHeaders(name));
        if (extraHeaders.containsKey(name)){
            header = Arrays.asList(extraHeaders.get(name));
        }
        return Collections.enumeration(header);
    }

}
