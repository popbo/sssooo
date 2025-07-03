package com.stdc.visual.auth.entity.permission.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 行权限字段
 */
@Data
@Accessors(chain = true)
public class ColumnPermissionFilterItem {

	/**
	 * 字段id
	 */
	private String id;

	/**
	 * 字段名称
	 */
	private String name;

	/**
	 * 字段限制类型：默认禁用  Prohibit 禁用 Desensitization 脱敏
	 */
	private String opt;

	/**
	 * 前端是否选中
	 */
	private Boolean selected = false;

}
