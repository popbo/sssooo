package com.stdc.visual.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2023/10/30--9:53
 * @describe:
 */
@Data
@ApiModel(value = "更新VisualVersion对象", description = "更新VisualVersion对象")
public class VisualVersionDTO{

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本id")
    private String versionId;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "新版本号")
    private String newVersion;

    /**
     * 版本备注
     */
    @ApiModelProperty(value = "新版本备注")
    private String newVersionRemark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;


}
