package com.stdc.visual.auth.entity.permission.po;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wang_jie
 * @data 2022/4/22 14:51
 * @describe:列权限实体类
 */
@Data
@Accessors(chain = true)
public class ColumnPermissionPO {

	/**
	 * 列权限主键id
	 */
	private String id;

	/**
	 * 权限类型： /角色/用户
	 */
	private String authTargetType;

	/**
	 * 权限对象ID
	 */
	private String authTargetId;

	/**
	 * 数据集ID
	 */
	private String datasetId;

	/**
	 * 权限详情
	 */
	private String permissions;

	/**
	 * 更新时间
	 */
	private Long updateTime;

}
