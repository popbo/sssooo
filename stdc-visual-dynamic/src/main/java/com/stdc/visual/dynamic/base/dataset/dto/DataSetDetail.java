package com.stdc.visual.dynamic.base.dataset.dto;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/5/19--14:38
 * @describe: 数据集详情
 */
@Data
public class DataSetDetail {
    @ApiModelProperty("数据集")
    private DatasetTable table;
    @ApiModelProperty("数据源")
    private Datasource datasource;
}