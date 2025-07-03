package com.stdc.visual.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/6/23--15:01
 * @describe: 可视化大屏收藏夹
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "bookmark")
@ApiModel(value = "可视化大屏收藏夹", description = "可视化大屏收藏夹")
public class BookMark {

    /**
     * 主键（唯一）
     */
    @TableId
    private String id;

    /**
     * 名称（唯一）
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 收藏分类ID
     */
    @ApiModelProperty(value = "收藏分类ID")
    private String typeId;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 是否为默认图片
     */
    @ApiModelProperty(value = "是否为默认图片")
    private String isDefault;

    /**
     * 名称
     */
    @ApiModelProperty(value = "路径")
    private String url;

    /**
     * 名称
     */
    @ApiModelProperty(value = "组件详情")
    public String detail;

}