package com.stdc.visual.dynamic.provider.query.pg;

import com.stdc.visual.dynamic.provider.query.SQLConstants;

import static com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes.pg;

/**
 * @author: wang_jie
 * @data: 2022/6/28--10:37
 * @describe:
 */
public class PgConstants extends SQLConstants {
    public static final String KEYWORD_TABLE = pg.getKeywordPrefix() + "%s" + pg.getKeywordSuffix();

    public static final String KEYWORD_FIX = "%s." + pg.getKeywordPrefix() + "%s" + pg.getKeywordSuffix();

    public static final String UNIX_TIMESTAMP = "floor(extract(epoch from(( %s - timestamp '1970-01-01 00:00:00')*1000))) ";

    public static final String DATE_FORMAT = "to_char(%s, '%s')";

    public static final String FROM_UNIXTIME = "to_timestamp(%s)";

    public static final String CAST = "CAST(%s AS %s)";

    public static final String TO_DATE = "TO_DATE(%s,'%s')";

    public static final String DEFAULT_DATE_FORMAT = "YYYY-MM-DD HH24:MI:SS";

    public static final String SUM = "SUM( %s )";

    public static final String DEFAULT_INT_FORMAT = "DECIMAL(18,0)";

    public static final String DEFAULT_ONE_FORMAT = "DECIMAL(18,1)";

    public static final String DEFAULT_FLOAT_FORMAT = "DECIMAL(18,2)";

    public static final String DEFAULT_THREE_FORMAT = "DECIMAL(18,3)";

    public static final String DEFAULT_FOUR_FORMAT = "DECIMAL(18,4)";

    public static final String WHERE_VALUE_BETWEEN = " '%s' AND '%s' ";

    public static final String WHERE_VALUE_BLANK = " %s ";

    public static final String WHERE_VALUE_NULL = "(NULL,'')";

    public static final String WHERE_VALUE_VALUE = "'%s'";

    public static final String AGG_COUNT = "COUNT(*)";

    public static final String AGG_FIELD = "%s(%s)";

    public static final String WHERE_BETWEEN = "'%s' AND '%s'";

    public static final String BRACKETS = "(%s)";

}