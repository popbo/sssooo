package com.stdc.visual.dynamic.base.dataset.dto.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:32
 * @describe: 排序条件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortCondition implements Serializable {
    /**
     * 字段id 多维度排序情况使用  维度：dimensionField  度量：measureField  图例：legendField
     */
    @ApiModelProperty(value = "字段id 多维度排序情况使用  维度：dimensionField  度量：measureField  图例：legendField")
    private String fieldId;
    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    private Long priority;
    /***
     * 排序类型
     */
    @ApiModelProperty(value = "排序类型")
    private String sortType;
    /***
     * 图例 使用自定义排序时图例的顺序
     */
    @ApiModelProperty(value = "图例 使用自定义排序时图例的顺序")
    private String customMsg;
}
