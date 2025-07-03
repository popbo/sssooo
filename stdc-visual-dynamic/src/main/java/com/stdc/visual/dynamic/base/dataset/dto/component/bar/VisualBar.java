package com.stdc.visual.dynamic.base.dataset.dto.component.bar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author: wang_jie
 * @data: 2022/5/25--15:35
 * @describe: 柱状图数据格式、折线图数据格式、象形图数据格式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisualBar {

    /***
     * 类别
     */
    private Set<String> categories;

    /***
     * 系列
     */
    private List<Series> series;

    /**
     * 获取一个实例
     * @return
     */
    public static VisualBar instance(){
        VisualBar visualBar = new VisualBar();
        visualBar.setCategories(new LinkedHashSet<>());
        visualBar.setSeries(new LinkedList<Series>());
        return visualBar;
    }


}
