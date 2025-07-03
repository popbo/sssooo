package com.stdc.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工程管理表
 * </p>
 *
 * @author dongbobo
 * @since 2025-06-23
 */
@TableName("stdc_visual_project")
@ApiModel(value = "Project对象", description = "工程管理表")
@Data
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty("工程名称")
    private String name;

    @ApiModelProperty("版本")
    private String version;

    @ApiModelProperty("发布url")
    private String sharedUrl;

    @ApiModelProperty("发布路径")
    private String path;

    private Boolean sharedCustom;

    @ApiModelProperty("分享加密")
    private Boolean sharedEncryption;

    @ApiModelProperty("加密密码")
    private String sharedPassword;

    @ApiModelProperty("发布状态")
    private Boolean shared;

    @ApiModelProperty("发布时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shareTime;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("发布人")
    private String sharedBy;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("是否已删除(0:未删除,1:删除)")
    @TableLogic
    private Integer isDeleted;

}
