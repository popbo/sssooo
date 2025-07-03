package com.stdc.visual.auth.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;


public class XmlUtil {

    private static XmlMapper xmlMapper = new XmlMapper();

    public static String toJson(String xml) {
        StringWriter w = new StringWriter();
        try (JsonParser jp = xmlMapper.getFactory().createParser(xml);JsonGenerator jg = JsonUtil.getMapper().getFactory().createGenerator(w);){

            while (jp.nextToken() != null) {
                jg.copyCurrentEvent(jp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return w.toString();
    }

}