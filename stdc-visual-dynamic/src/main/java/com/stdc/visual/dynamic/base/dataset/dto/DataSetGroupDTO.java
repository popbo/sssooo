package com.stdc.visual.dynamic.base.dataset.dto;

import com.stdc.visual.common.base.ITreeBase;
import com.stdc.visual.dynamic.base.dataset.po.DatasetGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:39
 * @describe: 数据集分组
 */
@Data
public class DataSetGroupDTO extends DatasetGroup implements ITreeBase<DataSetGroupDTO> {
    @ApiModelProperty("标签")
    private String label;
    @ApiModelProperty("子节点")
    private List<DataSetGroupDTO> children;
    @ApiModelProperty("权限")
    private String privileges;

    @ApiModelProperty("节点类型")
    public String getNodeType() {
        return super.getType();
    }

    ;
}
