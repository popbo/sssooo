package com.stdc.visual.auth.entity.permission.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author:wang_jie
 * @data:2022/4/24--9:21
 * @describe:行权限权限值
 */
@Data
@Accessors(chain = true)
public class RowPermissionFilterDTO implements Serializable {

	/**
	 * 筛选条件
	 */
	private String term;

	/**
	 * 筛选值
	 */
	private String value;

	private static final long serialVersionUID = 1L;

}
