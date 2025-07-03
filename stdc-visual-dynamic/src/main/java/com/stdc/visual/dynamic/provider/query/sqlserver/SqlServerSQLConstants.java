package com.stdc.visual.dynamic.provider.query.sqlserver;

import com.stdc.visual.dynamic.provider.query.SQLConstants;

import static com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes.sqlServer;

/**
 * @author: wang_jie
 * @data: 2022/6/28--14:12
 * @describe:
 */
public class SqlServerSQLConstants extends SQLConstants {
    public static final String KEYWORD_TABLE = sqlServer.getKeywordPrefix() + "%s" + sqlServer.getKeywordSuffix();

    public static final String KEYWORD_FIX = "%s." + sqlServer.getKeywordPrefix() + "%s" + sqlServer.getKeywordSuffix();

    public static final String UNIX_TIMESTAMP = "CAST(DATEDIFF(ss,'1970-01-01 08:00:00', %s) as bigint ) * 1000 ";

    public static final String DATE_FORMAT = "CONVERT(varchar(100), %s, %s)";

    public static final String FROM_UNIXTIME = "convert(varchar, %s ,120)";

    public static final String CONVERT = "CONVERT(%s, %s)";

    public static final String LONG_TO_DATE = "DATEADD(second,%s,'1970-01-01 08:00:00')";

    public static final String STRING_TO_DATE = "CONVERT(datetime, %s ,120)";

    public static final String DEFAULT_INT_FORMAT = "DECIMAL(20,0)";

    public static final String DEFAULT_ONE_FORMAT = "DECIMAL(20,1)";

    public static final String DEFAULT_FLOAT_FORMAT = "DECIMAL(20,2)";

    public static final String DEFAULT_THREE_FORMAT = "DECIMAL(20,3)";

    public static final String DEFAULT_FOUR_FORMAT = "DECIMAL(20,4)";

    public static final String CAST = "CAST(%s AS %s)";

    public static final String SUM = "SUM( %s )";

    public static final String WHERE_VALUE_BETWEEN = " '%s' AND '%s' ";

    public static final String WHERE_VALUE_BLANK = " %s ";

    public static final String WHERE_VALUE_NULL = "(NULL,'')";

    public static final String WHERE_VALUE_VALUE = "'%s'";

    public static final String AGG_COUNT = "COUNT(*)";

    public static final String AGG_FIELD = "%s(%s)";

    public static final String WHERE_BETWEEN = "'%s' AND '%s'";

    public static final String BRACKETS = "(%s)";
}