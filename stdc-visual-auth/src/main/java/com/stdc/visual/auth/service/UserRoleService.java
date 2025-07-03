package com.stdc.visual.auth.service;



import com.stdc.visual.auth.entity.role.dto.UserRoleDTO;
import com.stdc.visual.auth.entity.role.po.UserRolePO;

import java.util.List;

/**
 * @author wang_jie
 * @data 2022/4/22 11:31
 * @describe:角色用户抽象类
 */
public interface UserRoleService {
	/**
	 * 保存角色-用户丽连接关系
	 * @param dto
	 */
	Boolean save(UserRoleDTO dto);

	/**
	 * 删除角色-用户关系
	 * @param dto
	 */
	Boolean delete(UserRoleDTO dto);

	/**
	 * 更新角色-用户关系
	 * @param dto
	 */
	Boolean updateById(UserRoleDTO dto);

	/**
	 * 查询角色-用户关系
	 * @param request
	 * @return
	 */
	List<UserRolePO> queryById(UserRoleDTO request);

	/**
	 * 查询角色-用户关系
	 * @param userId
	 * @return
	 */
	List<UserRolePO> queryByUserId(String userId);


	/**
	 * 获取所有角色-用户关系
	 * @return
	 */
	List<UserRolePO> queryAll();

	/**
	 * 查询用户所有角色
	 * @param userId
	 * @return
	 */
	List<String> roles(Long userId);
}
