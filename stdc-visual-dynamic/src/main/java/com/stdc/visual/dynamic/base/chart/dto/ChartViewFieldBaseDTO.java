package com.stdc.visual.dynamic.base.chart.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:25
 * @describe: 自定义条件字段基础信息
 */
@Data
public class ChartViewFieldBaseDTO implements Serializable {
    private String id;

    private String tableId;

    private String originName;

    private String dataeaseName;

    private String name;

    private String type;

    private Boolean checked;

    private Integer columnIndex;

    private Long lastSyncTime;

    private Integer deType;

    private String summary;

    private String sort;

    private Integer deExtractType;

    private String dateStyle;

    private String datePattern;

    private Integer extField;

    private String chartType;

    private ChartFieldCompareDTO compareCalc;

    private String logic;

    private String filterType;
}
