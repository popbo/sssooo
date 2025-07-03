package com.stdc.visual.dynamic.base.dataset.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:29
 * @describe: 数据集详情
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "dataset_table")
@ApiModel(description = "数据集")
public class DatasetTable implements Serializable {
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("场景ID")
    private String sceneId;
    @ApiModelProperty("数据源ID")
    private String dataSourceId;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("模式")
    private Integer mode;
    @ApiModelProperty("创建者")
    private String createBy;
    @ApiModelProperty("创建时间")
    private Long createTime;
    @ApiModelProperty("定时任务实例")
    private String qrtzInstance;
    @ApiModelProperty("同步状态")
    private String syncStatus;
    @ApiModelProperty("上次更新时间")
    private Long lastUpdateTime;
    @ApiModelProperty("信息")
    private String info;

    private static final long serialVersionUID = 1L;
}
