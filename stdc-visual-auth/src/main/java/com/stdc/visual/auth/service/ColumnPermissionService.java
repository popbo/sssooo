package com.stdc.visual.auth.service;



import com.stdc.visual.auth.entity.permission.dto.ColumnPermissionDTO;
import com.stdc.visual.auth.entity.permission.po.ColumnPermissionPO;
import com.stdc.visual.auth.entity.permission.vo.ColumnPermissionVO;

import java.util.List;

/**
 * @author wang_jie
 * @data 2022/4/22 14:50
 * @describe:列权限操作抽象类
 */
public interface ColumnPermissionService {


	/**
	 * 新增列权限
	 * @param dto
	 */
	Boolean save(ColumnPermissionDTO dto);

	/**
	 * 删除列权限
	 * @param id
	 */
	Boolean deleteById(String id);

	/**
	 * 更新列权限
	 * @param dto
	 */
	Boolean update(ColumnPermissionDTO dto);

	/**
	 * 查询列权限
	 * @param request
	 * @return
	 */
	List<ColumnPermissionVO> query(ColumnPermissionDTO request);

	/**
	 * 获取所有列权限
	 * @return
	 */
	List<ColumnPermissionPO> allPermissionS();


}
