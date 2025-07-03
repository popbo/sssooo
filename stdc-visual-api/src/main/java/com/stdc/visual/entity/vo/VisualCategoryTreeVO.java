package com.stdc.visual.entity.vo;

import com.stdc.visual.entity.po.VisualCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2024/11/15
 * @describe: 可视化分类树状图视图类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisualCategoryTreeVO extends VisualCategory {

    //子节点
    private List<VisualCategoryTreeVO> children;

}
