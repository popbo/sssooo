package com.stdc.visual.auth.mapper;


import com.stdc.visual.auth.entity.permission.dto.ColumnPermissionDTO;
import com.stdc.visual.auth.entity.permission.po.ColumnPermissionPO;

import java.util.List;

/**
 * @author:wang_jie
 * @data:2022/4/22—15:33
 * @describe:列权限
 */
public interface ColumnPermissionMapper {


	/**
	 * 新增列权限
	 * @param dto
	 * @return
	 */
	int save(ColumnPermissionDTO dto);

	/**
	 * 删除列权限
	 * @param id
	 * @return
	 */
	int deleteByPrimaryKey(String id);


	/**
	 * 更新列权限
	 * @param dto
	 * @return
	 */
	int update(ColumnPermissionDTO dto);

	/**
	 * 查询列权限
	 * @param dto
	 * @return
	 */
	List<ColumnPermissionPO> query(ColumnPermissionDTO dto);

	/**
	 * 查询所有列权限详情
	 * @return
	 */
	List<String> queryAllPermission();

	/**
	 * 获取所有列权限
	 *
	 *
	 * @return
	 */
	List<ColumnPermissionPO> queryAll();

}
