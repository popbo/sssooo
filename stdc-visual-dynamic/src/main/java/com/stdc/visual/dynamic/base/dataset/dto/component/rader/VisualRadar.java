package com.stdc.visual.dynamic.base.dataset.dto.component.rader;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/26--11:39
 * @describe: 雷达图数据格式
 */
@Data
@Accessors(chain = true)
public class VisualRadar {
    /**
     * 指示
     */
    private List<Indicator> indicator = new ArrayList<>();
    /***
     * 系列
     */
    private List<Serie> series = new ArrayList<>();

    /***
     * 指示信号
     */
    @Data
    public static class Indicator{
        private String name;
        private Long max;
    }

    @Data
    public static class Serie{
        private List<DataS> data = new ArrayList<>();
    }

    /***
     * 数据
     */
    @Data
    public static class DataS{
        private List<Long> value;
        private String name;
    }
}
