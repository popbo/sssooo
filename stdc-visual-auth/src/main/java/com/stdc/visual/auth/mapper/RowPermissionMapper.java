package com.stdc.visual.auth.mapper;


import com.stdc.visual.auth.entity.permission.dto.RowPermissionDTO;
import com.stdc.visual.auth.entity.permission.po.RowPermissionPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wang_jie
 * @data 2022/4/22 15:34
 * @describe:行权限
 */
public interface RowPermissionMapper {


	/**
	 * 新增行权限
	 * @param dto
	 * @return
	 */
	int save(RowPermissionDTO dto);

	/**
	 * 删除行权限
	 * @param id
	 * @return
	 */
	int deleteByPrimaryKey(String id);


	/**
	 * 更新行权限
	 * @param dto
	 * @return
	 */
	int update(RowPermissionDTO dto);

	/**
	 * 查询行权限
	 * @param dto
	 * @return
	 */
	List<RowPermissionPO> query(RowPermissionDTO dto);


	/**
	 * 通过字段id获取到字段名称
	 * @param id
	 * @return
	 */
	String queryFieldNameById(@Param("id") String id);

	/**
	 * 获取所有行权限
	 * @return
	 */
	List<RowPermissionPO> queryAll();

}
