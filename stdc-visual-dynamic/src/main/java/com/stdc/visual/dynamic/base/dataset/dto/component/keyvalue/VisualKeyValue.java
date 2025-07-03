package com.stdc.visual.dynamic.base.dataset.dto.component.keyvalue;

import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/5/23--16:56
 * @describe: 数据为key-value形式的组件，包括：饼状图、象形图、散点图、漏斗图、轮播图
 */
@Data
public class VisualKeyValue {
    /**
     * 维度（名称）
     */
    private String name;
    /**
     * 度量（值）
     */
    private Double value = 0D;
    /***
     * 地址
     */
    private String url;

}
