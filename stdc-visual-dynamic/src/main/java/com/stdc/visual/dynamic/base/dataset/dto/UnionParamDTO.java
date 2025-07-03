package com.stdc.visual.dynamic.base.dataset.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:28
 * @describe: 关联数据集字段
 */
@Data
public class UnionParamDTO {
    private String unionType;
    private List<UnionItemDTO> unionFields;
}
