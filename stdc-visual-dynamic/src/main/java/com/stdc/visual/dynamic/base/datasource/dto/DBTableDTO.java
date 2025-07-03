package com.stdc.visual.dynamic.base.datasource.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: wang_jie
 * @data: 2022/5/17--9:38
 * @describe: 数据源下属表信息
 */
@Data
public class DBTableDTO {
    @ApiModelProperty("数据源ID")
    private String datasourceId;
    @ApiModelProperty("数据源名称")
    private String name;
    @ApiModelProperty("表注释")
    private String remark;
    @ApiModelProperty("启用检测")
    private boolean enableCheck;
    @ApiModelProperty("数据集路径")
    private String datasetPath;
}