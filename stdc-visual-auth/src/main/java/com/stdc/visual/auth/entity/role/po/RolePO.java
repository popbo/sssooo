package com.stdc.visual.auth.entity.role.po;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wang_jie
 * @data 2022/4/21 16:48
 * @describe: 角色实体类
 */
@Data
@Accessors(chain = true)
public class RolePO implements Serializable {

	/**
	 * 角色id
	 */
	private String roleId;

	private String roleKey;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

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

	/** 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示） */
	private boolean menuCheckStrictly;

	/** 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ） */
	private boolean deptCheckStrictly;

}
