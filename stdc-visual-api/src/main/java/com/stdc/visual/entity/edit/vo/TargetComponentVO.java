package com.stdc.visual.entity.edit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2023/5/17--19:16
 * @describe: 目标组件视图类
 */
@Data
@Builder
@ApiModel(value = "目标组件视图类")
public class TargetComponentVO {

    /**
     * 目标组件 id
     */
    @ApiModelProperty(value = "组件id")
    private String id;

    /**
     * 目标组件 名称
     */
    @ApiModelProperty(value = "组件名称")
    private String name;

}
