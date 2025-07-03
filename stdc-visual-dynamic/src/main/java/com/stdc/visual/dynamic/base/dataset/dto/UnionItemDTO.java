package com.stdc.visual.dynamic.base.dataset.dto;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:28
 * @describe: 关联数据集字段
 */
@Data
public class UnionItemDTO {
    private DatasetTableField parentField;
    private DatasetTableField currentField;
}