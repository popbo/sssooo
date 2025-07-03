package com.stdc.visual.auth.entity.menu.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.auth.entity.BaseEntity;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "sys_menu")
@ApiModel(value = "SysMenu对象", description = "菜单权限表")
public class SysMenu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("菜单ID")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    @ApiModelProperty("菜单名称")
    private String menuName;

    @ApiModelProperty("父菜单ID")
    private Long parentId;

    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    @ApiModelProperty("路由地址")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("路由参数")
    private String query;

    @ApiModelProperty("路由名称")
    private String routeName;

    @ApiModelProperty("是否为外链（0是 1否）")
    private Integer isFrame;

    @ApiModelProperty("是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    @ApiModelProperty("菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    @ApiModelProperty("菜单状态（0显示 1隐藏）")
    private String visible;

    @ApiModelProperty("菜单状态（0正常 1停用）")
    private String status;

    @ApiModelProperty("权限标识")
    private String perms;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String remark;



}
