package com.stdc.visual.dynamic.base.chart.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:26
 * @describe:
 */
@Data
public class ChartFieldCompareCustomDTO {
    private String field;
    private String calcType;
    private String timeType;
    private String currentTime;
    private String compareTime;
    private List<String> currentTimeRange;
    private List<String> compareTimeRange;

}
