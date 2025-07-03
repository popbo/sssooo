package com.stdc.visual.auth.entity.cas.po;

import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/10/26--14:37
 * @describe: 统一认证-远程调用-角色信息实体
 */
@Data
public class RolePO {

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String name;

}
