package com.stdc.project.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectAdd implements Serializable {

    @ApiModelProperty("工程名称")
    private String name;

}
