package com.stdc.visual.dynamic.base.dataset.dto.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:33
 * @describe: 时间格式条件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateFormatCondition implements Serializable {

    /**
     * 字段id
     */
    @ApiModelProperty(value = "字段id")
    private String fieldId;

    /**
     * 时间格式粒度
     */
    @ApiModelProperty(value = "时间格式粒度，year(年)，quarter(季度)，month(月)，week(周)，day(天)，hour(小时)，accurate(默认精确值)")
    private String granularity;

    /***
     * 格式
     */
    @ApiModelProperty(value = "格式 %Y(年)、%m(月)、%d（日）、%H(小时) 、%s(季度，周)")
    private String format;

    /**
     * 时间粒度标签-年
     */
    public static final String YEAR = "year";

    /**
     * 时间粒度标签-季度
     */
    public static final String QUARTER = "quarter";

    /**
     * 时间粒度标签-月
     */
    public static final String MONTH = "month";

    /**
     * 时间粒度标签-周
     */
    public static final String WEEK = "week";

    /**
     * 时间粒度标签-日
     */
    public static final String DAY = "day";

    /**
     * 时间粒度标签-小时
     */
    public static final String HOUR = "hour";

    /**
     * 时间粒度标签-分
     */
    public static final String MINUTE = "minute";

    /**
     * 时间粒度标签-默认
     */
    public static final String ACCURATE = "accurate";
}
