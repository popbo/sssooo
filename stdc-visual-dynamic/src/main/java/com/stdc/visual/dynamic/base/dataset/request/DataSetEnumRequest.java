package com.stdc.visual.dynamic.base.dataset.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/8/30--15:07
 * @describe: 获取数据集字段枚举值
 */
@Data
@ApiModel(description = "获取数据集字段枚举值")
public class DataSetEnumRequest {

    @ApiModelProperty(value = "数据源id", required = true)
    String dataSourceId;

    @ApiModelProperty(value = "数据集id", required = true)
    String dataSetTableId;

    @ApiModelProperty(value = "数据集字段id", required = true)
    String dataSetTableFieldId;
}
