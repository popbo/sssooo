package com.stdc.visual.dynamic.base.dataset.dto.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:31
 * @describe: 字段条件 对度量、维度、图例 等字段进行自定义筛选（如：排序、聚合函数、显示别名 等条件）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldCondition implements Serializable {

    /**
     * 排序条件
     */
    @ApiModelProperty(value = "排序条件")
    private List<SortCondition> sortConditionS;

    /**
     * 时间格式条件
     */
    @ApiModelProperty(value = "时间格式条件")
    private List<DateFormatCondition> dateFormatConditionS;

    /**
     * 度量聚合条件
     */
    @ApiModelProperty(value = "度量聚合条件")
    private List<PolCondition> polConditionS;


    /**
     * 度量数据格式
     */
    @ApiModelProperty(value = "度量数据格式")
    private List<MeasureFormatCondition> measureFormatConditionS;


}
