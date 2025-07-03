package com.stdc.visual.dynamic.base.dataset.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: wang_jie
 * @data: 2022/5/17--18:04
 * @describe:
 */
@Data
public class DataSetPreviewPage {
    private Integer total = 0;
    private Integer show = 0;
    private Integer page = 1;
    private Integer pageSize = 100;
}

