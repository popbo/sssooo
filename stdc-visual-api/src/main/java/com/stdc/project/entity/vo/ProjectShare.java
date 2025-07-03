package com.stdc.project.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProjectShare {

    @ApiModelProperty("批量传id数组")
    private String[] idList;

    @ApiModelProperty("发布传id")
    private String id;

    @ApiModelProperty("发布状态")
    private Boolean share;

    @ApiModelProperty("发布地址前缀")
    private String sharedUrlPrefix;

}
