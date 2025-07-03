package com.stdc.visual.auth.entity.role.po;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wang_jie
 * @data 2022/4/21 16:56
 * @describe:角色-用户 绑定 实体类
 */
@Data
@Accessors(chain = true)
public class UserRolePO implements Serializable {

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 角色id
	 */
	private String roleId;

	private static final long serialVersionUID = 1L;
}
