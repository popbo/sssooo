package com.stdc.visual.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:40
 * @describe: json工具类 继承 JSONObject
 */
@Slf4j
public class JsonUtil extends JSONObject {

    /**
     * json字符串转对象
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toJavaObj(String str, Class<T> clazz) {
        JSONObject object = JSONObject.parseObject(str);
        return JSONObject.toJavaObject(object, clazz);
    }

    /**
     * 将json字符串写入本地文件
     * @param jsonStr
     * @param filePath
     */
    public static void writeJsonToFile(String jsonStr,String filePath){
        try {
            File outFile = new File(filePath);
            //文件不存在 创建文件
            if (!outFile.exists()){
                File parentFile = outFile.getParentFile();
                if (!parentFile.exists()) parentFile.mkdirs();
                outFile.createNewFile();
            }
            FileWriter writer = new FileWriter(outFile);
            try {
                writer.write(jsonStr);
            } catch (IOException e) {
                log.info("JSON字符串保存为本地JSON文件失败");
            }finally {
                writer.flush();
                writer.close();
            }
            log.info("JSON字符串已保存为本地JSON文件!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
