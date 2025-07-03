package com.stdc.visual.auth.mapper;


import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.po.RolePO;

import java.util.List;

/**
 * @author wang_jie
 * @data 2022/4/21 19:31
 * @describe: 角色操作类
 */
public interface RoleMapper {


	/**
	 * 新增角色
	 * @param dto
	 * @return
	 */
	int save(RoleDTO dto);

	/**
	 * 删除角色
	 * @param roleId
	 * @return
	 */
	int deleteByPrimaryKey(String roleId);


	/**
	 * 更新角色
	 * @param dto
	 * @return
	 */
	int update(RoleDTO dto);

	/**
	 * 查询角色
	 * @param dto
	 * @return
	 */
	List<RolePO> query(RoleDTO dto);

	/**
	 * 查询角色
	 * @param roleId
	 * @return
	 */
	RolePO queryByRoleId(String roleId);

	/**
	 * 获取所有角色
	 * @return
	 */
	List<RolePO> queryAll();

	/**
	 * 根据用户ID查询角色
	 *
	 * @param userId 用户ID
	 * @return 角色列表
	 */
	public List<RolePO> selectRolePermissionByUserId(String userId);

}
