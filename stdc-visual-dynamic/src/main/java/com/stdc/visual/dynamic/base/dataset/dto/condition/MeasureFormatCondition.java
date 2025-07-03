package com.stdc.visual.dynamic.base.dataset.dto.condition;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:34
 * @describe: 度量数据格式
 */
@Data
public class MeasureFormatCondition implements Serializable {
    /**
     * 字段id
     */
    private String fieldId;

    /**
     * 度量格式粒度 0(无小数位)、1(1位小数位)、2(两位小数位)、3(三位小数位)、4(四位小数位)、
     */
    private Integer format;

    /***
     * 数量级 常规 万 百万 千万 亿
     */
    private String om;

    /**
     * 常规
     */
    public static final String usual = "usual";

    /**
     * 万
     */
    public static final String tenThousand = "tenThousand";

    /**
     * 百万
     */
    public static final String million = "million";

    /**
     * 千万
     */
    public static final String tenMillion = "tenMillion";

    /**
     * 亿
     */
    public static final String hundredMillion = "hundredMillion";

}
