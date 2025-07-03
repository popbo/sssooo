package com.stdc.visual.dynamic.provider.query.kingbase;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.dynamic.base.chart.dto.ChartCustomFilterItemDTO;
import com.stdc.visual.dynamic.base.chart.dto.ChartFieldCustomFilterDTO;
import com.stdc.visual.dynamic.base.dataset.dto.condition.DateFormatCondition;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import com.stdc.visual.dynamic.base.sqlobj.SQLObj;
import com.stdc.visual.dynamic.mapper.DatasetTableFieldMapper;
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

import static com.stdc.visual.dynamic.provider.query.SQLConstants.TABLE_ALIAS_PREFIX;

/**
 * @author: wang_jie
 * @data: 2022/5/16--21:01
 * @describe: mysql 查询sql 生产者
 */
@Service("kingbaseQuery")
public class KingBaseQueryProvider extends QueryProvider {

    @Resource
    private DatasetTableFieldMapper datasetTableFieldMapper;

    @Override
    public Integer transFieldType(String field) {
        switch (field) {
            case "CHAR":
            case "VARCHAR":
            case "TEXT":
            case "TINYTEXT":
            case "MEDIUMTEXT":
            case "LONGTEXT":
            case "ENUM":
                return 0;// 文本
            case "DATE":
            case "TIME":
            case "YEAR":
            case "DATETIME":
            case "TIMESTAMP":
                return 1;// 时间
            case "INT":
            case "SMALLINT":
            case "MEDIUMINT":
            case "INTEGER":
            case "BIGINT":
                return 2;// 整型
            case "FLOAT":
            case "DOUBLE":
            case "DECIMAL":
                return 3;// 浮点
            case "BIT":
            case "TINYINT":
                return 4;// 布尔
            default:
                return 0;
        }
    }

    @Override
    public String createSQLPreview(String sql, String orderBy) {
        return "SELECT * FROM (" + sqlFix(sql) + ") AS tmp " + " LIMIT 0,1000";
    }

