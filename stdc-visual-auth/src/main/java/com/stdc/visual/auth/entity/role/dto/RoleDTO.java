package com.stdc.visual.auth.entity.role.dto;

import com.stdc.visual.auth.entity.role.po.RolePO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author wang_jie
 * @data 2022/4/21 16:48
 * @describe:角色实体类
 */
@Data
@Accessors(chain = true)
public class RoleDTO extends RolePO implements Serializable {

    /** 角色菜单权限 */
    private Set<String> permissions;

}
