package com.stdc.visual.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2024/5/15--14:58
 * @describe:
 */
@Slf4j
public class PoiUtils {

    public static void convertJSONToExcel(String name,String jsonContent, String savePath) {
        JSONObject jsonData = JSONObject.parseObject(jsonContent);
        JSONArray columns = jsonData.getJSONArray("column");
        Boolean isLevel2 = false;
        for (int i = 0; i < columns.size(); i++) {
            JSONObject column = columns.getJSONObject(i);
            if (ObjectUtil.isNotEmpty(column.getJSONArray("children"))){
                isLevel2 = true;
                break;
            }
        }
        if (isLevel2){
            convertJSONToLevel2Excel(name,jsonContent,savePath);
        }else {
            convertJSONToLevel1Excel(name,jsonContent,savePath);
        }
    }

    /**
     * 一级表头
     * @param name
     * @param jsonContent
     * @param savePath
     */
    public static void convertJSONToLevel1Excel(String name,String jsonContent, String savePath) {
        try {
            Map<String,Integer> propToIndexMap = new HashMap<>();
            Map<String,String> propToLabelMap = new HashMap<>();
            JSONObject jsonData = JSONObject.parseObject(jsonContent);
            JSONArray columns = jsonData.getJSONArray("column");
            JSONArray data = jsonData.getJSONArray("data");
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(name);
            Row headerRow0 = sheet.createRow(0);

            //设置列
            //单元格起点
            JSONObject rowH = data.getJSONObject(0);
            Set<String> rowHead = rowH.keySet();
            List<Object> columnHeadS= columns.stream().filter(c -> rowHead.contains(((JSONObject) c).getString("prop"))).collect(Collectors.toList());
            for (int i = 0; i < columnHeadS.size(); i++) {
                JSONObject column = (JSONObject)columnHeadS.get(i);
                Cell cell = headerRow0.createCell(i);
                cell.setCellValue(column.getString("prop"));
                propToIndexMap.put(column.getString("prop"),cell.getColumnIndex());
                propToLabelMap.put(column.getString("prop"),column.getString("label"));
            }

            //设置行数据
            int rowStart = 1;
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(rowStart);
                JSONObject dataObj = data.getJSONObject(i);
                for (String keyProp : dataObj.keySet()) {
                    Integer CellIndex = propToIndexMap.get(keyProp);
                    if (ObjectUtil.isNotEmpty(CellIndex)){
                        Cell cell = row.createCell(CellIndex);
                        cell.setCellValue(dataObj.getString(keyProp));
                    }
                }
                rowStart++;
            }
            //替换prop标题
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER); // 设置水平居中
            for (String prop : propToLabelMap.keySet()) {
                for (Cell cell : headerRow0) {
                    if (StringUtil.equals(prop,cell.getStringCellValue())){
                        cell.setCellValue(propToLabelMap.get(prop));
                        cell.setCellStyle(cellStyle);
                    }
                }
            }
            //设置行宽 居中
            FileOutputStream fileOut = new FileOutputStream(savePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            log.info("-------> 一级表头文件生成成功: " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 二级表头
     * @param name
     * @param jsonContent
     * @param savePath
     */
    public static void convertJSONToLevel2Excel(String name,String jsonContent, String savePath) {
        try {
            Map<String,Integer> propToIndexMap = new HashMap<>();
            Map<String,String> propToLabelMap = new HashMap<>();
            JSONObject jsonData = JSONObject.parseObject(jsonContent);
            JSONArray columns = jsonData.getJSONArray("column");
            JSONArray data = jsonData.getJSONArray("data");
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(name);
            Row headerRow0 = sheet.createRow(0);
            Row headerRow1 = sheet.createRow(1);
            //设置列
            //单元格起点
            int cellStart = 0;
            for (int i = 0; i < columns.size(); i++) {
                JSONObject column = columns.getJSONObject(i);

                //没有二级表单  纵向合并第一行和第二行
                if (CollectionUtils.isEmpty(column.getJSONArray("children"))){
                    //合并第一行的单元格
                    CellRangeAddress cra = new CellRangeAddress(0,1,cellStart,cellStart);
                    sheet.addMergedRegion(cra);
                    Cell cell = headerRow0.createCell(cellStart);
                    cell.setCellValue(column.getString("prop"));
                    propToIndexMap.put(column.getString("prop"),cell.getColumnIndex());
                    propToLabelMap.put(column.getString("prop"),column.getString("label"));
                    //单元格只加一
                    cellStart ++;
                }
                //有二级表单 纵向不合并  但是横向合并
                else {
                    JSONArray children = column.getJSONArray("children");
                    int childrenSize = children.size();
                    //合并第一行的单元格
                    CellRangeAddress cra = new CellRangeAddress(0,0,cellStart,cellStart + childrenSize - 1);
                    sheet.addMergedRegion(cra);
                    Cell cell = headerRow0.createCell(cellStart);
                    cell.setCellValue(column.getString("prop"));
                    propToLabelMap.put(column.getString("prop"),column.getString("label"));
                    //根据二级菜单的数量增加单元格数量
                    for (int j = 0; j < childrenSize; j++) {
                        Cell c = headerRow1.createCell(cellStart);
                        JSONObject jsonObject = children.getJSONObject(j);
                        c.setCellValue(jsonObject.getString("prop"));
                        propToIndexMap.put(jsonObject.getString("prop"),c.getColumnIndex());
                        propToLabelMap.put(jsonObject.getString("prop"),jsonObject.getString("label"));
                        cellStart ++;
                    }
                }
            }
            //设置行数据
            int rowStart = 2;
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(rowStart);
                JSONObject dataObj = data.getJSONObject(i);
                for (String keyProp : dataObj.keySet()) {
                    Integer CellIndex = propToIndexMap.get(keyProp);
                    if (ObjectUtil.isNotEmpty(CellIndex)){
                        Cell cell = row.createCell(CellIndex);
                        cell.setCellValue(dataObj.getString(keyProp));
                    }
                }
                rowStart++;
            }
            //替换prop标题
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER); // 设置水平居中

            for (String prop : propToLabelMap.keySet()) {
                for (Cell cell : headerRow0) {
                    if (StringUtil.equals(prop,cell.getStringCellValue())){
                        cell.setCellValue(propToLabelMap.get(prop));
                        cell.setCellStyle(cellStyle);
                    }
                }
                for (Cell cell : headerRow1) {
                    if (StringUtil.equals(prop,cell.getStringCellValue())){
                        cell.setCellValue(propToLabelMap.get(prop));
                        cell.setCellStyle(cellStyle);
                    }
                }
            }
            //设置行宽 居中
            FileOutputStream fileOut = new FileOutputStream(savePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            log.info("-------> 二级表头文件生成成功: " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public static void main(String[] args) {
//        String body = "{\"column\":[{\"label\":\"县（市、区）/市直学校\",\"prop\":\"县（市、区）/市直学校\"},{\"label\":\"留守儿童信息\",\"prop\":\"留守儿童信息\",\"children\":[{\"label\":\"应填报数量\",\"prop\":\"留守儿童应填报数量\"},{\"label\":\"已填报数量\",\"prop\":\"留守儿童实际填报数量\"},{\"label\":\"完成率\",\"prop\":\"留守儿童统计完成率\"}]},{\"label\":\"单亲家庭学生信息\",\"prop\":\"单亲家庭学生信息\",\"children\":[{\"label\":\"应填报数量\",\"prop\":\"单亲家庭学生应填报数量\"},{\"label\":\"已填报数量\",\"prop\":\"单亲家庭学生实际填报数量\"},{\"label\":\"完成率\",\"prop\":\"单亲家庭学生统计完成率\"}]},{\"label\":\"心理风险学生信息\",\"prop\":\"心理风险学生信息\",\"children\":[{\"label\":\"应填报数量\",\"prop\":\"心理风险学生应填报数量\"},{\"label\":\"已填报数量\",\"prop\":\"心理风险学生实际填报数量\"},{\"label\":\"完成率\",\"prop\":\"心理风险学生统计完成率\"}]},{\"label\":\"控辍保学信息\",\"prop\":\"控辍保学信息\",\"children\":[{\"label\":\"台账人数\",\"prop\":\"台账人数\"},{\"label\":\"已辍学人数\",\"prop\":\"已辍学人数\"},{\"label\":\"存在辍学风险人数\",\"prop\":\"存在辍学风险人数\"},{\"label\":\"实际人数\",\"prop\":\"实际人数\"},{\"label\":\"实际已辍学人数\",\"prop\":\"实际已辍学人数\"},{\"label\":\"实际存在辍学风险人数\",\"prop\":\"实际存在辍学风险人数\"},{\"label\":\"填报人数统计完成率\",\"prop\":\"填报人数统计完成率\"},{\"label\":\"已辍学人数统计完成率\",\"prop\":\"已辍学人数统计完成率\"},{\"label\":\"存在辍学风险人数统计完成率\",\"prop\":\"存在辍学风险人数统计完成率\"}]}],\"data\":[{\"县（市、区）/市直学校\":\"川汇区\",\"留守儿童应填报数量\":5969,\"留守儿童实际填报数量\":101,\"留守儿童统计完成率\":\"1.69%\",\"单亲家庭学生应填报数量\":2919,\"单亲家庭学生实际填报数量\":43,\"单亲家庭学生统计完成率\":\"1.47%\",\"心理风险学生应填报数量\":1192,\"心理风险学生实际填报数量\":6,\"心理风险学生统计完成率\":\"0.50%\",\"台账人数\":\"131\",\"已辍学人数\":\"114\",\"存在辍学风险人数\":\"17\",\"实际人数\":68,\"实际已辍学人数\":36,\"实际存在辍学风险人数\":32,\"填报人数统计完成率\":\"51.91%\",\"已辍学人数统计完成率\":\"31.58%\",\"存在辍学风险人数统计完成率\":\"100.00%\"},{\"县（市、区）/市直学校\":\"扶沟县\",\"留守儿童应填报数量\":6805,\"留守儿童实际填报数量\":0,\"留守儿童统计完成率\":\"0.00%\",\"单亲家庭学生应填报数量\":3141,\"单亲家庭学生实际填报数量\":0,\"单亲家庭学生统计完成率\":\"0.00%\",\"心理风险学生应填报数量\":1304,\"心理风险学生实际填报数量\":0,\"心理风险学生统计完成率\":\"0.00%\",\"台账人数\":\"60\",\"已辍学人数\":\"47\",\"存在辍学风险人数\":\"13\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"0.00%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"0.00%\"},{\"县（市、区）/市直学校\":\"鹿邑县\",\"留守儿童应填报数量\":16474,\"留守儿童实际填报数量\":0,\"留守儿童统计完成率\":\"0.00%\",\"单亲家庭学生应填报数量\":5715,\"单亲家庭学生实际填报数量\":0,\"单亲家庭学生统计完成率\":\"0.00%\",\"心理风险学生应填报数量\":592,\"心理风险学生实际填报数量\":0,\"心理风险学生统计完成率\":\"0.00%\",\"台账人数\":\"63\",\"已辍学人数\":\"63\",\"存在辍学风险人数\":\"0\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"0.00%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"\"},{\"县（市、区）/市直学校\":\"西华县\",\"留守儿童应填报数量\":12508,\"留守儿童实际填报数量\":95,\"留守儿童统计完成率\":\"0.76%\",\"单亲家庭学生应填报数量\":4424,\"单亲家庭学生实际填报数量\":52,\"单亲家庭学生统计完成率\":\"1.18%\",\"心理风险学生应填报数量\":327,\"心理风险学生实际填报数量\":9,\"心理风险学生统计完成率\":\"2.75%\",\"台账人数\":\"347\",\"已辍学人数\":\"290\",\"存在辍学风险人数\":\"57\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"0.00%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"0.00%\"},{\"县（市、区）/市直学校\":\"沈丘县\",\"留守儿童应填报数量\":17623,\"留守儿童实际填报数量\":0,\"留守儿童统计完成率\":\"0.00%\",\"单亲家庭学生应填报数量\":5147,\"单亲家庭学生实际填报数量\":0,\"单亲家庭学生统计完成率\":\"0.00%\",\"心理风险学生应填报数量\":242,\"心理风险学生实际填报数量\":0,\"心理风险学生统计完成率\":\"0.00%\",\"台账人数\":\"391\",\"已辍学人数\":\"288\",\"存在辍学风险人数\":\"103\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"0.00%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"0.00%\"},{\"县（市、区）/市直学校\":\"淮阳区\",\"留守儿童应填报数量\":10427,\"留守儿童实际填报数量\":0,\"留守儿童统计完成率\":\"0.00%\",\"单亲家庭学生应填报数量\":5283,\"单亲家庭学生实际填报数量\":0,\"单亲家庭学生统计完成率\":\"0.00%\",\"心理风险学生应填报数量\":755,\"心理风险学生实际填报数量\":0,\"心理风险学生统计完成率\":\"0.00%\",\"台账人数\":\"1080\",\"已辍学人数\":\"618\",\"存在辍学风险人数\":\"462\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"0.00%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"0.00%\"},{\"县（市、区）/市直学校\":\"项城市\",\"留守儿童应填报数量\":19503,\"留守儿童实际填报数量\":32,\"留守儿童统计完成率\":\"0.16%\",\"单亲家庭学生应填报数量\":7488,\"单亲家庭学生实际填报数量\":9,\"单亲家庭学生统计完成率\":\"0.12%\",\"心理风险学生应填报数量\":3560,\"心理风险学生实际填报数量\":0,\"心理风险学生统计完成率\":\"0.00%\",\"台账人数\":\"366\",\"已辍学人数\":\"274\",\"存在辍学风险人数\":\"92\",\"实际人数\":12,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":12,\"填报人数统计完成率\":\"3.28%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"13.04%\"},{\"县（市、区）/市直学校\":\"商水县\",\"留守儿童应填报数量\":17217,\"留守儿童实际填报数量\":728,\"留守儿童统计完成率\":\"4.23%\",\"单亲家庭学生应填报数量\":5884,\"单亲家庭学生实际填报数量\":109,\"单亲家庭学生统计完成率\":\"1.85%\",\"心理风险学生应填报数量\":416,\"心理风险学生实际填报数量\":10,\"心理风险学生统计完成率\":\"2.40%\",\"台账人数\":\"137\",\"已辍学人数\":\"82\",\"存在辍学风险人数\":\"55\",\"实际人数\":20,\"实际已辍学人数\":2,\"实际存在辍学风险人数\":18,\"填报人数统计完成率\":\"14.60%\",\"已辍学人数统计完成率\":\"2.44%\",\"存在辍学风险人数统计完成率\":\"32.73%\"},{\"县（市、区）/市直学校\":\"太康县\",\"留守儿童应填报数量\":24101,\"留守儿童实际填报数量\":196,\"留守儿童统计完成率\":\"0.81%\",\"单亲家庭学生应填报数量\":3799,\"单亲家庭学生实际填报数量\":17,\"单亲家庭学生统计完成率\":\"0.45%\",\"心理风险学生应填报数量\":320,\"心理风险学生实际填报数量\":31,\"心理风险学生统计完成率\":\"9.69%\",\"台账人数\":\"1063\",\"已辍学人数\":\"397\",\"存在辍学风险人数\":\"666\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"0.00%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"0.00%\"},{\"县（市、区）/市直学校\":\"郸城县\",\"留守儿童应填报数量\":32504,\"留守儿童实际填报数量\":1103,\"留守儿童统计完成率\":\"3.39%\",\"单亲家庭学生应填报数量\":10138,\"单亲家庭学生实际填报数量\":508,\"单亲家庭学生统计完成率\":\"5.01%\",\"心理风险学生应填报数量\":1008,\"心理风险学生实际填报数量\":13,\"心理风险学生统计完成率\":\"1.29%\",\"台账人数\":\"516\",\"已辍学人数\":\"491\",\"存在辍学风险人数\":\"25\",\"实际人数\":10,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":10,\"填报人数统计完成率\":\"1.94%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"40.00%\"},{\"县（市、区）/市直学校\":\"黄泛区\",\"留守儿童应填报数量\":435,\"留守儿童实际填报数量\":0,\"留守儿童统计完成率\":\"0.00%\",\"单亲家庭学生应填报数量\":162,\"单亲家庭学生实际填报数量\":0,\"单亲家庭学生统计完成率\":\"0.00%\",\"心理风险学生应填报数量\":6,\"心理风险学生实际填报数量\":0,\"心理风险学生统计完成率\":\"0.00%\",\"台账人数\":\"0\",\"已辍学人数\":\"0\",\"存在辍学风险人数\":\"0\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"\",\"已辍学人数统计完成率\":\"\",\"存在辍学风险人数统计完成率\":\"\"},{\"县（市、区）/市直学校\":\"示范区\",\"留守儿童应填报数量\":790,\"留守儿童实际填报数量\":0,\"留守儿童统计完成率\":\"0.00%\",\"单亲家庭学生应填报数量\":451,\"单亲家庭学生实际填报数量\":0,\"单亲家庭学生统计完成率\":\"0.00%\",\"心理风险学生应填报数量\":381,\"心理风险学生实际填报数量\":0,\"心理风险学生统计完成率\":\"0.00%\",\"台账人数\":\"4\",\"已辍学人数\":\"1\",\"存在辍学风险人数\":\"3\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"0.00%\",\"已辍学人数统计完成率\":\"0.00%\",\"存在辍学风险人数统计完成率\":\"0.00%\"},{\"县（市、区）/市直学校\":\"市直学校\",\"留守儿童应填报数量\":2634,\"留守儿童实际填报数量\":0,\"留守儿童统计完成率\":\"0.00%\",\"单亲家庭学生应填报数量\":1234,\"单亲家庭学生实际填报数量\":0,\"单亲家庭学生统计完成率\":\"0.00%\",\"心理风险学生应填报数量\":1986,\"心理风险学生实际填报数量\":0,\"心理风险学生统计完成率\":\"0.00%\",\"台账人数\":\"12\",\"已辍学人数\":\"0\",\"存在辍学风险人数\":\"12\",\"实际人数\":0,\"实际已辍学人数\":0,\"实际存在辍学风险人数\":0,\"填报人数统计完成率\":\"0.00%\",\"已辍学人数统计完成率\":\"\",\"存在辍学风险人数统计完成率\":\"0.00%\"},{\"县（市、区）/市直学校\":\"合计\",\"留守儿童应填报数量\":166990,\"留守儿童实际填报数量\":2255,\"留守儿童统计完成率\":\"\",\"单亲家庭学生应填报数量\":55785,\"单亲家庭学生实际填报数量\":738,\"单亲家庭学生统计完成率\":\"\",\"心理风险学生应填报数量\":12089,\"心理风险学生实际填报数量\":69,\"心理风险学生统计完成率\":\"\",\"台账人数\":\"4170\",\"已辍学人数\":\"2665\",\"存在辍学风险人数\":\"1505\",\"实际人数\":110,\"实际已辍学人数\":38,\"实际存在辍学风险人数\":72,\"填报人数统计完成率\":\"\",\"已辍学人数统计完成率\":\"\",\"存在辍学风险人数统计完成率\":\"\"}]}";
//        convertJSONToExcel("666666",body, "3454345.xlsx");
//    }

}
