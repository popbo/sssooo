package com.stdc.visual.dynamic.base.dataset.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:27
 * @describe:
 */
@Data
public class DataTableInfoCustomUnion {
    private String tableId;
    private List<String> checkedFields;
}
