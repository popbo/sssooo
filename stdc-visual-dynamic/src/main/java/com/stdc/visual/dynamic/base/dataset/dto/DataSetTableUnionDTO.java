package com.stdc.visual.dynamic.base.dataset.dto;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTableUnion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: wang_jie
 * @data: 2022/5/17--15:22
 * @describe: 表关联关系
 */
@Data
public class DataSetTableUnionDTO extends DatasetTableUnion {
    @ApiModelProperty("源表名称")
    private String sourceTableName;
    @ApiModelProperty("源表字段名称")
    private String sourceTableFieldName;
    @ApiModelProperty("目标表名称")
    private String targetTableName;
    @ApiModelProperty("目标表字段名称")
    private String targetTableFieldName;
}
