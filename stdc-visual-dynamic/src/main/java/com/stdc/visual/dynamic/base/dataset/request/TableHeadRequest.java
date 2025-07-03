package com.stdc.visual.dynamic.base.dataset.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/6/7--9:35
 * @describe: 表格表头数据参数
 */
@Data
@ApiModel(description = "表格表头数据参数")
public class TableHeadRequest implements Serializable {

    @ApiModelProperty("父级表头")
    private String head;

    @ApiModelProperty("子表头")
    private List<TableHeadRequest> child;


}
