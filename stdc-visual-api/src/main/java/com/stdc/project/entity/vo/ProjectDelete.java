package com.stdc.project.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProjectDelete {

    @ApiModelProperty("id数组")
    private String[] idList;

}
