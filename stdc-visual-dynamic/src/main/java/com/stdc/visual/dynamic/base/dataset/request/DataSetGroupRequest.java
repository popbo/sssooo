package com.stdc.visual.dynamic.base.dataset.request;

import com.stdc.visual.dynamic.base.dataset.po.DatasetGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:45
 * @describe: 数据集分组请求类
 */
@Data
public class DataSetGroupRequest extends DatasetGroup {
    @ApiModelProperty("排序")
    private String sort;
    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("ID集合")
    private Set<String> ids;
}

