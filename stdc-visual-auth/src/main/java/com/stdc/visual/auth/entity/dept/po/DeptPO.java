package com.stdc.visual.auth.entity.dept.po;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author:wang_jie
 * @data:2022/4/24--20:08
 * @describe:组织实体类
 */
@Data
@Accessors(chain = true)
public class DeptPO implements Serializable {

	/**
	 * 组织id
	 */
	private Long deptId;

	/**
	 * 上级部门id
	 */
	private Long pid;

	/**
	 * 子部门数目
	 */
	private Integer subCount;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 排序
	 */
	private Integer deptSort;

	/**
	 * 创建者
	 */
	private String createBy;

	/**
	 * 更新者
	 */
	private String updateBy;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	private static final long serialVersionUID = 1L;

}