    @Override
    public String createQuerySQL(DataSetTableRequest request, String table, List<DatasetTableField> fieldsCopy, boolean isGroup,Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilterCopy) {
        List<DatasetTableField> fields = deepCopy(fieldsCopy);
        List<ChartFieldCustomFilterDTO> fieldCustomFilter = deepCopy(fieldCustomFilterCopy);
        SQLObj tableObj = SQLObj.builder()
                .tableName((table.startsWith("(") && table.endsWith(")")) ? table : String.format(KingBaseConstants.KEYWORD_TABLE, table))
                .tableAlias(String.format(TABLE_ALIAS_PREFIX, 0))
                .build();
        List<SQLObj> xFields = new ArrayList<>();
        List<SQLObj> groupFields = new ArrayList<>();
        /**获取查询条件*/
        /***维度*/
        String dimensionField = request.getDimensionField();
        /***度量*/
        String measureField = request.getMeasureField();
        /***图例*/
        String legendField = request.getLegendField();
        /**维度、度量、图例 均为""时，使用不含自定义方法创建sql
         * ps：2022/03/04 11:06 发现此处有逻辑 bug , 维度、度量、图例 均为""时 应该也带上where子句条件
         * */
        if (StringUtils.isEmpty(dimensionField) && StringUtils.isEmpty(measureField) && StringUtils.isEmpty(legendField)) {
            return createQuerySqlNoFilter(table, fields, isGroup, fieldCustomFilterCopy);
        }
        /**sql查询的字段集合*/
        String ids = dimensionField + StringPool.COMMA + legendField;
        if (CollectionUtils.isNotEmpty(fields)) {
            for (int i = 0; i < fields.size(); i++) {
                DatasetTableField f = fields.get(i);
                //只查询维度、度量、图例字段
                if (!request.getFieldIDS().contains(f.getId())){
                    continue;
                }
                String originField;
                if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 2) {
                    // 解析origin name中有关联的字段生成sql表达式
                    originField = calcFieldRegex(f.getOriginName(), tableObj);
                } else if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 1) {
                    originField = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                } else {
                    originField = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                }
                String fieldAlias = String.format(SQLConstants.FIELD_ALIAS_X_PREFIX, i);
                String fieldName = "";
                //处理自定义时间格式
                //设置时间分组
//                if (ObjectUtils.isNotEmpty(request)
//                        && ObjectUtils.isNotEmpty(request.getFieldCondition())
//                        && CollectionUtils.isNotEmpty(request.getFieldCondition().getDateFormatConditionS())) {
//                    for (DateFormatCondition dateFormatCondition : request.getFieldCondition().getDateFormatConditionS()) {
//                        //设置dataFormat，自定义时间格式
//                        if (StringUtils.equals(f.getId(), dateFormatCondition.getFieldId())) {
//                            //设置分组粒度
//                            String dataFieldName = originField;
//                            //按照分组进行
//                            switch (dateFormatCondition.getGranularity()) {
//                                case DateFormatCondition.YEAR:
//                                    dataFieldName = String.format(MySQLConstants.DATE_FORMAT, originField, MySQLConstants.YEAR_DATE_FORMAT);
//                                    break;
//                                case DateFormatCondition.QUARTER:
//                                    dataFieldName = String.format(MySQLConstants.QUARTER_DATE_FORMAT, originField);
//                                    break;
//                                case DateFormatCondition.MONTH:
//                                    dataFieldName = String.format(MySQLConstants.DATE_FORMAT, originField, MySQLConstants.MONTH_DATE_FORMAT);
//                                    break;
//                                case DateFormatCondition.WEEK:
//                                    dataFieldName = String.format(MySQLConstants.DATE_FORMAT, originField, MySQLConstants.WEEK_DATE_FORMAT);
//                                    break;
//                                case DateFormatCondition.DAY:
//                                    dataFieldName = String.format(MySQLConstants.DATE_FORMAT, originField, MySQLConstants.DAY_DATE_FORMAT);
//                                    break;
//                                case DateFormatCondition.HOUR:
//                                    dataFieldName = String.format(MySQLConstants.DATE_FORMAT, originField, MySQLConstants.HOUR_DATE_FORMAT);
//                                    break;
//                                case DateFormatCondition.ACCURATE:
//                                    dataFieldName = String.format(MySQLConstants.DATE_FORMAT, originField, MySQLConstants.DEFAULT_DATE_FORMAT);
//                                    break;
//                                default:
//                                    break;
//                            }
//                            groupFields.add(SQLObj.builder()
//                                    .fieldName(dataFieldName)
//                                    .fieldAlias(fieldAlias)
//                                    .build());
//                        }
//                    }
//                }
                // 处理横轴字段
                //设置时间展示格式
                String dataFormat = KingBaseConstants.DAY_DATE_FORMAT;
                //是否需要替换
                Boolean isNeedReplace = false;
                if (ObjectUtils.isNotEmpty(request)
                        && ObjectUtils.isNotEmpty(request.getFieldCondition())
                        && CollectionUtils.isNotEmpty(request.getFieldCondition().getDateFormatConditionS())) {
                    for (DateFormatCondition DateFormatCondition : request.getFieldCondition().getDateFormatConditionS()) {
                        //设置dataFormat，自定义时间格式
                        if (StringUtils.equals(f.getId(), DateFormatCondition.getFieldId())) {
                            //修改抽取类型
                            f.setDeExtractType(0);
                            dataFormat = DateFormatCondition.getFormat();
                            //数据格式为 “季度、周” 的情况
                            if (StringUtils.equals(DateFormatCondition.QUARTER, DateFormatCondition.getGranularity())
                                    || StringUtils.equals(DateFormatCondition.WEEK, DateFormatCondition.getGranularity())) {
                                isNeedReplace = true;
                            }
                        }
                    }
                }
                if (f.getDeExtractType() == 1) {
                    if (f.getDeType() == 2 || f.getDeType() == 3) {
                        fieldName = String.format(KingBaseConstants.UNIX_TIMESTAMP, originField) + "*1000";
                    } else {
                        fieldName = originField;
                    }
                } else if (f.getDeExtractType() == 0) {
                    if (f.getDeType() == 2) {
                        fieldName = transPol(request, f, originField);
                    } else if (f.getDeType() == 3) {
                        fieldName = transPol(request, f, originField);
                    } else if (f.getDeType() == 1) {
//						fieldName = String.format(MySQLConstants.STR_TO_DATE, originField, dataFormat);
                        if (isNeedReplace && StringUtils.isNotBlank(dataFormat)) {
                            //数据格式为 “季度” 的情况
                            fieldName = String.format(KingBaseConstants.CAST, String.format(KingBaseConstants.QUARTER_DATE_FORMAT, originField), "CHAR");
                            fieldName = String.format(KingBaseConstants.REPLACE, dataFormat, "%s", fieldName);
                        } else {
                            fieldName = String.format(KingBaseConstants.DATE_FORMAT, originField, dataFormat);
                        }
                    } else {
                        fieldName = originField;
                    }
                } else {
                    if (f.getDeType() == 1) {
                        String cast = transPol(request, f, originField);
                        fieldName = String.format(KingBaseConstants.FROM_UNIXTIME, cast, KingBaseConstants.DEFAULT_DATE_FORMAT);
                    } else if (f.getDeType() == 2) {
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
                if (ids.contains(f.getId())) {
                    if (!fieldName.contains("SUM") && f.getDeType() != 1) {
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
        if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("customGroups", groupFields);
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);
        //除度量以外的条件 放到where子句中
        String customWheres = transCustomFilterListNoMeasureFieldS(tableObj, fieldCustomFilter, measureField);
        if (ObjectUtils.isNotEmpty(customWheres)) {
            List<String> wheres = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(customWheres)) wheres.add(customWheres);
            if (CollectionUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
        }
        //度量条件 放到having子句中
        String havingS = havingFilterList(fields, fieldCustomFilter, measureField);
        if (ObjectUtils.isNotEmpty(havingS)) {
            List<String> having = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(havingS)) {
                st_sql.add("isHaving", true);
                having.add(havingS);
            }
            if (CollectionUtils.isNotEmpty(having)) st_sql.add("havings", having);
        }
        return st_sql.render();
    }

    /**
     * 初步创建sql，添加维度，图例分组，并且除 度量以外的 数值型 查询条件
     */
    @Override
    public String createQuerySQL(String table, List<DatasetTableField> fieldsCopy, boolean isGroup,Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilterCopy) {
        List<DatasetTableField> fields = deepCopy(fieldsCopy);
        List<ChartFieldCustomFilterDTO> fieldCustomFilter = deepCopy(fieldCustomFilterCopy);
        SQLObj tableObj = SQLObj.builder()
                .tableName((table.startsWith("(") && table.endsWith(")")) ? table : String.format(KingBaseConstants.KEYWORD_TABLE, table))
                .tableAlias(String.format(TABLE_ALIAS_PREFIX, 0))
                .build();
        List<SQLObj> xFields = new ArrayList<>();
        List<SQLObj> groupFields = new ArrayList<>();
        /***维度*/
        String dimensionField = "";
        /***度量*/
        String measureField = "";
        /***图例*/
        String legendField = "";
        DataSetTableRequest superRequest = null;
        /**获取查询条件*/
        if (CollectionUtils.isNotEmpty(fieldCustomFilter)){
            superRequest = fieldCustomFilter.get(0).getSuperRequest();
            dimensionField = superRequest.getDimensionField();
            measureField = superRequest.getMeasureField();
            legendField = superRequest.getLegendField();
        }
        /**维度、度量、图例 均为""时，使用不含自定义方法创建sql
         * ps：2022/03/04 11:06 发现此处有逻辑 bug , 维度、度量、图例 均为""时 应该也带上where子句条件
         * */
        if (StringUtils.isEmpty(dimensionField) && StringUtils.isEmpty(measureField) && StringUtils.isEmpty(legendField)){
            return createQuerySqlNoFilter(table,fields,isGroup,fieldCustomFilterCopy);
        }
        /**sql查询的字段集合*/
        List<String> ids = new ArrayList<>();
        ids.add(dimensionField);
        ids.add(legendField);
        if (CollectionUtils.isNotEmpty(fields)) {
            for (int i = 0; i < fields.size(); i++) {
                DatasetTableField f = fields.get(i);
                //只查询维度、度量、图例字段
                if (!superRequest.getFieldIDS().contains(f.getId())){
                    continue;
                }
                String originField;
                if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 2) {
                    // 解析origin name中有关联的字段生成sql表达式
                    originField = calcFieldRegex(f.getOriginName(), tableObj);
                } else if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 1) {
                    originField = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                } else {
                    originField = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                }
                String fieldAlias = String.format(SQLConstants.FIELD_ALIAS_X_PREFIX, i);
                String fieldName = "";
                //处理自定义时间格式
                //设置时间分组
                if(ObjectUtils.isNotEmpty(superRequest)
                        && ObjectUtils.isNotEmpty(superRequest.getFieldCondition())
                        && CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getDateFormatConditionS())){
                    for (DateFormatCondition dateFormatCondition: superRequest.getFieldCondition().getDateFormatConditionS()) {
                        //设置dataFormat，自定义时间格式
                        if (StringUtils.equals(f.getId(), dateFormatCondition.getFieldId())){
                            //设置分组粒度
                            String dataFieldName = originField;
                            //按照分组进行
                            switch (dateFormatCondition.getGranularity()){
                                case DateFormatCondition.YEAR:
                                    dataFieldName = String.format(KingBaseConstants.DATE_FORMAT, originField, KingBaseConstants.YEAR_DATE_FORMAT);
                                    break;
                                case DateFormatCondition.QUARTER:
                                    dataFieldName = String.format(KingBaseConstants.QUARTER_DATE_FORMAT, originField);
                                    break;
                                case DateFormatCondition.MONTH:
                                    dataFieldName = String.format(KingBaseConstants.DATE_FORMAT, originField, KingBaseConstants.MONTH_DATE_FORMAT);
                                    break;
                                case DateFormatCondition.WEEK:
                                    dataFieldName = String.format(KingBaseConstants.DATE_FORMAT, originField, KingBaseConstants.WEEK_DATE_FORMAT);
                                    break;
                                case DateFormatCondition.DAY:
                                    dataFieldName = String.format(KingBaseConstants.DATE_FORMAT, originField, KingBaseConstants.DAY_DATE_FORMAT);
                                    break;
                                case DateFormatCondition.HOUR:
                                    dataFieldName = String.format(KingBaseConstants.DATE_FORMAT, originField, KingBaseConstants.HOUR_DATE_FORMAT);
                                    break;
                                case DateFormatCondition.ACCURATE:
                                    dataFieldName = String.format(KingBaseConstants.DATE_FORMAT, originField, KingBaseConstants.DEFAULT_DATE_FORMAT);
                                    break;
                                default:
                                    break;
                            }
                            groupFields.add(SQLObj.builder()
                                    .fieldName(dataFieldName)
                                    .fieldAlias(fieldAlias)
                                    .build());
                        }
                    }
                }
                // 处理横轴字段
                //设置时间展示格式
                String dataFormat = KingBaseConstants.DAY_DATE_FORMAT;
                //是否需要替换
                Boolean isNeedReplace = false;
                if(ObjectUtils.isNotEmpty(superRequest)
                        && ObjectUtils.isNotEmpty(superRequest.getFieldCondition())
                        && CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getDateFormatConditionS())){
                    for (DateFormatCondition dateFormatCondition: superRequest.getFieldCondition().getDateFormatConditionS()) {
                        //设置dataFormat，自定义时间格式
                        if (StringUtils.equals(f.getId(), dateFormatCondition.getFieldId())){
                            //修改抽取类型
                            f.setDeExtractType(0);
                            //设置分组粒度
                            String dataFieldName = originField;
                            dataFormat = dateFormatCondition.getFormat();
                            if (!StringUtil.hasText(dataFormat)){
                                //按照分组进行
                                switch (dateFormatCondition.getGranularity()){
                                    case DateFormatCondition.YEAR:
                                        dataFormat = KingBaseConstants.YEAR_DATE_FORMAT;
                                        break;
                                    case DateFormatCondition.QUARTER:
                                        dataFormat = String.format(KingBaseConstants.QUARTER_DATE_FORMAT, originField);
                                        break;
                                    case DateFormatCondition.MONTH:
                                        dataFormat = KingBaseConstants.MONTH_DATE_FORMAT;
                                        break;
                                    case DateFormatCondition.WEEK:
                                        dataFormat = KingBaseConstants.WEEK_DATE_FORMAT;
                                        break;
                                    case DateFormatCondition.DAY:
                                        dataFormat = KingBaseConstants.DAY_DATE_FORMAT;
                                        break;
                                    case DateFormatCondition.HOUR:
                                        dataFormat = KingBaseConstants.HOUR_DATE_FORMAT;
                                        break;
                                    case DateFormatCondition.MINUTE:
                                        dataFormat = KingBaseConstants.MINUTE_DATE_FORMAT;
                                        break;
                                    case DateFormatCondition.ACCURATE:
                                        dataFormat = KingBaseConstants.DEFAULT_DATE_FORMAT;
                                        break;
                                    default:
                                        break;
                                }
                            }
                            //数据格式为 “季度、周” 的情况
                            if (StringUtils.equals(DateFormatCondition.QUARTER, dateFormatCondition.getGranularity())
                                    || StringUtils.equals(DateFormatCondition.WEEK, dateFormatCondition.getGranularity())) {
                                isNeedReplace = true;
                            }
                        }
                    }
                }
                if (f.getDeExtractType() == 1) {
                    if (f.getDeType() == 2 || f.getDeType() == 3) {
                        fieldName = String.format(KingBaseConstants.UNIX_TIMESTAMP, originField) + "*1000";
                    } else {
                        fieldName = originField;
                    }
                } else if (f.getDeExtractType() == 0) {
                    if (f.getDeType() == 2) {
                        fieldName = transPol(superRequest, f, originField);
                    } else if (f.getDeType() == 3) {
                        fieldName = transPol(superRequest, f, originField);
                    } else if (f.getDeType() == 1) {
//						fieldName = String.format(MySQLConstants.STR_TO_DATE, originField, dataFormat);
                        if (isNeedReplace && StringUtils.isNotBlank(dataFormat)){
                            //数据格式为 “季度” 的情况
                            fieldName = String.format(KingBaseConstants.CAST,String.format(KingBaseConstants.QUARTER_DATE_FORMAT, originField),"CHAR");
                            fieldName = String.format(KingBaseConstants.REPLACE,dataFormat,"%s",fieldName);
                        }else {
                            fieldName = String.format(KingBaseConstants.DATE_FORMAT, originField, dataFormat);
                        }
                    } else {
                        fieldName = originField;
                    }
                } else {
                    if (f.getDeType() == 1) {
                        String cast = transPol(superRequest, f, originField);
                        fieldName = String.format(KingBaseConstants.FROM_UNIXTIME, cast, KingBaseConstants.DEFAULT_DATE_FORMAT);
                    } else if (f.getDeType() == 2) {
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
                if (ids.contains(f.getId())){
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
        if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("customGroups", groupFields);
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);
        //除度量以外的条件 放到where子句中
        String customWheres = transCustomFilterListNoMeasureFieldS(tableObj, fieldCustomFilter,measureField);
        if (ObjectUtils.isNotEmpty(customWheres)){
            List<String> wheres = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(customWheres)) wheres.add(customWheres);
            if (CollectionUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
        }
        //度量条件 放到having子句中
        String havingS = havingFilterList(fields, fieldCustomFilter,measureField);
        if (ObjectUtils.isNotEmpty(havingS)){
            List<String> having = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(havingS)){
                st_sql.add("isHaving", true);
                having.add(havingS);
            }
            if (CollectionUtils.isNotEmpty(having)) st_sql.add("havings", having);
        }
        return st_sql.render();
    }
    /**
     * 第二步创建sql，sql经过维度，图例分组，度量sum()之后，对度量进行条件查询
     */
//    public String creatSqlWithMeasureFilter(String table, List<DatasetTableField> fields, boolean isGroup, Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilter){
//		SQLObj tableObj = SQLObj.builder()
//			.tableName((table.startsWith("(") && table.endsWith(")")) ? table : String.format(MySQLConstants.KEYWORD_TABLE, table))
//			.tableAlias(String.format(TABLE_ALIAS_PREFIX, 1))
//			.build();
//		List<SQLObj> xFields = new ArrayList<>();
//		/***度量*/
//		String measureField = CollectionUtils.isNotEmpty(fieldCustomFilter)?fieldCustomFilter.get(0).getSuperRequest().getMeasureField():"";
//		if (CollectionUtils.isNotEmpty(fields)) {
//			for (int i = 0; i < fields.size(); i++) {
//				DatasetTableField f = fields.get(i);
//				String originField;
//				if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 2) {
//					// 解析origin name中有关联的字段生成sql表达式
//					originField = calcFieldRegex(f.getOriginName(), tableObj);
//				} else if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 1) {
//					originField = String.format(MySQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
//				} else {
//					originField = String.format(MySQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
//				}
//				String fieldAlias = String.format(SQLConstants.FIELD_ALIAS_Y_PREFIX, i);
//				String fieldName = "";
//				// 处理横轴字段
//				//设置时间展示格式
//				String dataFormat = MySQLConstants.DAY_DATE_FORMAT;
//				DataSetTableRequest superRequest = fieldCustomFilter.get(0).getSuperRequest();
//				//是否需要重
//				Boolean isNeedReplace = false;
//				if(ObjectUtils.isNotEmpty(superRequest)
//					&& ObjectUtils.isNotEmpty(superRequest.getFieldCondition())
//					&& CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getDateFormatConditionS())){
//					for (DateFormatCondition DateFormatCondition: superRequest.getFieldCondition().getDateFormatConditionS()) {
//						//设置dataFormat，自定义时间格式
//						if (StringUtils.equals(f.getId(), DateFormatCondition.getFieldId())){
//							//修改抽取类型
//							f.setDeExtractType(0);
//							dataFormat = DateFormatCondition.getFormat();
//							//数据格式为 “季度、周” 的情况
//							if (StringUtils.equals(DateFormatCondition.QUARTER, DateFormatCondition.getGranularity())
//									|| StringUtils.equals(DateFormatCondition.WEEK, DateFormatCondition.getGranularity())) {
//								isNeedReplace = true;
//							}
//						}
//					}
//				}
//				if (f.getDeExtractType() == 1) {
//					if (f.getDeType() == 2 || f.getDeType() == 3) {
//						fieldName = String.format(MySQLConstants.UNIX_TIMESTAMP, originField) + "*1000";
//					} else {
//						fieldName = originField;
//					}
//				} else if (f.getDeExtractType() == 0) {
//					if (f.getDeType() == 2) {
//						fieldName =String.format(MySQLConstants.CAST, originField, MySQLConstants.DEFAULT_INT_FORMAT);
//					} else if (f.getDeType() == 3) {
//						fieldName = String.format(MySQLConstants.CAST, originField, MySQLConstants.DEFAULT_INT_FORMAT);
//					} else if (f.getDeType() == 1) {
////						fieldName = String.format(MySQLConstants.STR_TO_DATE, originField, MySQLConstants.DEFAULT_DATE_FORMAT);
//						if (isNeedReplace && StringUtils.isNotBlank(dataFormat)){
//							//数据格式为 “季度” 的情况
//							fieldName = String.format(MySQLConstants.CAST,String.format(MySQLConstants.QUARTER_DATE_FORMAT, originField),"CHAR");
//							fieldName = String.format(MySQLConstants.REPLACE,dataFormat,"%s",fieldName);
//						}else {
//							fieldName = String.format(MySQLConstants.DATE_FORMAT, originField, dataFormat);
//						}
//
//					} else {
//						fieldName = originField;
//					}
//				} else {
//					if (f.getDeType() == 1) {
//						String cast = String.format(MySQLConstants.CAST, originField, MySQLConstants.DEFAULT_INT_FORMAT)+ "/1000";
//						fieldName = String.format(MySQLConstants.FROM_UNIXTIME, cast, MySQLConstants.DEFAULT_DATE_FORMAT);
//					} else if (f.getDeType() == 2) {
//						fieldName = String.format(MySQLConstants.CAST, originField, MySQLConstants.DEFAULT_INT_FORMAT);
//					} else {
//						fieldName = originField;
//					}
//				}
//				xFields.add(SQLObj.builder()
//					.fieldName(fieldName)
//					.fieldAlias(fieldAlias)
//					.build());
//			}
//		}
//		STGroup stg = new STGroupFile(SQLConstants.SQL_TEMPLATE);
//		ST st_sql = stg.getInstanceOf("previewSql");
//		st_sql.add("isGroup", isGroup);
//		if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("groups", xFields);
//		if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);
//		/**修改别名*/
//		for (int i = 0; i < fieldCustomFilter.size(); i++) {
//			for (DatasetTableField field : fields) {
//				if (ObjectUtils.isNotEmpty(fieldCustomFilter.get(i).getField())  && StringUtils.equals(field.getId(), fieldCustomFilter.get(i).getField().getId())) {
//					fieldCustomFilter.get(i).getField().setOriginName(field.getOriginName());
//				}
//			}
//		}
//		/**只添加度量条件到新sql中*/
//		String customWheres = transCustomFilterListOnLyMeasureFieldS(tableObj,fieldCustomFilter,measureField);
//		if (ObjectUtils.isNotEmpty(customWheres)){
//			List<String> wheres = new ArrayList<>();
//			if (ObjectUtils.isNotEmpty(customWheres)) wheres.add(customWheres);
//			if (CollectionUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
//		}
//		return st_sql.render();
//	}

    /**
     * 最初创建sql方法备份
     */
    public String createQuerySqlNoFilter(String table, List<DatasetTableField> fields, boolean isGroup, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        SQLObj tableObj = SQLObj.builder()
                .tableName((table.startsWith("(") && table.endsWith(")")) ? table : String.format(KingBaseConstants.KEYWORD_TABLE, table))
                .tableAlias(String.format(TABLE_ALIAS_PREFIX, 0))
                .build();
        Map<String, String> whereMap = new HashMap<>(fields.size());
        List<SQLObj> xFields = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fields)) {
            for (int i = 0; i < fields.size(); i++) {
                DatasetTableField f = fields.get(i);
                String originField;
                if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 2) {
                    // 解析origin name中有关联的字段生成sql表达式
                    originField = calcFieldRegex(f.getOriginName(), tableObj);
                } else if (ObjectUtils.isNotEmpty(f.getExtField()) && f.getExtField() == 1) {
                    originField = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                } else {
                    originField = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), f.getOriginName());
                }
                //收集where子句条件
                whereMap.put(f.getDataeaseName(), originField);
                String fieldAlias = String.format(SQLConstants.FIELD_ALIAS_X_PREFIX, i);
                String fieldName = "";
                // 处理横轴字段
                if (f.getDeExtractType() == 1) {
                    if (f.getDeType() == 2 || f.getDeType() == 3) {
                        fieldName = String.format(KingBaseConstants.UNIX_TIMESTAMP, originField) + "*1000";
                    } else {
                        fieldName = originField;
                    }
                } else if (f.getDeExtractType() == 0) {
                    if (f.getDeType() == 2) {
                        fieldName = String.format(KingBaseConstants.CAST, originField, KingBaseConstants.DEFAULT_INT_FORMAT);
                    } else if (f.getDeType() == 3) {
                        fieldName = String.format(KingBaseConstants.CAST, originField, KingBaseConstants.DEFAULT_FLOAT_FORMAT);
                    } else if (f.getDeType() == 1) {
                        fieldName = String.format(KingBaseConstants.STR_TO_DATE, originField, KingBaseConstants.DEFAULT_DATE_FORMAT);
                    } else {
                        fieldName = originField;
                    }
                } else {
                    if (f.getDeType() == 1) {
                        String cast = String.format(KingBaseConstants.CAST, originField, KingBaseConstants.DEFAULT_INT_FORMAT) + "/1000";
                        fieldName = String.format(KingBaseConstants.FROM_UNIXTIME, cast, KingBaseConstants.DEFAULT_DATE_FORMAT);
                    } else if (f.getDeType() == 2) {
                        fieldName = String.format(KingBaseConstants.CAST, originField, KingBaseConstants.DEFAULT_INT_FORMAT);
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
        String customWheres = transCustomFilterListDefault(tableObj, fieldCustomFilter);
        STGroup stg = new STGroupFile(SQLConstants.SQL_TEMPLATE);
        ST st_sql = stg.getInstanceOf("previewSql");
        st_sql.add("isGroup", isGroup);
        if (CollectionUtils.isNotEmpty(xFields)) st_sql.add("groups", xFields);
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);
        if (ObjectUtils.isNotEmpty(customWheres)) {
            List<String> wheres = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(customWheres)) wheres.add(customWheres);
            if (CollectionUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
        }
        return st_sql.render();
    }

    @Override
    public String createQuerySQLAsTmp(String sql, List<DatasetTableField> fields, boolean isGroup, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        return createQuerySQL("(" + sqlFix(sql) + ")", fields, isGroup,null, fieldCustomFilter);
    }

    @Override
    public String createQueryTableWithPage(String table, List<DatasetTableField> fields, Integer page, Integer pageSize, Integer realSize, boolean isGroup, Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        return createQuerySQL(table, fields, isGroup,null, fieldCustomFilter) + " LIMIT " + (page - 1) * pageSize + "," + realSize;
    }

    @Override
    public String createQueryTableWithLimit(String table, List<DatasetTableField> fields, Integer limit, boolean isGroup, Datasource ds, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        return createQuerySQL(table, fields, isGroup,null, fieldCustomFilter) + " LIMIT 0," + limit;
    }

    @Override
    public String createQuerySqlWithLimit(String sql, List<DatasetTableField> fields, Integer limit, boolean isGroup, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        return createQuerySQLAsTmp(sql, fields, isGroup, fieldCustomFilter) + " LIMIT 0," + limit;
    }

    @Override
    public String searchTable(String table) {
        return "SELECT table_name FROM information_schema.TABLES WHERE table_name ='" + table + "'";
    }

    @Override
    public String createQuerySQLWithPage(String sql, List<DatasetTableField> fields, Integer page, Integer pageSize, Integer realSize, boolean isGroup, List<ChartFieldCustomFilterDTO> fieldCustomFilter) {
        return createQuerySQLAsTmp(sql, fields, isGroup, fieldCustomFilter) + " LIMIT " + (page - 1) * pageSize + "," + realSize;
    }

    @Override
    public String convertTableToSql(String tableName, Datasource ds) {
        return createSQLPreview("SELECT * FROM " + String.format(KingBaseConstants.KEYWORD_TABLE, tableName), null);
    }

    public String transMysqlFilterTerm(String term) {
        switch (term) {
            case "eq":
                return " = ";
            case "ne":
                return " != ";
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
            case "not between":
                return " NOT BETWEEN ";
            default:
                return term;
        }
    }

    /**
     * 修改度量条件 将where子句中的度量条件  添加格式 比如默认加上sum()方法
     */
    public String transCustomFilterListWithMeasureFieldS(SQLObj tableObj, List<ChartFieldCustomFilterDTO> requestList, String measureField) {
        if (CollectionUtils.isEmpty(requestList)) {
            return null;
        }
        List<String> res = new ArrayList<>();
        //遍历自定义过滤器
        for (ChartFieldCustomFilterDTO request : requestList) {
            List<SQLObj> list = new ArrayList<>();
            //数据集数据字段
            DatasetTableField field = request.getField();
            if (ObjectUtils.isEmpty(field)) {
                continue;
            }
            String whereName = "";
            String originName;
            //ExtField 没有额外字段 type -> type
            if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 2) {
                // 解析origin name中有关联的字段生成sql表达式
                originName = calcFieldRegex(field.getOriginName(), tableObj);
            } else if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 1) {
                //带上表名  type -> phone.`type`
                originName = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            } else {
                //带上表名  type -> phone.`type`
                originName = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            }
            //判断de类型为 1
            if (field.getDeType() == 1) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    //时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(KingBaseConstants.STR_TO_DATE, originName, KingBaseConstants.DEFAULT_DATE_FORMAT);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
                    //转换 CAST(field AS DECIMAL(20,2))/1000 设置列的范围并且/1000
                    String cast = String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_INT_FORMAT) + "/1000";
                    //转换时间戳为具体格式时间 FROM_UNIXTIME(CAST(field AS DECIMAL(20,2))/1000 ,'%Y-%m-%d %H:%i:%S')
                    whereName = String.format(KingBaseConstants.FROM_UNIXTIME, cast, KingBaseConstants.DEFAULT_DATE_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == 1) {
                    if (measureField.contains(field.getId())) {
                        //默认 `tableAlias`.`fieldAlias`
                        whereName = String.format(KingBaseConstants.SUM, String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_INT_FORMAT));
                    } else {
                        //默认 `tableAlias`.`fieldAlias`
                        whereName = originName;
                    }
                }
                //判断de类型为 2,3
            } else if (field.getDeType() == 2 || field.getDeType() == 3) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    if (measureField.contains(field.getId())) {
                        //默认 `tableAlias`.`fieldAlias`
                        whereName = String.format(KingBaseConstants.SUM, String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_INT_FORMAT));
                    } else {
                        //转换 时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                        whereName = String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_FLOAT_FORMAT);
                    }
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == 1) {
                    //转化 UNIX_TIMESTAMP(originName) 直接得到参数对应的时间戳
                    whereName = String.format(KingBaseConstants.UNIX_TIMESTAMP, originName);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
                    if (measureField.contains(field.getId())) {
                        //默认 `tableAlias`.`fieldAlias`
                        whereName = String.format(KingBaseConstants.SUM, String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_INT_FORMAT));
                    } else {
                        //默认 `tableAlias`.`fieldAlias`
                        whereName = originName;
                    }
                }
                //判断de类型为 1,2,3之外
            } else {
                if (measureField.contains(field.getId())) {
                    //默认 `tableAlias`.`fieldAlias`
                    whereName = String.format(KingBaseConstants.SUM, String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_INT_FORMAT));
                } else {
                    //默认 `tableAlias`.`fieldAlias
                    whereName = originName;
                }
            }
            //获取到自定义过滤条件列表
            List<ChartCustomFilterItemDTO> filter = request.getFilter();
            //遍历自定义过滤条件列表
            for (ChartCustomFilterItemDTO filterItemDTO : filter) {
                String value = StringUtils.isEmpty(filterItemDTO.getValue()) ? " " : filterItemDTO.getValue();
                //判断 value 为null的时候 , 为 "=","!=" 的情况
                if (StringUtils.equalsIgnoreCase(value, "null")) {
                    value = "";
                }
                String whereTerm = transMysqlFilterTerm(filterItemDTO.getTerm());
                String whereValue = "";
                //whereTerm 为 In 或者 Not In的情况
                if (whereTerm.contains("IN") || whereTerm.contains("in")) {
                    whereValue = " ( '" + String.join("','", Arrays.asList(value.split(","))) + "' ) ";
                    //whereTerm 为 between 的情况
                } else if (whereTerm.contains("BETWEEN")) {
                    String[] betweenValueS = value.split(",");
                    whereValue = String.format(KingBaseConstants.WHERE_VALUE_BETWEEN, betweenValueS[0], betweenValueS[1]);
                } else {
                    //删除 一些where子句条件
//					if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "null")) {
//						whereValue = "";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_null")) {
//						whereValue = "";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "empty")) {
//						whereValue = "''";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_empty")) {
//						whereValue = "''";
//					} else
                    if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "in")) {
                        whereValue = "('" + StringUtils.join(value, "','") + "')";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "LIKE")) {
                        whereValue = "'%" + value + "%'";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "lt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "le")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "gt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "ge")) {
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_BLANK, value);
                        ;
                    } else {
                        //转换 `whereValue`
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_VALUE, value);
                    }
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
        return CollectionUtils.isNotEmpty(res) ? "(" + String.join(" AND ", res) + ")" : null;
    }

    /**
     * 排除前端参数内的度量筛选字段条件，而不排除原有指标字段筛选条件
     * 2022年3月24日: 修改where子句度量条件bug，新增度量聚合条件,去除 参数度量条件，参数度量条件不使用sql进行筛选
     */
    public String transCustomFilterListNoMeasureFieldS(SQLObj tableObj, List<ChartFieldCustomFilterDTO> requestList, String measureField) {
        if (CollectionUtils.isEmpty(requestList)) {
            return null;
        }
        List<String> res = new ArrayList<>();
        //遍历自定义过滤器
        for (ChartFieldCustomFilterDTO request : requestList) {
            List<SQLObj> list = new ArrayList<>();
            //数据集数据字段
            DatasetTableField field = request.getField();
            if (ObjectUtils.isEmpty(field) || measureField.contains(field.getId())) {
                continue;
            }
            String whereName = "";
            String originName;
            //ExtField 没有额外字段 type -> type
            if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 2) {
                // 解析origin name中有关联的字段生成sql表达式
                originName = calcFieldRegex(field.getOriginName(), tableObj);
            } else if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 1) {
                //带上表名  type -> phone.`type`
                originName = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            } else {
                //带上表名  type -> phone.`type`
                originName = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            }
            //判断de类型为 1
            if (field.getDeType() == 1) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    //时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(KingBaseConstants.STR_TO_DATE, originName, KingBaseConstants.DEFAULT_DATE_FORMAT);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
                    //转换 CAST(field AS DECIMAL(20,2))/1000 设置列的范围并且/1000
                    String cast = String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_INT_FORMAT) + "/1000";
                    //转换时间戳为具体格式时间 FROM_UNIXTIME(CAST(field AS DECIMAL(20,2))/1000 ,'%Y-%m-%d %H:%i:%S')
                    whereName = String.format(KingBaseConstants.FROM_UNIXTIME, cast, KingBaseConstants.DEFAULT_DATE_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == 1) {
                    //默认 `tableAlias`.`fieldAlias`
                    whereName = originName;
                }
                //判断de类型为 2,3
            } else if (field.getDeType() == 2 || field.getDeType() == 3) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    //转换 时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_FLOAT_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == 1) {
                    //转化 UNIX_TIMESTAMP(originName) 直接得到参数对应的时间戳
                    whereName = String.format(KingBaseConstants.UNIX_TIMESTAMP, originName);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
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
                if (StringUtils.equalsIgnoreCase(value, "null")) {
                    value = "";
                }
                String whereTerm = transMysqlFilterTerm(filterItemDTO.getTerm());
                String whereValue = "";
                //whereTerm 为 In 或者 Not In的情况
                if (whereTerm.contains("IN") || whereTerm.contains("in")) {
                    whereValue = " ( '" + String.join("','", Arrays.asList(value.split(","))) + "' ) ";
                    //whereTerm 为 between 的情况
                } else if (whereTerm.contains("BETWEEN")) {
                    String[] betweenValueS = value.split(",");
                    whereValue = String.format(KingBaseConstants.WHERE_VALUE_BETWEEN, betweenValueS[0], betweenValueS[1]);
                } else {
                    //删除 一些where子句条件
//					if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "null")) {
//						whereValue = "";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_null")) {
//						whereValue = "";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "empty")) {
//						whereValue = "''";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_empty")) {
//						whereValue = "''";
//					} else
                    if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "in")) {
                        whereValue = "('" + StringUtils.join(value, "','") + "')";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "LIKE")) {
                        whereValue = "'%" + value + "%'";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "lt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "le")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "gt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "ge")) {
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_VALUE, value);
                        ;
                    } else {
                        //转换 `whereValue`
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_VALUE, value);
                    }
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
        return CollectionUtils.isNotEmpty(res) ? "(" + String.join(" AND ", res) + ")" : null;
    }

    /**
     * 默认条件 无维度、无度量、无图例 有条件 情况时候
     */
    public String transCustomFilterListDefault(SQLObj tableObj, List<ChartFieldCustomFilterDTO> requestList) {
        if (CollectionUtils.isEmpty(requestList)) {
            return null;
        }
        List<String> res = new ArrayList<>();
        //遍历自定义过滤器
        for (ChartFieldCustomFilterDTO request : requestList) {
            List<SQLObj> list = new ArrayList<>();
            //数据集数据字段
            DatasetTableField field = request.getField();
            if (ObjectUtils.isEmpty(field)) {
                continue;
            }
            String whereName = "";
            String originName;
            //ExtField 没有额外字段 type -> type
            if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 2) {
                // 解析origin name中有关联的字段生成sql表达式
                originName = calcFieldRegex(field.getOriginName(), tableObj);
            } else if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 1) {
                //带上表名  type -> phone.`type`
                originName = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            } else {
                //带上表名  type -> phone.`type`
                originName = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            }
            //判断de类型为 1
            if (field.getDeType() == 1) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    //时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(KingBaseConstants.STR_TO_DATE, originName, KingBaseConstants.DEFAULT_DATE_FORMAT);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
                    //转换 CAST(field AS DECIMAL(20,2))/1000 设置列的范围并且/1000
                    String cast = String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_INT_FORMAT) + "/1000";
                    //转换时间戳为具体格式时间 FROM_UNIXTIME(CAST(field AS DECIMAL(20,2))/1000 ,'%Y-%m-%d %H:%i:%S')
                    whereName = String.format(KingBaseConstants.FROM_UNIXTIME, cast, KingBaseConstants.DEFAULT_DATE_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == 1) {
                    //默认 `tableAlias`.`fieldAlias`
                    whereName = originName;
                }
                //判断de类型为 2,3
            } else if (field.getDeType() == 2 || field.getDeType() == 3) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    //转换 时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_FLOAT_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == 1) {
                    //转化 UNIX_TIMESTAMP(originName) 直接得到参数对应的时间戳
                    whereName = String.format(KingBaseConstants.UNIX_TIMESTAMP, originName);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
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
                if (StringUtils.equalsIgnoreCase(value, "null")) {
                    value = "";
                }
                String whereTerm = transMysqlFilterTerm(filterItemDTO.getTerm());
                String whereValue = "";
                //whereTerm 为 In 或者 Not In的情况
                if (whereTerm.contains("IN") || whereTerm.contains("in")) {
                    whereValue = " ( '" + String.join("','", Arrays.asList(value.split(","))) + "' ) ";
                    //whereTerm 为 between 的情况
                } else if (whereTerm.contains("BETWEEN")) {
                    String[] betweenValueS = value.split(",");
                    whereValue = String.format(KingBaseConstants.WHERE_VALUE_BETWEEN, betweenValueS[0], betweenValueS[1]);
                } else {
                    //删除 一些where子句条件
//					if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "null")) {
//						whereValue = "";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_null")) {
//						whereValue = "";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "empty")) {
//						whereValue = "''";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_empty")) {
//						whereValue = "''";
//					} else
                    if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "in")) {
                        whereValue = "('" + StringUtils.join(value, "','") + "')";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "LIKE")) {
                        whereValue = "'%" + value + "%'";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "lt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "le")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "gt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "ge")) {
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_BLANK, value);
                        ;
                    } else {
                        //转换 `whereValue`
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_VALUE, value);
                    }
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
        return CollectionUtils.isNotEmpty(res) ? "(" + String.join(" AND ", res) + ")" : null;
    }

    /**
     * 度量经过聚合运算之后进行having筛选
     */
    public String havingFilterList(List<DatasetTableField> fields, List<ChartFieldCustomFilterDTO> requestList, String measureField) {
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
            for (DatasetTableField f : fields) {
                if (StringUtils.equals(f.getId(), field.getId())) {
                    whereName = f.getOriginName();
                    break;
                }
            }
            if (ObjectUtils.isEmpty(whereName)) {
                continue;
            }
//			String whereName = "";
//			String originName;
//			//ExtField 没有额外字段 type -> type
//			if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 2) {
//				// 解析origin name中有关联的字段生成sql表达式
//				originName = calcFieldRegex(field.getOriginName(), tableObj);
//			} else if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 1) {
//				//带上表名  type -> phone.`type`
//				originName = String.format(MySQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
//			} else {
//				//带上表名  type -> phone.`type`
//				originName = String.format(MySQLConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
//			}
            //判断de类型为 1
//			if (field.getDeType() == 1) {
//				//判断de抽取类型为 0,5
//				if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
//					//时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
//					whereName = String.format(MySQLConstants.STR_TO_DATE, originName, MySQLConstants.DEFAULT_DATE_FORMAT);
//				}
//				//判断de抽取类型为 2,3,4
//				if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
//					//转换 CAST(field AS DECIMAL(20,2))/1000 设置列的范围并且/1000
//					String cast = String.format(MySQLConstants.CAST, originName, MySQLConstants.DEFAULT_INT_FORMAT) + "/1000";
//					//转换时间戳为具体格式时间 FROM_UNIXTIME(CAST(field AS DECIMAL(20,2))/1000 ,'%Y-%m-%d %H:%i:%S')
//					whereName = String.format(MySQLConstants.FROM_UNIXTIME, cast, MySQLConstants.DEFAULT_DATE_FORMAT);
//				}
//				//判断de抽取类型为 1
//				if (field.getDeExtractType() == 1) {
//					//默认 `tableAlias`.`fieldAlias`
//					whereName = originName;
//				}
//				//判断de类型为 2,3
//			} else if (field.getDeType() == 2 || field.getDeType() == 3) {
//				//判断de抽取类型为 0,5
//				if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
//					//转换 时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
//					whereName = String.format(MySQLConstants.CAST, originName, MySQLConstants.DEFAULT_FLOAT_FORMAT);
//				}
//				//判断de抽取类型为 1
//				if (field.getDeExtractType() == 1) {
//					//转化 UNIX_TIMESTAMP(originName) 直接得到参数对应的时间戳
//					whereName = String.format(MySQLConstants.UNIX_TIMESTAMP, originName);
//				}
//				//判断de抽取类型为 2,3,4
//				if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
//					//默认 `tableAlias`.`fieldAlias`
//					whereName = originName;
//				}
//				//判断de类型为 1,2,3之外
//			} else {
//				//默认 `tableAlias`.`fieldAlias
//				whereName = originName;
//			}
            //获取到自定义过滤条件列表
            List<ChartCustomFilterItemDTO> filter = request.getFilter();
            //遍历自定义过滤条件列表
            for (ChartCustomFilterItemDTO filterItemDTO : filter) {
                String value = StringUtils.isEmpty(filterItemDTO.getValue()) ? " " : filterItemDTO.getValue();
                //判断 value 为null的时候 , 为 "=","!=" 的情况
                if (StringUtils.equalsIgnoreCase(value, "null")) {
                    value = "";
                }
                String whereTerm = transMysqlFilterTerm(filterItemDTO.getTerm());
                String whereValue = "";
                //whereTerm 为 In 或者 Not In的情况
                if (whereTerm.contains("IN") || whereTerm.contains("in")) {
                    whereValue = " ( '" + String.join("','", Arrays.asList(value.split(","))) + "' ) ";
                    //whereTerm 为 between 的情况
                } else if (whereTerm.contains("BETWEEN")) {
                    String[] betweenValueS = value.split(",");
                    whereValue = String.format(KingBaseConstants.WHERE_VALUE_BETWEEN, betweenValueS[0], betweenValueS[1]);
                } else {
                    //删除 一些where子句条件
//					if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "null")) {
//						whereValue = "";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_null")) {
//						whereValue = "";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "empty")) {
//						whereValue = "''";
//					} else if (StringUtils.equalsIgnoreCase(filterItemDTO.getTerm(), "not_empty")) {
//						whereValue = "''";
//					} else
                    if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "in")) {
                        whereValue = "('" + StringUtils.join(value, "','") + "')";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "LIKE")) {
                        whereValue = "'%" + value + "%'";
                    } else if (StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "lt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "le")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "gt")
                            || StringUtils.containsIgnoreCase(filterItemDTO.getTerm(), "ge")) {
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_BLANK, value);
                        ;
                    } else {
                        //转换 `whereValue`
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_VALUE, value);
                    }
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
        return CollectionUtils.isNotEmpty(res) ? "(" + String.join(" AND ", res) + ")" : null;
    }

    /**
     * 度量聚合函数转换
     */
