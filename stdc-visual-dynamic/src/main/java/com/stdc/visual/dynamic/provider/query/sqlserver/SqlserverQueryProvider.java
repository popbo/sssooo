package com.stdc.visual.dynamic.provider.query.sqlserver;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.stdc.visual.dynamic.base.chart.dto.ChartCustomFilterItemDTO;
import com.stdc.visual.dynamic.base.chart.dto.ChartFieldCustomFilterDTO;
import com.stdc.visual.dynamic.base.dataset.dto.condition.MeasureFormatCondition;
import com.stdc.visual.dynamic.base.dataset.dto.condition.PolCondition;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.base.datasource.dto.configuration.JdbcConfiguration;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import com.stdc.visual.dynamic.base.sqlobj.SQLObj;
import com.stdc.visual.dynamic.mapper.DatasetTableFieldMapper;
import com.stdc.visual.dynamic.provider.query.DeTypeConstants;
import com.stdc.visual.dynamic.provider.query.QueryProvider;
import com.stdc.visual.dynamic.provider.query.SQLConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.stdc.visual.dynamic.provider.query.SQLConstants.TABLE_ALIAS_PREFIX;

/**
 * @author: wang_jie
 * @data: 2022/6/28--14:17
 * @describe:
 */
@Service("sqlserverQuery")
public class SqlserverQueryProvider extends QueryProvider {
    @Resource
    private DatasetTableFieldMapper datasetTableFieldMapper;

    @Override
    public Integer transFieldType(String field) {
        field = field.toUpperCase();
        switch (field) {
            case "CHAR":
            case "NCHAR":
            case "NTEXT":
            case "VARCHAR":
            case "TEXT":
            case "TINYTEXT":
            case "MEDIUMTEXT":
            case "LONGTEXT":
            case "ENUM":
            case "XML":
            case "TIME":
                return DeTypeConstants.DE_STRING;// 文本
            case "DATE":
            case "YEAR":
            case "DATETIME":
            case "DATETIME2":
            case "DATETIMEOFFSET":
                return DeTypeConstants.DE_TIME;// 时间
            case "INT":
            case "MEDIUMINT":
            case "INTEGER":
            case "BIGINT":
            case "SMALLINT":
                return DeTypeConstants.DE_INT;// 整型
            case "FLOAT":
            case "DOUBLE":
            case "DECIMAL":
            case "MONEY":
            case "NUMERIC":
                return DeTypeConstants.DE_FLOAT;// 浮点
            case "BIT":
            case "TINYINT":
                return DeTypeConstants.DE_BOOL;// 布尔
            case "TIMESTAMP":
                return DeTypeConstants.DE_BINARY;// 二进制
            default:
                return DeTypeConstants.DE_STRING;
        }
    }

    @Override
    public String createSQLPreview(String sql, String orderBy) {
        return "SELECT top 1000 * FROM (" + sqlFix(sql) + ") AS tmp";
    }

