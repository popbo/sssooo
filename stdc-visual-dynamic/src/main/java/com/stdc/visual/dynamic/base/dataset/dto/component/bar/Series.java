package com.stdc.visual.dynamic.base.dataset.dto.component.bar;

import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/25--15:36
 * @describe:
 */
@Data
public class Series {
    /**
     * 名称
     */
    private String name;

    /***
     * 数据
     */
    private List<Double> data;
}
