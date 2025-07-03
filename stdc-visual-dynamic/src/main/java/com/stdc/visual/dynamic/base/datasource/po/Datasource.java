package com.stdc.visual.dynamic.base.datasource.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--19:57
 * @describe: 数据源数据库实体
 */
@Data
@TableName(value = StdcVisualConstant.PROJECT_PREFIX + "datasource")
public class Datasource implements Serializable {
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("创建时间")
    private Long createTime;
    @ApiModelProperty("更新时间")
    private Long updateTime;
    @ApiModelProperty("创建者")
    private String createBy;
    @ApiModelProperty("状态")
    private String status;
    @ApiModelProperty("配置详情")
    private String configuration;

    private static final long serialVersionUID = 1L;
}