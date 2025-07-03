package com.stdc.visual.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/11/16--19:11
 * @describe: http请求,响应工具类
 */
@Slf4j
public class HttpServletUtil {

    /**
     * 获取当前请求
     * @return
     */
    public static HttpServletRequest getCurrentRequest(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attr.getRequest();
    }

    /**
     * 获取当前响应
     * @return
     */
    public static HttpServletResponse getCurrentResp(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attr.getResponse();
    }

    /**
     * 写入响应码和信息到response中
     * @param respCode
     * @param msg
     * @throws IOException
     */
    public static void writeJsonToResp(int respCode ,String msg) throws IOException {
        writeJsonToResp(getCurrentResp(),respCode,msg);
    }

    /**
     * 写入响应码和信息到response中
     * @param response
     * @param respCode
     * @param msg
     * @throws IOException
     */
    public static void writeJsonToResp(HttpServletResponse response,int respCode ,String msg) throws IOException {
        //设置响应状态码
        response.setStatus(respCode);
        //设置响应数据格式
        response.setContentType("application/json;charset=utf-8");
        //输入响应内容
        PrintWriter writer = response.getWriter();
        String json= JSON.toJSONString(R.fail(respCode, msg));
        writer.write(json);
        writer.flush();
    }

    /**
     * 写入文件到 HttpServletResponse
     * @param path 本地文件地址
     */
    public static void writeFileToResp(String path) {
        writeFileToResp(new File(path));
    }

    /**
     * 写入文件到 HttpServletResponse
     * @param file 文件对象
     */
    private static void writeFileToResp(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            writeFileToResp(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if (ObjectUtil.isNotEmpty(inputStream)) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 写入输入流到 HttpServletResponse
     * @param inputStream 输入流
     */
    private static void writeFileToResp(InputStream inputStream) {
        HttpServletResponse currentResp = HttpServletUtil.getCurrentResp();
        currentResp.setStatus(HttpServletResponse.SC_OK);
        ServletOutputStream outputStream = null;
        try {
            outputStream = currentResp.getOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (ObjectUtil.isNotEmpty(outputStream)) outputStream.close();
                if (ObjectUtil.isNotEmpty(inputStream)) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取请求参数 Map集合 getParamMap
     * @return
     */
    public static Map<String,String[]> getParamMap(){
        return getCurrentRequest().getParameterMap();
    }


    /**
     * 获取请求体内容 返回 JavaObj
     * @return
     */
    public static <T> T getBodyToJavaObj(Class<T> clazz){
        return JSON.toJavaObject(getBodyMap(),clazz);
    }

    /**
     * 获取请求体内容 返回 JSONObject
     * @return
     */
    public static JSONObject getBodyMap(){
        return JSON.parseObject(getBodyStr());
    }

    /**
     * 获取请求体内容 返回 String
     * @return
     */
    public static String getBodyStr(){
        HttpServletRequest currentRequest = getCurrentRequest();
        String collect = null;
        try {
            collect = currentRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error(LogUtil.getExceptionDetailsToStr(e));
        }
        return collect;
    }








}
