package com.stdc.visual.auth.entity.permission.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author:wang_jie
 * @data:2022/4/24--16:48
 * @describe:列权限权限值
 */
@Data
@Accessors(chain = true)
public class ColumnPermissionFilterDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 行权限是否开启
	 */
	private Boolean enable;

	/**
	 * 列权限列表字段
	 */
	private List<ColumnPermissionFilterItem> columns;

}


