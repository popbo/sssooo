package com.stdc.visual.dynamic.base.chart.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:27
 * @describe: 自定义条件信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartCustomFilterItemDTO implements Serializable {
    //字段id,dataeaseName
    private String fieldId;
    //字段连接符
    private String term;
    //字段值
    private String value;
}