package com.stdc.visual.entity.edit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/17--15:42
 * @describe: 工程化编辑 操作类
 */
@Data
@ApiModel(value = "工程化编辑操作类", description = "工程化编辑操作类")
public class VisualEditDTO {

    /**
     * config id
     */
    @ApiModelProperty(value = "大屏页面id")
    private Long configId;

    /**
     * 全局替换内容
     */
    @ApiModelProperty(value = "全局替换内容")
    List<ReplaceAllEditDTO> replaceAllEditS;

    /**
     * 组件修改内容
     */
    List<ComponentEditDTO> componentEditDTOS;


}
