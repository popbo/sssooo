package com.stdc.visual.auth.entity.user.dto;

import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: wang_jie
 * @data: 2022/5/24--20:22
 * @describe: 用户类额外增强 添加 token
 */
@Data
public class SysUserDTO extends SysUser implements Serializable {

    private String token;

    @ApiModelProperty("角色集合")
    private List<RoleDTO> roles;

    @ApiModelProperty("融合监控")
    private Map<String,String> fusionMonitor;


    @ApiModelProperty("权限集合")
    private Set<String> permissionsSet;//ruoyi
    @ApiModelProperty("角色集合")
    private Set<String> rolesSet;//ruoyi

}
