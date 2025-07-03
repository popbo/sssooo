package com.stdc.visual.auth.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.auth.entity.dept.dto.DeptDTO;
import com.stdc.visual.auth.entity.dept.po.DeptPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:wang_jie
 * @data:2022/4/24--20:42
 * @describe:组织
 */
public interface DeptMapper extends BaseMapper<DeptPO> {


	/**
	 * 新增组织
	 * @param dto
	 * @return
	 */
	int save(DeptDTO dto);

	/**
	 * 删除组织
	 * @param deptId
	 * @return
	 */
	int deleteByPrimaryKey(@Param("deptId") String deptId);


	/**
	 * 更新组织
	 * @param dto
	 * @return
	 */
	int update(DeptDTO dto);

	/**
	 * 查询组织
	 * @param dto
	 * @return
	 */
	List<DeptDTO> query(DeptDTO dto);

	/**
	 * 查询组织
	 * @param deptId
	 * @return
	 */
	DeptPO queryById(@Param("deptId") String deptId);

	/**
	 * 获取所有组织
	 *
	 *
	 * @return
	 */
	List<DeptPO> queryAll();






}
