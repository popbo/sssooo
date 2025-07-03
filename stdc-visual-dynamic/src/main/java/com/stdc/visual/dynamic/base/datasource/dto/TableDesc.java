package com.stdc.visual.dynamic.base.datasource.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:14
 * @describe: 表详情
 */
@Data
public class TableDesc {
    @ApiModelProperty("表名称")
    private String name;
    @ApiModelProperty("表备注")
    private String remark;
}
