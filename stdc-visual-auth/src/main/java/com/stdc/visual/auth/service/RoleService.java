package com.stdc.visual.auth.service;


import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.po.RolePO;
import com.stdc.visual.auth.entity.user.po.SysUser;

import java.util.List;
import java.util.Set;

/**
 * @author wang_jie
 * @data 2022/4/21 17:02
 * @describe: 角色操作抽象类
 */
public interface RoleService {

	/**
	 * 保存角色
	 * @param dto
	 */
	Boolean save(RoleDTO dto);

	/**
	 * 删除角色
	 * @param roleId
	 */
	Boolean delete(String roleId);

	/**
	 * 更新角色
	 * @param dto
	 */
	Boolean update(RoleDTO dto);

	/**
	 * 查询角色
	 * @param request
	 * @return
	 */
	List<RolePO> query(RoleDTO request);

	/**
	 * 查询用户角色信息
	 * @param user
	 * @return
	 */
	List<RoleDTO> queryByUser(SysUser user);

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
	List<RolePO> allRoles();

	/**
	 * 根据用户ID查询角色权限
	 *
	 * @param userId 用户ID
	 * @return 权限列表
	 */
	public Set<String> selectRolePermissionByUserId(String userId);

}
