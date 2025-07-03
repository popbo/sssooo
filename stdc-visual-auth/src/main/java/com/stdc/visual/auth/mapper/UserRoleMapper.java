package com.stdc.visual.auth.mapper;

import com.stdc.visual.auth.entity.role.dto.UserRoleDTO;
import com.stdc.visual.auth.entity.role.po.UserRolePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wang_jie
 * @data 2022/4/22 11:04
 * @describe: 用户-角色
 */
public interface UserRoleMapper {
	/**
	 * 新增角色
	 * @param dto
	 * @return
	 */
	int save(UserRoleDTO dto);

	/**
	 * 删除角色
	 * @param dto
	 * @return
	 */
	int deleteById(UserRoleDTO dto);


	/**
	 * 更新角色
	 * @param dto
	 * @return
	 */
	int updateById(UserRoleDTO dto);

	/**
	 * 查询角色
	 * @param dto
	 * @return
	 */
	List<UserRolePO> queryById(UserRoleDTO dto);

	/**
	 * 查询角色
	 * @param userId
	 * @return
	 */
	List<UserRolePO> queryByUserId(String userId);

	/**
	 * 获取所有角色
	 * @return
	 */
	List<UserRolePO> queryAll();

	/**
	 * 查询当前用户所有角色id
	 * @param userId
	 * @return
	 */
	List<String> roleCodes(@Param("userId") Long userId);
}
