package com.stdc.visual.entity.edit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2023/5/17--19:17
 * @describe: 模板组件视图类
 */
@Data
@Builder
@ApiModel(value = "模板组件视图类")
public class TemplateComponentVO {
    /**
     * 模板组件 id
     */
    @ApiModelProperty(value = "组件id")
    private String id;

    /**
     * 模板组件 名称
     */
    @ApiModelProperty(value = "组件名称")
    private String name;
}