//    public String transPol(DataSetTableRequest superRequest, DatasetTableField field, String originField) {
//        String fieldName = "";
//        if (superRequest.getMeasureField().contains(field.getId())) {
//            if (ObjectUtils.isNotEmpty(superRequest.getFieldCondition())
//                    && CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getPolConditionS())) {
//                //度量数据格式
//                MeasureFormatCondition measureFormatCondition = null;
//                if (CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getMeasureFormatConditionS())) {
//                    measureFormatCondition = superRequest.getFieldCondition().getMeasureFormatConditionS().stream().filter(m -> StringUtils.equals(m.getFieldId(), field.getId())).collect(Collectors.toList()).get(0);
//                }
//                List<PolCondition> polConditionS = superRequest.getFieldCondition().getPolConditionS();
//                for (PolCondition pol : polConditionS) {
//                    if (StringUtils.equals(pol.getFieldId(), field.getId())) {
//                        //默认为常规，无小数位
//                        String format = MySQLConstants.DEFAULT_INT_FORMAT;
//                        if (ObjectUtils.isNotEmpty(measureFormatCondition)) {
//                            switch (measureFormatCondition.getFormat()) {
//                                case 0:
//                                    format = MySQLConstants.DEFAULT_INT_FORMAT;
//                                    break;
//                                case 1:
//                                    format = MySQLConstants.DEFAULT_ONE_FORMAT;
//                                    break;
//                                case 2:
//                                    format = MySQLConstants.DEFAULT_FLOAT_FORMAT;
//                                    break;
//                                case 3:
//                                    format = MySQLConstants.DEFAULT_THREE_FORMAT;
//                                    break;
//                                case 4:
//                                    format = MySQLConstants.DEFAULT_FOUR_FORMAT;
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                        switch (pol.getPolType()) {
//                            case PolCondition.AVG:
//                                fieldName = String.format(MySQLConstants.AVG, String.format(MySQLConstants.CAST, originField, format));
//                                break;
//                            case PolCondition.SUM:
//                                fieldName = String.format(MySQLConstants.SUM, String.format(MySQLConstants.CAST, originField, format));
//                                break;
//                            case PolCondition.MAX:
//                                fieldName = String.format(MySQLConstants.MAX, String.format(MySQLConstants.CAST, originField, format));
//                                break;
//                            case PolCondition.MIN:
//                                fieldName = String.format(MySQLConstants.MIN, String.format(MySQLConstants.CAST, originField, format));
//                                break;
//                            default:
//                                fieldName = String.format(MySQLConstants.SUM, String.format(MySQLConstants.CAST, originField, format));
//                                break;
//                        }
//                        break;
//                    }
//                }
//            } else {
//                //度量数据格式
//                MeasureFormatCondition measureFormatCondition = null;
//                if (ObjectUtils.isNotEmpty(superRequest.getFieldCondition()) && CollectionUtils.isNotEmpty(superRequest.getFieldCondition().getMeasureFormatConditionS())) {
//                    measureFormatCondition = superRequest.getFieldCondition().getMeasureFormatConditionS().stream().filter(m -> StringUtils.equals(m.getFieldId(), field.getId())).collect(Collectors.toList()).get(0);
//                }
//                //默认为常规，无小数位
//                String format = MySQLConstants.DEFAULT_INT_FORMAT;
//                if (ObjectUtils.isNotEmpty(measureFormatCondition)) {
//                    switch (measureFormatCondition.getFormat()) {
//                        case 0:
//                            format = MySQLConstants.DEFAULT_INT_FORMAT;
//                            break;
//                        case 1:
//                            format = MySQLConstants.DEFAULT_ONE_FORMAT;
//                            break;
//                        case 2:
//                            format = MySQLConstants.DEFAULT_FLOAT_FORMAT;
//                            break;
//                        case 3:
//                            format = MySQLConstants.DEFAULT_THREE_FORMAT;
//                            break;
//                        case 4:
//                            format = MySQLConstants.DEFAULT_FOUR_FORMAT;
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                //默认sum
//                fieldName = String.format(MySQLConstants.SUM, String.format(MySQLConstants.CAST, originField, format));
//            }
//        } else {
//            fieldName = String.format(MySQLConstants.CAST, originField, MySQLConstants.DEFAULT_INT_FORMAT);
//        }
//        return fieldName;
//    }
//
    public String transPol(DataSetTableRequest superRequest, DatasetTableField field, String originField) {
        String fieldName = "";
        if (superRequest.getMeasureField().contains(field.getId())) {
            //度量数据格式  DeTypeConstants
            //默认为常规，无小数位
            String format = null;
            switch (field.getDeType()) {
                case 2:
                    format = KingBaseConstants.DEFAULT_INT_FORMAT;
                    break;
                case 3:
                    format = KingBaseConstants.DEFAULT_FLOAT_FORMAT;
                    break;
                default:
                    format = KingBaseConstants.DEFAULT_INT_FORMAT;
                    break;
            }
            //默认sum
            fieldName = String.format(KingBaseConstants.SUM, String.format(KingBaseConstants.CAST, originField, format));
        } else {
            fieldName = String.format(KingBaseConstants.CAST, originField, KingBaseConstants.DEFAULT_INT_FORMAT);
        }
        return fieldName;
    }


    public String transCustomFilterList(SQLObj tableObj, List<ChartFieldCustomFilterDTO> requestList) {
        if (CollectionUtils.isEmpty(requestList)) {
            return null;
        }
        List<String> res = new ArrayList<>();
        //遍历自定义过滤器
        for (ChartFieldCustomFilterDTO request : requestList) {
            List<SQLObj> list = new ArrayList<>();
            //数据集数据字段
            DatasetTableField field = request.getField();

            if (ObjectUtils.isEmpty(field)) {
                continue;
            }
            String whereName = "";
            String originName;
            //ExtField 没有额外字段 type -> type
            if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 2) {
                // 解析origin name中有关联的字段生成sql表达式
                originName = calcFieldRegex(field.getOriginName(), tableObj);
            } else if (ObjectUtils.isNotEmpty(field.getExtField()) && field.getExtField() == 1) {
                //带上表名  type -> phone.`type`
                originName = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            } else {
                //带上表名  type -> phone.`type`
                originName = String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), field.getOriginName());
            }
            //判断de类型为 1
            if (field.getDeType() == 1) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    //时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(KingBaseConstants.STR_TO_DATE, originName, KingBaseConstants.DEFAULT_DATE_FORMAT);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
                    //转换 CAST(field AS DECIMAL(20,2))/1000 设置列的范围并且/1000
                    String cast = String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_INT_FORMAT) + "/1000";
                    //转换时间戳为具体格式时间 FROM_UNIXTIME(CAST(field AS DECIMAL(20,2))/1000 ,'%Y-%m-%d %H:%i:%S')
                    whereName = String.format(KingBaseConstants.FROM_UNIXTIME, cast, KingBaseConstants.DEFAULT_DATE_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == 1) {
                    //默认 `tableAlias`.`fieldAlias`
                    whereName = originName;
                }
                //判断de类型为 2,3
            } else if (field.getDeType() == 2 || field.getDeType() == 3) {
                //判断de抽取类型为 0,5
                if (field.getDeExtractType() == 0 || field.getDeExtractType() == 5) {
                    //转换 时间格式的字符串（str），按照所提供的显示格式（format）转换为DATETIME
                    whereName = String.format(KingBaseConstants.CAST, originName, KingBaseConstants.DEFAULT_FLOAT_FORMAT);
                }
                //判断de抽取类型为 1
                if (field.getDeExtractType() == 1) {
                    //转化 UNIX_TIMESTAMP(originName) 直接得到参数对应的时间戳
                    whereName = String.format(KingBaseConstants.UNIX_TIMESTAMP, originName);
                }
                //判断de抽取类型为 2,3,4
                if (field.getDeExtractType() == 2 || field.getDeExtractType() == 3 || field.getDeExtractType() == 4) {
                    //默认 `tableAlias`.`fieldAlias`
                    whereName = originName;
                }
                //判断de类型为 1,2,3之外
            } else {
                //默认 `tableAlias`.`fieldAlias
                whereName = originName;
            }
            //判断过滤类型 即 filterType = enum 代表 field in (xx,xx,xx) 语句
            //并且将 enumCheckField 放进条件 即enumCheckField代表 in 条件语句参数
            if (StringUtils.equalsIgnoreCase(request.getFilterType(), "enum")) {
                if (CollectionUtils.isNotEmpty(request.getEnumCheckField())) {
                    res.add("(" + whereName + " IN ('" + String.join("','", request.getEnumCheckField()) + "'))");
                }
//			判断过滤类型 filterType != enum 即代表 其他过滤语句
            } else {
                //获取到自定义过滤条件列表
                List<ChartCustomFilterItemDTO> filter = request.getFilter();
                //遍历自定义过滤条件列表
                for (ChartCustomFilterItemDTO filterItemDTO : filter) {
                    String value = filterItemDTO.getValue();
                    String whereTerm = transMysqlFilterTerm(filterItemDTO.getTerm());
                    String whereValue = "";
                    //whereTerm 为 In 或者 Not In的情况
                    if (whereTerm.contains("IN") || whereTerm.contains("in")) {
                        whereValue = " ( " + String.join(",", Arrays.asList(value.split(","))) + " ) ";
                        //whereTerm 为 between 的情况
                    } else if (whereTerm.contains("BETWEEN")) {
                        String[] betweenValueS = value.split(",");
                        whereValue = String.format(KingBaseConstants.WHERE_VALUE_BETWEEN, betweenValueS[0], betweenValueS[1]);
                    } else {
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
                            //转换 `whereValue`
                            whereValue = String.format(KingBaseConstants.WHERE_VALUE_VALUE, value);
                        }
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

    private String sqlFix(String sql) {
        if (sql.lastIndexOf(";") == (sql.length() - 1)) {
            sql = sql.substring(0, sql.length() - 1);
        }
        return sql;
    }

    private String transDateFormat(String dateStyle, String datePattern) {
        String split = "-";
        if (StringUtils.equalsIgnoreCase(datePattern, "date_sub")) {
            split = "-";
        } else if (StringUtils.equalsIgnoreCase(datePattern, "date_split")) {
            split = "/";
        } else {
            split = "-";
        }

        if (StringUtils.isEmpty(dateStyle)) {
            return "%Y-%m-%d %H:%i:%S";
        }

        switch (dateStyle) {
            case "y":
                return "%Y";
            case "y_M":
                return "%Y" + split + "%m";
            case "y_M_d":
                return "%Y" + split + "%m" + split + "%d";
            case "H_m_s":
                return "%H:%i:%S";
            case "y_M_d_H_m":
                return "%Y" + split + "%m" + split + "%d" + " %H:%i";
            case "y_M_d_H_m_s":
                return "%Y" + split + "%m" + split + "%d" + " %H:%i:%S";
            default:
                return "%Y-%m-%d %H:%i:%S";
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
                    String.format(KingBaseConstants.KEYWORD_FIX, tableObj.getTableAlias(), ele.getOriginName()));
        }
        return originField;
    }
}
