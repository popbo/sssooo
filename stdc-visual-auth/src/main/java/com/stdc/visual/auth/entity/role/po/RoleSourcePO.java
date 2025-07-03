package com.stdc.visual.auth.entity.role.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/8/25--15:39
 * @describe: 角色 - 资源 映射实体类
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "sys_role_source")
@ApiModel(value = "RoleSourcePO", description = "角色-资源映射表")
public class RoleSourcePO {

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    private String roleId;

    /**
     * 资源id
     */
    @ApiModelProperty(value = "资源id")
    private String sourceId;

    /**
     * 资源类型
     */
    @ApiModelProperty(value = "资源类型")
    private String sourceType;

    /**
     * 权限类型
     */
    @ApiModelProperty(value = "权限类型")
    private String authType;

}
