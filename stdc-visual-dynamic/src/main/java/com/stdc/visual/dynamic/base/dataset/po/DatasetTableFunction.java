package com.stdc.visual.dynamic.base.dataset.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/20--9:38
 * @describe:
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "dataset_table_function")
public class DatasetTableFunction implements Serializable {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("函数")
    private String func;
    @ApiModelProperty("库类型")
    private String dbType;
    @ApiModelProperty("函数类型")
    private Integer funcType;
    @ApiModelProperty("描述")
    private String description;

    private static final long serialVersionUID = 1L;
}