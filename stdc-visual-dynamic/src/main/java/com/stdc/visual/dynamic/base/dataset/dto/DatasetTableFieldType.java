package com.stdc.visual.dynamic.base.dataset.dto;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/19--15:15
 * @describe: 数据集字段类型
 */
@Data
public class DatasetTableFieldType {
    @ApiModelProperty("维度")
    List<DatasetTableField> dimensionList;
    @ApiModelProperty("指标")
    List<DatasetTableField> quotaList;
}