    @Override
    public String createQuerySQL(DataSetTableRequest request, String table, List<DatasetTableField> fieldsCopy, boolean isGroup, Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilterCopy) {
        //深拷贝
        List<DatasetTableField> fields = deepCopy(fieldsCopy);
        List<ChartFieldCustomFilterDTO> fieldCustomFilter = deepCopy(fieldCustomFilterCopy);
        SQLObj tableObj = SQLObj.builder()
                .tableName((table.startsWith("(") && table.endsWith(")")) ? table : String.format(SqlServerSQLConstants.KEYWORD_TABLE, table))
                .tableAlias(String.format(TABLE_ALIAS_PREFIX, 0))
                .build();
        setSchema(tableObj, ds);
        //分组字段，因为度量、数值型字段中默认使用sum()函数，使用维度字段进行分组
        List<SQLObj> groupFields = new ArrayList<>();
        List<SQLObj> xFields = new ArrayList<>();
        /**获取查询条件*/
        /***维度*/
        String dimensionField = request.getDimensionField();
        /***度量*/
        String measureField = request.getMeasureField();
        /***图例*/
        String legendField = request.getLegendField();
        /**维度、度量、图例 均为""时，使用不含自定义方法创建sql*/
        if (StringUtils.isEmpty(dimensionField) && StringUtils.isEmpty(measureField) && StringUtils.isEmpty(legendField)){
            return createQuerySQLWithNoFilter(table,fields,isGroup,ds,fieldCustomFilterCopy);
        }
        /**sql查询的字段集合*/
        String idS =null;
        if (CollectionUtils.isNotEmpty(fields)) {
            for (int i = 0; i < fields.size(); i++) {
                DatasetTableField f = fields.get(i);
                //只查询维度、度量、图例字段
                idS = dimensionField + "," + measureField + "," + legendField;
                if (!idS.contains(f.getId())){
                    continue;
                }
                String originField;
                if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 2) {
                    // 解析origin name中有关联的字段生成sql表达式
                    originField = calcFieldRegex(f.getOriginName(), tableObj);
                } else if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 1) {
                    originField = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                } else {
                    originField = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                }
                String fieldAlias = String.format(SQLConstants.FIELD_ALIAS_X_PREFIX, i);
                String fieldName;
                // 处理横轴字段
                if (f.getDeExtractType() == DeTypeConstants.DE_TIME) { // 时间 转为 数值
                    if (f.getDeType() == DeTypeConstants.DE_INT || f.getDeType() == DeTypeConstants.DE_FLOAT) {
                        fieldName = String.format(SqlServerSQLConstants.UNIX_TIMESTAMP, originField);
                    } else {
                        fieldName = originField;
                    }
                } else if (f.getDeExtractType() == DeTypeConstants.DE_STRING) {
                    if (f.getDeType() == DeTypeConstants.DE_INT) {
                        fieldName = transPol(request, f, originField);
                    } else if (f.getDeType() == DeTypeConstants.DE_FLOAT) {
                        fieldName = transPol(request, f, originField);
                    } else if (f.getDeType() == DeTypeConstants.DE_TIME) { //字符串转时间
                        fieldName = String.format(SqlServerSQLConstants.STRING_TO_DATE, originField);
                    } else {
                        fieldName = originField;
                    }
                } else {
                    if (f.getDeType() == DeTypeConstants.DE_TIME) { // 数值转时间
                        String cast = transPol(request, f, originField);
                        fieldName = String.format(SqlServerSQLConstants.FROM_UNIXTIME, cast);
                    } else if (f.getDeType() == DeTypeConstants.DE_INT) {
                        fieldName = transPol(request, f, originField);
                    } else {
                        fieldName = originField;
                    }
                }
                xFields.add(SQLObj.builder()
                        .fieldName(fieldName)
                        .fieldAlias(fieldAlias)
                        .build());
                //筛选出 维度、图例 字段
                idS = dimensionField + "," + legendField;
                if (idS.contains(f.getId())){
                    if (!fieldName.contains("SUM") && f.getDeType() != 1){
                        groupFields.add(SQLObj.builder()
                                .fieldName(fieldName)
                                .fieldAlias(fieldAlias)
                                .build());
                    }
                }
                //重命名别名
                f.setOriginName(fieldAlias);
            }
        }

        STGroup stg = new STGroupFile(SQLConstants.SQL_TEMPLATE);
        ST st_sql = stg.getInstanceOf("previewSql");
        st_sql.add("isCustomGroup", true);
        st_sql.add("isGroup", isGroup);
        if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("groups", xFields);
        //分组条件
        if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("customGroups", groupFields);
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);
        //除度量以外的条件 放到where子句中
        String customWheres = transCustomFilterListNoMeasureFieldS(tableObj, fieldCustomFilter,measureField);
        if (ObjectUtils.isNotEmpty(customWheres)){
            List<String> wheres = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(customWheres)) wheres.add(customWheres);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
        }
        //度量条件 放到having子句中
        String havingS = havingFilterList(fields, fieldCustomFilter,measureField);
        if (ObjectUtils.isNotEmpty(havingS)){
            List<String> having = new ArrayList<>();
            st_sql.add("isHaving", true);
            having.add(havingS);
            if (CollectionUtils.isNotEmpty(having)) st_sql.add("havings", having);
        }
        return st_sql.render();
    }

    @Override
    public String createQuerySQL(String table, List<DatasetTableField> fieldsCopy, boolean isGroup, Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilterCopy) {
        //深拷贝
        List<DatasetTableField> fields = deepCopy(fieldsCopy);
        List<ChartFieldCustomFilterDTO> fieldCustomFilter = deepCopy(fieldCustomFilterCopy);
        SQLObj tableObj = SQLObj.builder()
                .tableName((table.startsWith("(") && table.endsWith(")")) ? table : String.format(SqlServerSQLConstants.KEYWORD_TABLE, table))
                .tableAlias(String.format(TABLE_ALIAS_PREFIX, 0))
                .build();
        setSchema(tableObj, ds);
        //分组字段，因为度量、数值型字段中默认使用sum()函数，使用维度字段进行分组
        List<SQLObj> groupFields = new ArrayList<>();
        List<SQLObj> xFields = new ArrayList<>();
        /***维度*/
        String dimensionField = "";
        /***度量*/
        String measureField = "";
        /***图例*/
        String legendField = "";
        DataSetTableRequest superRequest = null;
        /**获取查询条件*/
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(fieldCustomFilter)){
            superRequest = fieldCustomFilter.get(0).getSuperRequest();
            dimensionField = superRequest.getDimensionField();
            measureField = superRequest.getMeasureField();
            legendField = superRequest.getLegendField();
        }
        /**维度、度量、图例 均为""时，使用不含自定义方法创建sql*/
        if (StringUtils.isEmpty(dimensionField) && StringUtils.isEmpty(measureField) && StringUtils.isEmpty(legendField)){
            return createQuerySQLWithNoFilter(table,fields,isGroup,ds,fieldCustomFilterCopy);
        }
        /**sql查询的字段集合*/
        String idS =null;
        if (CollectionUtils.isNotEmpty(fields)) {
            for (int i = 0; i < fields.size(); i++) {
                DatasetTableField f = fields.get(i);
                //只查询维度、度量、图例字段
                idS = dimensionField + "," + measureField + "," + legendField;
                if (!idS.contains(f.getId())){
                    continue;
                }
                String originField;
                if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 2) {
                    // 解析origin name中有关联的字段生成sql表达式
                    originField = calcFieldRegex(f.getOriginName(), tableObj);
                } else if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 1) {
                    originField = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                } else {
                    originField = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                }
                String fieldAlias = String.format(SQLConstants.FIELD_ALIAS_X_PREFIX, i);
                String fieldName;
                // 处理横轴字段
                if (f.getDeExtractType() == DeTypeConstants.DE_TIME) { // 时间 转为 数值
                    if (f.getDeType() == DeTypeConstants.DE_INT || f.getDeType() == DeTypeConstants.DE_FLOAT) {
                        fieldName = String.format(SqlServerSQLConstants.UNIX_TIMESTAMP, originField);
                    } else {
                        fieldName = originField;
                    }
                } else if (f.getDeExtractType() == DeTypeConstants.DE_STRING) {
                    if (f.getDeType() == DeTypeConstants.DE_INT) {
                        fieldName = transPol(superRequest, f, originField);
                    } else if (f.getDeType() == DeTypeConstants.DE_FLOAT) {
                        fieldName = transPol(superRequest, f, originField);
                    } else if (f.getDeType() == DeTypeConstants.DE_TIME) { //字符串转时间
                        fieldName = String.format(SqlServerSQLConstants.STRING_TO_DATE, originField);
                    } else {
                        fieldName = originField;
                    }
                } else {
                    if (f.getDeType() == DeTypeConstants.DE_TIME) { // 数值转时间
                        String cast = transPol(superRequest, f, originField);
                        fieldName = String.format(SqlServerSQLConstants.FROM_UNIXTIME, cast);
                    } else if (f.getDeType() == DeTypeConstants.DE_INT) {
                        fieldName = transPol(superRequest, f, originField);
                    } else {
                        fieldName = originField;
                    }
                }
                xFields.add(SQLObj.builder()
                        .fieldName(fieldName)
                        .fieldAlias(fieldAlias)
                        .build());
                //筛选出 维度、图例 字段
                idS = dimensionField + "," + legendField;
                if (idS.contains(f.getId())){
                    if (!fieldName.contains("SUM") && f.getDeType() != 1){
                        groupFields.add(SQLObj.builder()
                                .fieldName(fieldName)
                                .fieldAlias(fieldAlias)
                                .build());
                    }
                }
                //重命名别名
                f.setOriginName(fieldAlias);
            }
        }

        STGroup stg = new STGroupFile(SQLConstants.SQL_TEMPLATE);
        ST st_sql = stg.getInstanceOf("previewSql");
        st_sql.add("isCustomGroup", true);
        st_sql.add("isGroup", isGroup);
        if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("groups", xFields);
        //分组条件
        if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("customGroups", groupFields);
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);
        //除度量以外的条件 放到where子句中
        String customWheres = transCustomFilterListNoMeasureFieldS(tableObj, fieldCustomFilter,measureField);
        if (ObjectUtils.isNotEmpty(customWheres)){
            List<String> wheres = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(customWheres)) wheres.add(customWheres);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
        }
        //度量条件 放到having子句中
        String havingS = havingFilterList(fields, fieldCustomFilter,measureField);
        if (ObjectUtils.isNotEmpty(havingS)){
            List<String> having = new ArrayList<>();
            st_sql.add("isHaving", true);
            having.add(havingS);
            if (CollectionUtils.isNotEmpty(having)) st_sql.add("havings", having);
        }
        return st_sql.render();
    }

    @Override
    public String createQuerySQLAsTmp(String sql, List<DatasetTableField> fields, boolean isGroup, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        return createQuerySQL("(" + sqlFix(sql) + ")", fields, isGroup, null, fieldCustomFilter);
    }

    @Override
    public String createQueryTableWithPage(String table, List<DatasetTableField> fields, Integer page, Integer pageSize, Integer realSize, boolean isGroup, Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        Integer size = (page - 1) * pageSize + realSize;
        return String.format("SELECT top %s * from ( %s ) AS DE_SQLSERVER_TMP ", size.toString(), createQuerySQL(table, fields, isGroup, ds, fieldCustomFilter));
    }

    @Override
    public String createQuerySQLWithPage(String sql, List<DatasetTableField> fields, Integer page, Integer pageSize, Integer realSize, boolean isGroup, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        Integer size = (page - 1) * pageSize + realSize;
        return String.format("SELECT top %s * from ( %s ) AS DE_SQLSERVER_TMP ", size.toString(), createQuerySQLAsTmp(sql, fields, isGroup, fieldCustomFilter));
    }

    @Override
    public String createQueryTableWithLimit(String table, List<DatasetTableField> fields, Integer limit, boolean isGroup, Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        String schema = new Gson().fromJson(ds.getConfiguration(), JdbcConfiguration.class).getSchema();
        return String.format("SELECT top %s * from %s ", limit.toString(), schema + "." + String.format(SqlServerSQLConstants.KEYWORD_TABLE, table));
    }

    @Override
    public String createQuerySqlWithLimit(String sql, List<DatasetTableField> fields, Integer limit, boolean isGroup, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        return String.format("SELECT top %s * from ( %s ) as DE_SQLSERVER_TMP ", limit.toString(), sqlFix(sql));
    }

    @Override
    public String searchTable(String table) {
        return "SELECT table_name FROM information_schema.TABLES WHERE table_name ='" + table + "'";
    }

    private String sqlFix(String sql) {
        if (sql.lastIndexOf(";") == (sql.length() - 1)) {
            sql = sql.substring(0, sql.length() - 1);
        }
        return sql;
    }

    public void setSchema(SQLObj tableObj, Datasource ds) {
        if (ds != null && !tableObj.getTableName().startsWith("(") && !tableObj.getTableName().endsWith(")")) {
            String schema = new Gson().fromJson(ds.getConfiguration(), JdbcConfiguration.class).getSchema();
            schema = String.format(SqlServerSQLConstants.KEYWORD_TABLE, schema);
            tableObj.setTableName(schema + "." + tableObj.getTableName());
        }
    }

    public String createQuerySQLWithNoFilter(String table, List<DatasetTableField> fields, boolean isGroup, Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {

        SQLObj tableObj = SQLObj.builder()
                .tableName((table.startsWith("(") && table.endsWith(")")) ? table : String.format(SqlServerSQLConstants.KEYWORD_TABLE, table))
                .tableAlias(String.format(TABLE_ALIAS_PREFIX, 0))
                .build();
        setSchema(tableObj, ds);

        List<SQLObj> xFields = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fields)) {
            for (int i = 0; i < fields.size(); i++) {
                DatasetTableField f = fields.get(i);
                String originField;
                if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 2) {
                    // 解析origin name中有关联的字段生成sql表达式
                    originField = calcFieldRegex(f.getOriginName(), tableObj);
                } else if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 1) {
                    originField = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                } else {
                    originField = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                }
                String fieldAlias = String.format(SQLConstants.FIELD_ALIAS_X_PREFIX, i);
                String fieldName;
                // 处理横轴字段
                if (f.getDeExtractType() == DeTypeConstants.DE_TIME) { // 时间 转为 数值
                    if (f.getDeType() == DeTypeConstants.DE_INT || f.getDeType() == DeTypeConstants.DE_FLOAT) {
                        fieldName = String.format(SqlServerSQLConstants.UNIX_TIMESTAMP, originField);
                    } else {
                        fieldName = originField;
                    }
                } else if (f.getDeExtractType() == DeTypeConstants.DE_STRING) {
                    if (f.getDeType() == DeTypeConstants.DE_INT) {
                        fieldName = String.format(SqlServerSQLConstants.CONVERT, SqlServerSQLConstants.DEFAULT_INT_FORMAT, originField);
                    } else if (f.getDeType() == DeTypeConstants.DE_FLOAT) {
                        fieldName = String.format(SqlServerSQLConstants.CONVERT, SqlServerSQLConstants.DEFAULT_FLOAT_FORMAT, originField);
                    } else if (f.getDeType() == DeTypeConstants.DE_TIME) { //字符串转时间
                        fieldName = String.format(SqlServerSQLConstants.STRING_TO_DATE, originField);
                    } else {
                        fieldName = originField;
                    }
                } else {
                    if (f.getDeType() == DeTypeConstants.DE_TIME) { // 数值转时间
                        String cast = String.format(SqlServerSQLConstants.LONG_TO_DATE, originField + "/1000");
                        fieldName = String.format(SqlServerSQLConstants.FROM_UNIXTIME, cast);
                    } else if (f.getDeType() == DeTypeConstants.DE_INT) {
                        fieldName = String.format(SqlServerSQLConstants.CONVERT, SqlServerSQLConstants.DEFAULT_INT_FORMAT, originField);
                    } else {
                        fieldName = originField;
                    }
                }
                xFields.add(SQLObj.builder()
                        .fieldName(fieldName)
                        .fieldAlias(fieldAlias)
                        .build());
            }
        }

        STGroup stg = new STGroupFile(SQLConstants.SQL_TEMPLATE);
        ST st_sql = stg.getInstanceOf("previewSql");
        st_sql.add("isGroup", isGroup);
        if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("groups", xFields);
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);
        String customWheres = transCustomFilterList(tableObj, fieldCustomFilter);
        List<String> wheres = new ArrayList<>();
        if (customWheres != null) wheres.add(customWheres);
        if (CollectionUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
        return st_sql.render();
    }

    /**度量聚合函数转换*/
    public String transPol(DataSetTableRequest superRequest,DatasetTableField field,String originField){
        String fieldName = "";
        if (superRequest.getMeasureField().contains(field.getId())){
            if (ObjectUtils.isNotEmpty(superRequest.getFieldCondition())
                    && org.apache.commons.collections.CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getPolConditionS())){
                //度量数据格式
                MeasureFormatCondition measureFormatCondition = null;
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getMeasureFormatConditionS())){
                    measureFormatCondition = superRequest.getFieldCondition().getMeasureFormatConditionS().stream().filter(m -> StringUtils.equals(m.getFieldId(), field.getId())).collect(Collectors.toList()).get(0);
                }
                List<PolCondition> polConditionS = superRequest.getFieldCondition().getPolConditionS();
                for (PolCondition pol : polConditionS) {
                    if (StringUtils.equals(pol.getFieldId(),field.getId())){
                        //默认为常规，无小数位
                        String format = SqlServerSQLConstants.DEFAULT_INT_FORMAT;
                        if (ObjectUtils.isNotEmpty(measureFormatCondition)){
                            switch (measureFormatCondition.getFormat()){
                                case 0:
                                    format = SqlServerSQLConstants.DEFAULT_INT_FORMAT;
                                    break;
                                case 1:
                                    format = SqlServerSQLConstants.DEFAULT_ONE_FORMAT;
                                    break;
                                case 2:
                                    format = SqlServerSQLConstants.DEFAULT_FLOAT_FORMAT;
                                    break;
                                case 3:
                                    format = SqlServerSQLConstants.DEFAULT_THREE_FORMAT;
                                    break;
                                case 4:
                                    format = SqlServerSQLConstants.DEFAULT_FOUR_FORMAT;
                                    break;
                                default:
                                    break;
                            }
                        }
                        fieldName = String.format(SqlServerSQLConstants.SUM, String.format(SqlServerSQLConstants.CAST, originField, format));
                    }
                }
            }else {
                //度量数据格式
                MeasureFormatCondition measureFormatCondition = null;
                if (ObjectUtils.isNotEmpty(superRequest.getFieldCondition()) && org.apache.commons.collections.CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getMeasureFormatConditionS())){
                    measureFormatCondition = superRequest.getFieldCondition().getMeasureFormatConditionS().stream().filter(m -> StringUtils.equals(m.getFieldId(), field.getId())).collect(Collectors.toList()).get(0);
                }
                //默认为常规，无小数位
                String format = SqlServerSQLConstants.DEFAULT_INT_FORMAT;
                if (ObjectUtils.isNotEmpty(measureFormatCondition)){
                    switch (measureFormatCondition.getFormat()){
                        case 0:
                            format = SqlServerSQLConstants.DEFAULT_INT_FORMAT;
                            break;
                        case 1:
                            format = SqlServerSQLConstants.DEFAULT_ONE_FORMAT;
                            break;
                        case 2:
                            format = SqlServerSQLConstants.DEFAULT_FLOAT_FORMAT;
                            break;
                        case 3:
                            format = SqlServerSQLConstants.DEFAULT_THREE_FORMAT;
                            break;
                        case 4:
                            format = SqlServerSQLConstants.DEFAULT_FOUR_FORMAT;
                            break;
                        default:
                            break;
                    }
                }
                //默认sum
                fieldName = String.format(SqlServerSQLConstants.SUM, String.format(SqlServerSQLConstants.CAST, originField,format));
            }
        }else {
            fieldName = String.format(SqlServerSQLConstants.CAST, originField, SqlServerSQLConstants.DEFAULT_INT_FORMAT);
        }
        return fieldName;
    }

    /**
     * 排除参数内的度量筛选字段条件，而不排除原有维度、图例字段筛选条件
     */
    public String transCustomFilterListNoMeasureFieldS(SQLObj tableObj, List<ChartFieldCustomFilterDTO> requestList,String measureField){
        //筛选条件为空
        if (CollectionUtils.isEmpty(requestList)) {
            return null;
        }
        List<String> res = new ArrayList<>();
        //遍历自定义过滤器
        for (ChartFieldCustomFilterDTO request : requestList) {
            List<SQLObj> list = new ArrayList<>();
            //数据集数据字段
            DatasetTableField field = request.getField();
            //排除度量字段
            if (ObjectUtils.isEmpty(field) || measureField.contains(field.getId())) {
                continue;
            }
            String whereName = "";
            String originName;
            //ExtField 没有额外字段 type -> type
            if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == DeTypeConstants.DE_INT) {
                // 解析origin name中有关联的字段生成sql表达式
                originName = calcFieldRegex(field.getOriginName(), tableObj);
            } else if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == DeTypeConstants.DE_TIME) {
                //带上表名  type -> phone.`type`
                originName = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            } else {
                //带上表名  type -> phone.`type`
                originName = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            }
            //判断de类型为 1
            if (field.getDeType() == DeTypeConstants.DE_TIME) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == DeTypeConstants.DE_STRING || field.getDeExtractType() == DeTypeConstants.DE_LOCATION) {
                    //时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(SqlServerSQLConstants.STRING_TO_DATE, originName, SqlServerSQLConstants.DATE_FORMAT);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == DeTypeConstants.DE_INT || field.getDeExtractType() == DeTypeConstants.DE_FLOAT || field.getDeExtractType() == DeTypeConstants.DE_BOOL) {
                    //转换 CAST(field AS DECIMAL(20,2))/1000 设置列的范围并且/1000
                    String cast = String.format(SqlServerSQLConstants.CAST, originName, SqlServerSQLConstants.DEFAULT_INT_FORMAT) + "/1000";
                    //转换时间戳为具体格式时间 FROM_UNIXTIME(CAST(field AS DECIMAL(20,2))/1000 ,'%Y-%m-%d %H:%i:%S')
                    whereName = String.format(SqlServerSQLConstants.FROM_UNIXTIME, cast, SqlServerSQLConstants.DATE_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == DeTypeConstants.DE_TIME) {
                    //默认 `tableAlias`.`fieldAlias`
                    whereName = originName;
                }
                //判断de类型为 2,3
            } else if (field.getDeType() == DeTypeConstants.DE_INT || field.getDeType() == DeTypeConstants.DE_FLOAT) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == DeTypeConstants.DE_STRING || field.getDeExtractType() == DeTypeConstants.DE_LOCATION) {
                    //转换 时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(SqlServerSQLConstants.CAST, originName, SqlServerSQLConstants.DEFAULT_FLOAT_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == DeTypeConstants.DE_TIME) {
                    //转化 UNIX_TIMESTAMP(originName) 直接得到参数对应的时间戳
                    whereName = String.format(SqlServerSQLConstants.UNIX_TIMESTAMP, originName);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == DeTypeConstants.DE_INT || field.getDeExtractType() == DeTypeConstants.DE_FLOAT || field.getDeExtractType() == DeTypeConstants.DE_BOOL) {
                    //默认 `tableAlias`.`fieldAlias`
                    whereName = originName;
                }
                //判断de类型为 1,2,3之外
            } else {
                //默认 `tableAlias`.`fieldAlias
                whereName = originName;
            }
            //获取到自定义过滤条件列表
            List<ChartCustomFilterItemDTO> filter = request.getFilter();
            //遍历自定义过滤条件列表
            for (ChartCustomFilterItemDTO filterItemDTO : filter) {
                String value = StringUtils.isEmpty(filterItemDTO.getValue()) ? " " : filterItemDTO.getValue();
                //判断 value 为null的时候 , 为 "=","!=" 的情况
                if (StringUtils.equalsIgnoreCase(value,"null")){
                    value = "";
                }
                String whereTerm = transFilterTerm (filterItemDTO.getTerm());
                String whereValue = "";
                //whereTerm 为 In 或者 Not In的情况
                if (whereTerm.contains("IN") || whereTerm.contains("in")){
                    whereValue = " ( '" + String.join("','", Arrays.asList(value.split(","))) + "' ) ";
                    //whereTerm 为 between 的情况
                }else if (whereTerm.contains("BETWEEN")){
                    String[] betweenValueS = value.split(",");
                    whereValue = String.format(SqlServerSQLConstants.WHERE_VALUE_BETWEEN,betweenValueS[0],betweenValueS[1]);
                } else {
                    if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "in")) {
                        whereValue = "('" + StringUtils.join(value, "','") + "')";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "LIKE")) {
                        whereValue = "'%" + value + "%'";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(),"lt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(),"le")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(),"gt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(),"ge")){
                        whereValue =  String.format(SqlServerSQLConstants.WHERE_VALUE_VALUE, value);;
                    }else {
                        //转换 `whereValue`
                        whereValue = String.format(SqlServerSQLConstants.WHERE_VALUE_VALUE, value);
                    }
                }
                list.add(SQLObj.builder()
                        .whereField(whereName)
                        .whereTermAndValue(whereTerm + whereValue)
                        .build());
            }
            List<String> strList = new ArrayList<>();
            list.forEach(ele -> strList.add(ele.getWhereField() + " " + ele.getWhereTermAndValue()));
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)) {
                res.add("(" + String.join(" " + getLogic(request.getLogic()) + " ", strList) + ")");
            }
        }
        return org.apache.commons.collections.CollectionUtils.isNotEmpty(res) ? "(" + String.join(" AND ", res) + ")" : null;
    }
    /**度量经过聚合运算之后进行having筛选*/
    public String havingFilterList(List<DatasetTableField> fields, List<ChartFieldCustomFilterDTO> requestList,String measureField) {
        if (CollectionUtils.isEmpty(requestList)) {
            return null;
        }
        //度量经过聚合运算之后进行having筛选
        List<String> res = new ArrayList<>();
        //遍历自定义过滤器
        for (ChartFieldCustomFilterDTO request : requestList) {
            List<SQLObj> list = new ArrayList<>();
            //数据集数据字段
            DatasetTableField field = request.getField();
            if (ObjectUtils.isEmpty(field) || !measureField.contains(field.getId())) {
                continue;
            }
            String whereName = null;
            for(DatasetTableField f:fields){
                if (StringUtils.equals(f.getId(),field.getId())){
                    whereName = f.getOriginName();
                    break;
                }
            }
            if (ObjectUtils.isEmpty(whereName)){
                continue;
            }
            //获取到自定义过滤条件列表
            List<ChartCustomFilterItemDTO> filter = request.getFilter();
            //遍历自定义过滤条件列表
            for (ChartCustomFilterItemDTO filterItemDTO : filter) {
                String value = StringUtils.isEmpty(filterItemDTO.getValue()) ? " " : filterItemDTO.getValue();
                //判断 value 为null的时候 , 为 "=","!=" 的情况
                if (StringUtils.equalsIgnoreCase(value,"null")){
                    value = "";
                }
                String whereTerm = transFilterTerm(filterItemDTO.getTerm());
                String whereValue = "";
                //whereTerm 为 In 或者 Not In的情况
                if (whereTerm.contains("IN") || whereTerm.contains("in")){
                    whereValue = " ( '" + String.join("','", Arrays.asList(value.split(","))) + "' ) ";
                    //whereTerm 为 between 的情况
                }else if (whereTerm.contains("BETWEEN")){
                    String[] betweenValueS = value.split(",");
                    whereValue = String.format(SqlServerSQLConstants.WHERE_VALUE_BETWEEN,betweenValueS[0],betweenValueS[1]);
                } else {
                    if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "in")) {
                        whereValue = "('" + StringUtils.join(value, "','") + "')";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "LIKE")) {
                        whereValue = "'%" + value + "%'";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(),"lt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(),"le")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(),"gt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(),"ge")){
                        whereValue =  String.format(SqlServerSQLConstants.WHERE_VALUE_BLANK, value);;
                    }else {
                        //转换 `whereValue`
                        whereValue = String.format(SqlServerSQLConstants.WHERE_VALUE_VALUE, value);
                    }
                }
                list.add(SQLObj.builder()
                        .whereField(whereName)
                        .whereTermAndValue(whereTerm + whereValue)
                        .build());
            }
            List<String> strList = new ArrayList<>();
            list.forEach(ele -> strList.add(ele.getWhereField() + " " + ele.getWhereTermAndValue()));
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)) {
                res.add("(" + String.join(" " + getLogic(request.getLogic()) + " ", strList) + ")");
            }
        }
        return CollectionUtils.isNotEmpty(res) ? "(" + String.join(" AND ", res) + ")" : null;
    }

    public String transCustomFilterList(SQLObj tableObj, List<ChartFieldCustomFilterDTO> requestList) {
        if (CollectionUtils.isEmpty(requestList)) {
            return null;
        }
        List<String> res = new ArrayList<>();
        for (ChartFieldCustomFilterDTO request : requestList) {
            List<SQLObj> list = new ArrayList<>();
            DatasetTableField field = request.getField();

            if (ObjectUtils.isEmpty(field)) {
                continue;
            }
            String whereName = "";
            String originName;
            if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 2) {
                // 解析origin name中有关联的字段生成sql表达式
                originName = calcFieldRegex(field.getOriginName(), tableObj);
            } else if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 1) {
                originName = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            } else {
                originName = String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            }
            if (field.getDeType() == 1) {
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    whereName = String.format(SqlServerSQLConstants.STRING_TO_DATE, originName);
                }
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
                    String cast = String.format(SqlServerSQLConstants.LONG_TO_DATE, originName + "/1000");
                    whereName = String.format(SqlServerSQLConstants.FROM_UNIXTIME, cast);
                }
                if (field.getDeExtractType() == 1) {
                    whereName = originName;
                }
            } else if (field.getDeType() == 2 || field.getDeType() == 3) {
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    whereName = String.format(SqlServerSQLConstants.CONVERT, SqlServerSQLConstants.DEFAULT_FLOAT_FORMAT, originName);
                }
                if (field.getDeExtractType() == 1) {
                    whereName = String.format(SqlServerSQLConstants.UNIX_TIMESTAMP, originName);
                }
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
                    whereName = originName;
                }
            } else {
                whereName = originName;
            }

            if (StringUtils.equalsIgnoreCase(request.getFilterType(), "enum")) {
                if (CollectionUtils.isNotEmpty(request.getEnumCheckField())) {
                    res.add("(" + whereName + " IN ('" + String.join("','", request.getEnumCheckField()) + "'))");
                }
            } else {
                List<ChartCustomFilterItemDTO> filter = request.getFilter();
                for (ChartCustomFilterItemDTO filterItemDTO : filter) {
                    String value = filterItemDTO.getValue();
                    String whereTerm = transFilterTerm(filterItemDTO.getTerm());
                    String whereValue = "";

                    if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "null")) {
                        whereValue = "";
                    } else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_null")) {
                        whereValue = "";
                    } else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "empty")) {
                        whereValue = "''";
                    } else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_empty")) {
                        whereValue = "''";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "in")) {
                        whereValue = "('" + StringUtils.join(value, "','") + "')";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "like")) {
                        whereValue = "'%" + value + "%'";
                    } else {
                        whereValue = String.format(SqlServerSQLConstants.WHERE_VALUE_VALUE, value);
                    }
                    list.add(SQLObj.builder()
                            .whereField(whereName)
                            .whereTermAndValue(whereTerm + whereValue)
                            .build());
                }

                List<String> strList = new ArrayList<>();
                list.forEach(ele -> strList.add(ele.getWhereField() + " " + ele.getWhereTermAndValue()));
                if (CollectionUtils.isNotEmpty(list)) {
                    res.add("(" + String.join(" " + getLogic(request.getLogic()) + " ", strList) + ")");
                }
            }
        }
        return CollectionUtils.isNotEmpty(res) ? "(" + String.join(" AND ", res) + ")" : null;
    }
    public String transFilterTerm(String term) {
        switch (term) {
            case "eq":
                return " = ";
            case "not_eq":
                return " <> ";
            case "lt":
                return " < ";
            case "le":
                return " <= ";
            case "gt":
                return " > ";
            case "ge":
                return " >= ";
            case "in":
                return " IN ";
            case "not in":
                return " NOT IN ";
            case "like":
                return " LIKE ";
            case "not like":
                return " NOT LIKE ";
            case "null":
                return " IS NULL ";
            case "not_null":
                return " IS NOT NULL ";
            case "empty":
                return " = ";
            case "not_empty":
                return " <> ";
            case "between":
                return " BETWEEN ";
            default:
                return "";
        }
    }

    private String calcFieldRegex(String originField, SQLObj tableObj) {
        originField = originField.replaceAll("[\\t\\n\\r]]", "");
        // 正则提取[xxx]
        String regex = "\\[(.*?)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originField);
        Set<String> ids = new HashSet<>();
        while (matcher.find()) {
            String id = matcher.group(1);
            ids.add(id);
        }
        if (CollectionUtils.isEmpty(ids)) {
            return originField;
        }
        LambdaQueryWrapper<DatasetTableField> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DatasetTableField::getId, new ArrayList<>(ids));
        List<DatasetTableField> calcFields = datasetTableFieldMapper.selectList(queryWrapper);
//        DatasetTableFieldExample datasetTableFieldExample = new DatasetTableFieldExample();
//        datasetTableFieldExample.createCriteria().andIdIn(new ArrayList<>(ids));
//        List<DatasetTableField> calcFields = datasetTableFieldMapper.selectByExample(datasetTableFieldExample);
        for (DatasetTableField ele : calcFields) {
            originField = originField.replaceAll("\\[" + ele.getId() + "]",
                    String.format(SqlServerSQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), ele.getOriginName()));
        }
        return originField;
    }

}
