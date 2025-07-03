package com.stdc.visual.auth.entity.cas.dto;

import com.stdc.visual.auth.entity.cas.po.RolePO;
import com.stdc.visual.auth.entity.cas.po.UserPO;
import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/10/26--14:41
 * @describe: 角色-用户
 */
@Data
public class UserRoleDto {

    /**
     * 用户
     */
    private UserPO user;

    /**
     * 角色
     */
    private List<RolePO> role;

}
