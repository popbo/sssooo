package com.stdc.visual.auth.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.visual.auth.entity.dept.dto.DeptDTO;
import com.stdc.visual.auth.entity.dept.po.DeptPO;

import java.util.List;

/**
 * @author:wang_jie
 * @data:2022/4/24--20:34
 * @describe:组织抽象操作类
 */
public interface DeptService extends IService<DeptPO> {

	/**
	 * 新增组织
	 * @param dto
	 */
	 Boolean save(DeptDTO dto);

	/**
	 * 删除组织
	 * @param id
	 */
	 Boolean deleteById(String id);

	/**
	 * 更新组织
	 * @param dto
	 */
	 Boolean update(DeptDTO dto);

	/**
	 * 查询组织
	 * @param request
	 * @return
	 */
	 List<DeptDTO> query(DeptDTO request);

	/**
	 * 查询组织
	 * @return
	 */
	DeptPO queryDeptById(String id);

	/**
	 * 获取所有组织
	 * @return
	 */
	 List<DeptDTO> allDepts();

	/**
	 * 查询部门树结构信息
	 *
	 * @param dept 部门信息
	 * @return 部门树信息集合
	 */
	//public List<DeptDTO> selectDeptTreeList(DeptPO dept);

}
