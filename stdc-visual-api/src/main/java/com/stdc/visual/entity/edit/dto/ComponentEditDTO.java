package com.stdc.visual.entity.edit.dto;

import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/17--15:58
 * @describe: 组件修改内容
 */
@Data
@ApiModel(value = "组件修改内容", description = "组件修改内容")
public class ComponentEditDTO {

    /**
     * 样板组件id
     */
    @ApiModelProperty(value = "样板组件id")
    private String templateComponentId;

    /**
     * 操作类型 data 数据 , config 配置 , event 事件 , rendering 渲染
     */
    @ApiModelProperty(value = "操作类型 data 数据 , config 配置 , event 事件 , rendering 渲染")
    private List<String> editTypeS;

    /**
     * 复用配置:key(使用逗号隔开)
     */
    @ApiModelProperty(value = "复用配置:key")
    private List<VisualSelect> configS;

    /**
     * 目标组件id
     */
    @ApiModelProperty(value = "目标组件id列表")
    private List<String> targetComponentIdS;


}
