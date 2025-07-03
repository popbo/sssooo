package com.stdc.visual.dynamic.base.dataset.dto;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--15:59
 * @describe:
 */
@Data
public class DataSetTableDTO extends DatasetTable {
    @ApiModelProperty("子节点")
    private List<DataSetTableDTO> children;
    @ApiModelProperty("权限")
    private String privileges;
    @ApiModelProperty("是否叶子结点")
    private Boolean isLeaf;
    @ApiModelProperty("父ID")
    private String pid;
}
