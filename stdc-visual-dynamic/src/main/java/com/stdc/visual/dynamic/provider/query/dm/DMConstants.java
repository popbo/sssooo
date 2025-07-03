package com.stdc.visual.dynamic.provider.query.dm;


import com.stdc.visual.dynamic.provider.query.SQLConstants;

import static com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes.dm;

/**
 * @Author gin
 * @Date 2021/7/8 7:22 下午
 */
public class DMConstants extends SQLConstants {
    public static final String KEYWORD_TABLE = dm.getKeywordPrefix() + "%s" + dm.getKeywordSuffix();

    public static final String KEYWORD_FIX = "%s." + dm.getKeywordPrefix() + "%s" + dm.getKeywordSuffix();

    public static final String UNIX_TIMESTAMP = "UNIX_TIMESTAMP(%s)";

    public static final String DATE_FORMAT = "DATE_FORMAT(%s,'%s')";

    public static final String FROM_UNIXTIME = "FROM_UNIXTIME(%s,'%s')";

    public static final String STR_TO_DATE = "STR_TO_DATE(%s,'%s')";

    public static final String SUM = "SUM( %s )";

	public static final String AVG = "AVG( %s )";

	public static final String MAX = "MAX( %s )";

	public static final String MIN = "MIN( %s )";

    public static final String CAST = "CAST(%s AS %s)";

	public static final String REPLACE = "REPLACE('%s','%s',%s)";

    public static final String DEFAULT_DATE_FORMAT = "%Y-%m-%d %H:%i:%S";

	public static final String YEAR_DATE_FORMAT = "%Y";

	public static final String QUARTER_DATE_FORMAT = "QUARTER(%s)";

	public static final String MONTH_DATE_FORMAT = "%Y-%m";

	public static final String WEEK_DATE_FORMAT = "%u";

	public static final String DAY_DATE_FORMAT = "%Y-%m-%d";

	public static final String HOUR_DATE_FORMAT = "%Y-%m-%d %H";

	public static final String DEFAULT_INT_FORMAT = "DECIMAL(20,0)";

	public static final String DEFAULT_ONE_FORMAT = "DECIMAL(20,1)";

    public static final String DEFAULT_FLOAT_FORMAT = "DECIMAL(20,2)";

	public static final String DEFAULT_THREE_FORMAT = "DECIMAL(20,3)";

	public static final String DEFAULT_FOUR_FORMAT = "DECIMAL(20,4)";

    public static final String WHERE_VALUE_NULL = "(NULL,'')";

    public static final String WHERE_VALUE_VALUE = "'%s'";

	public static final String WHERE_VALUE_BLANK = " %s ";

	public static final String WHERE_VALUE_BETWEEN = " '%s' AND '%s' ";

    public static final String AGG_COUNT = "COUNT(*)";

    public static final String AGG_FIELD = "%s(%s)";

    public static final String WHERE_BETWEEN = "'%s' AND '%s'";

    public static final String BRACKETS = "(%s)";

    public static final String ADD = "+%s+";

}
