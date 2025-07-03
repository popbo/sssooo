package com.stdc.topology2d.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CMP配置版本", description = "CMP配置版本")
public class CmpVersion {

    @ApiModelProperty(value = "发布ID", name = "发布ID")
    private String releaseId;

    @ApiModelProperty(value = "版本号", name = "版本号")
    private String version;

    @ApiModelProperty(value = "发布时间时间戳", name = "发布时间时间戳")
    private String time;

    @ApiModelProperty(value = "发布标题", name = "发布标题")
    private String title;

    @ApiModelProperty(value = "发布描述", name = "发布描述")
    private String description;

}
