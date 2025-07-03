package com.stdc.visual.auth.entity.menu.dto;

import com.stdc.visual.auth.entity.menu.po.SysMenu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysMenuDto extends SysMenu implements Serializable {

    /** 子菜单 */
    @ApiModelProperty("子菜单")
    private List<SysMenuDto> children = new ArrayList<SysMenuDto>();

}
