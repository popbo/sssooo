package com.stdc.visual.dynamic.base.dataset.dto;

import com.stdc.visual.dynamic.base.chart.dto.ChartCustomFilterItemDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:36
 * @describe: 联动条件查询条件
 */
@Data
public class LinkCustomFilter implements Serializable {
    private List<ChartCustomFilterItemDTO> filter;
}

