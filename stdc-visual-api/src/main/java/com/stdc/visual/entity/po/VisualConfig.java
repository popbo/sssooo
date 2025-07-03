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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 可视化配置表实体类
 *
 * @author wangjie
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "config")
@ApiModel(value = "VisualConfig对象", description = "可视化配置表")
public class VisualConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    private Long id;
    /**
     * 可视化表主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "可视化表主键")
    private Long visualId;
    /**
     * 配置json
     */
    @ApiModelProperty(value = "配置json")
    private String detail;
    /**
     * 组件json
     */
    @ApiModelProperty(value = "组件json")
    private String component;
    /**
     * 版本
     */
    @ApiModelProperty(value = "version")
    private String version;
    /**
     * 版本备注
     */
    @ApiModelProperty(value = "版本备注")
    private String versionRemark;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Long createTime = System.currentTimeMillis();
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;
    /**
     * 背景图片id
     */
    @ApiModelProperty(value = "背景图片主键id")
    private String backgroundId;

    /**
     * 状态[0:未删除,1:删除]
     */
    @TableLogic
    @ApiModelProperty(value = "是否已删除")
    private Integer isDeleted = 0;

}
