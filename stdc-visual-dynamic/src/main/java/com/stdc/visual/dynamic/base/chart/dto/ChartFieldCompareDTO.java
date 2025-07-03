package com.stdc.visual.dynamic.base.chart.dto;

import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:26
 * @describe:
 */
@Data
public class ChartFieldCompareDTO {
    private String type;
    private String resultData;
    private String field;
    private ChartFieldCompareCustomDTO custom;
}
