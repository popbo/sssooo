package com.stdc.visual.dynamic.base.dataset.dto.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:33
 * @describe: 聚合条件
 */
@Data
public class PolCondition implements Serializable {
    /**
     * 字段id 多维度排序情况使用  维度：dimensionField  度量：measureField  图例：legendField
     */
    @ApiModelProperty(value = "字段id")
    private String fieldId;
    /***
     * 聚合类型 求和 最大值 最小值 平均值 中位数
     */
    @ApiModelProperty(value = "聚合类型 求和 ")
    private String polType;

    /**
     * 求和
     */
    public static final String SUM = "sum";

    /**
     * 求和
     */
    public static final String AVG = "avg";

    /**
     * 求和
     */
    public static final String MAX = "max";

    /**
     * 求和
     */
    public static final String MIN = "min";
}

