package com.stdc.visual.dynamic.base.dataset.dto.component.bar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @author: wang_jie
 * @data: 2022/5/26--19:03
 * @describe:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisualLineBar {
    /***
     * 类别
     */
    private Set<String> categories;

    /***
     * 系列
     */
    private List<ExtSeries> series;
}


