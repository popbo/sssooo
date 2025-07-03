package com.stdc.visual.dynamic.base.datasource.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:15
 * @describe: 表字段
 */
@Data
public class TableFiled {
    @ApiModelProperty("字段名称")
    private String fieldName;
    @ApiModelProperty("重新标记")
    private String remarks;
    @ApiModelProperty("字段类型")
    private String fieldType;
    @ApiModelProperty("字段大小")
    private int fieldSize;
}