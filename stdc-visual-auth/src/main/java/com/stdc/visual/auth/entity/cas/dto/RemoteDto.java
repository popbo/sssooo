package com.stdc.visual.auth.entity.cas.dto;

import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/10/26--14:36
 * @describe: 统一认证-远程调用-返回信息实体类
 */
@Data
public class RemoteDto {

    /**
     * 是否返回成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 角色-用户-关系
     */
    private UserRoleDto data;
}
