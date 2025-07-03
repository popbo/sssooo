package com.stdc.visual.auth.entity.role.dto;

import com.stdc.visual.auth.entity.role.po.UserRolePO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wang_jie
 * @data 2022/4/22 11:14
 * @describe:角色用户实体类
 */
@Data
@Accessors(chain = true)
public class UserRoleDTO extends UserRolePO implements Serializable {
}
