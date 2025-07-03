package com.stdc.visual.dynamic.base.dataset.dto.component.scatter;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/24--16:05
 * @describe: 散点图
 */
@Data
@Accessors(chain = true)
public class VisualScatter {

    private List<List<Double>> data;

}
