package com.stdc.visual.auth.utils;

import com.fasterxml.jackson.databind.*;

import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    /**
     * 获取ObjectMapper
     * @return ObjectMapper
     */
    public static ObjectMapper getMapper(){
        return mapper;
    }

    /**
     * 字符串转JsonNode
     * @param json	字符串
     * @return		JsonNode
     * @throws IOException
     */
    public static JsonNode toJsonNode(String json) throws IOException{
        return mapper.readTree(json);
    }
}
