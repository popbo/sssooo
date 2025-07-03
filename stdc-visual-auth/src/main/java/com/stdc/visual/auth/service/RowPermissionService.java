package com.stdc.visual.auth.service;


import com.stdc.visual.auth.entity.permission.dto.RowPermissionDTO;
import com.stdc.visual.auth.entity.permission.po.RowPermissionPO;
import com.stdc.visual.auth.entity.permission.vo.RowPermissionVO;

import java.util.List;

/**
 * @author wang_jie
 * @data 2022/4/22 14:44
 * @describe:行权限操作抽象类
 */
public interface RowPermissionService {


	/**
	 * 新增行权限
	 * @param dto
	 */
	Boolean save(RowPermissionDTO dto);

	/**
	 * 删除行权限
	 * @param id
	 */
	Boolean deleteById(String id);

	/**
	 * 更新行权限
	 * @param dto
	 */
	Boolean update(RowPermissionDTO dto);

	/**
	 * 查询行权限
	 * @param request
	 * @return
	 */
	List<RowPermissionPO> query(RowPermissionDTO request);

	/**
	 * 查询行权限
	 * @param request
	 * @return
	 */
	List<RowPermissionVO> queryVO(RowPermissionDTO request);

	/**
	 * 获取所有行权限
	 * @return
	 */
	List<RowPermissionPO> allPermissionS();



}
