package com.stdc.topology2d.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiModel(value = "组件工程化全局同步", description = "组件工程化全局同步")
public class EngineeringGlobalSynchronizationFrom {

    @ApiModelProperty("图纸id")
    private String id;

    @ApiModelProperty("组件文字")
    private String text;

    @ApiModelProperty("组件新文字")
    private String newtext;

}
