package com.stdc.visual.dynamic.provider.query.es;

import com.stdc.visual.dynamic.provider.query.SQLConstants;

import static com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes.es;

/**
 * @author: wang_jie
 * @data: 2022/6/28--14:37
 * @describe:
 */
public class EsSqlLConstants extends SQLConstants {
    public static final String KEYWORD_TABLE = es.getKeywordPrefix() + "%s" + es.getKeywordSuffix();

    public static final String KEYWORD_FIX = "%s." + es.getKeywordPrefix() + "%s" + es.getKeywordSuffix();

    public static final String UNIX_TIMESTAMP = "UNIX_TIMESTAMP(%s)";

    public static final String DATETIME_FORMAT = "DATETIME_FORMAT(%s,'%s')";

    public static final String CAST = "CAST(%s AS %s)";

    public static final String SUM = "SUM( %s )";

    public static final String ROUND = "ROUND(%s, %s)";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String WHERE_VALUE_NULL = "(NULL,'')";

    public static final String WHERE_VALUE_VALUE = "'%s'";

    public static final String AGG_COUNT = "COUNT(*)";

    public static final String AGG_FIELD = "%s(%s)";

    public static final String WHERE_BETWEEN = "'%s' AND '%s'";

    public static final String BRACKETS = "(%s)";
}
