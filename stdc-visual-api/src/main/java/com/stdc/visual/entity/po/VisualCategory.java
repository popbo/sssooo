/*
 *      Copyright (c) 2018-2028, wangjie Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: wangjie 庄骞 (smallwangjie@163.com)
 */
package com.stdc.visual.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 可视化分类表实体类
 *
 * @author wangjie
 */
@Data
@NoArgsConstructor
@TableName(StdcVisualConstant.PROJECT_PREFIX + "category")
@ApiModel(value = "VisualCategory对象", description = "可视化分类表")
public class VisualCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    private Long id;
    /**
     * 分类键值
     */
    @ApiModelProperty(value = "分类键值")
    private String categoryKey;
    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    private String categoryValue;
    /**
     * 分类来源:来自哪里呢？0:可视化,1:组态,2:3D组态
     */
    @ApiModelProperty(value = "分类来源")
    private String categorySource;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
    /**
     * 父类id,根目录为0
     */
    @ApiModelProperty(value = "父类id,根目录为0")
    private String parentId;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Long createTime;
    /**
     * 是否已删除
     */
    @TableLogic
    @ApiModelProperty(value = "是否已删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "工程id")
    private String projectId;

    @ApiModelProperty("当前页")
    @TableField(exist = false)
    private Integer current;

    @ApiModelProperty("每页的数量")
    @TableField(exist = false)
    private Integer size;

    public VisualCategory(String categoryKey, String categoryValue, String description) {
        this.categoryKey = categoryKey;
        this.categoryValue = categoryValue;
        this.description = description;
    }
}
