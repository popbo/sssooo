package com.stdc.visual.dynamic.base.dataset.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:30
 * @describe: 数据集分组
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "dataset_group")
public class DatasetGroup implements Serializable {
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("父ID")
    private String pid;
    @ApiModelProperty("级别")
    private Integer level;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("创建者")
    private String createBy;
    @ApiModelProperty("创建时间")
    private Long createTime;

    private static final long serialVersionUID = 1L;
}
