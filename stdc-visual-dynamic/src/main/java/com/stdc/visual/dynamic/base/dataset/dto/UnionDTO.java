package com.stdc.visual.dynamic.base.dataset.dto;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:27
 * @describe: 关联数据集
 */
@Data
public class UnionDTO {
    private DatasetTable currentDs;
    private List<String> currentDsField;
    private List<UnionDTO> childrenDs;
    private UnionParamDTO unionToParent;
    private int allChildCount;
}
