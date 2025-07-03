package com.stdc.project.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectUpdate implements Serializable {

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("工程名称")
    private String name;

}
