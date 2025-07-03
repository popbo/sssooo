package com.stdc.visual.auth.entity.permission.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wang_jie
 * @data 2022/4/22 14:09
 * @describe:行权限实体类
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "RowPermissionPO", description = "行权限实体类")
public class RowPermissionPO implements Serializable {

	@ApiModelProperty(value = "行权限主键id")
	private String id;

	@ApiModelProperty(value = "权限类型 角色/用户/组织 ；role/user/dept")
	private String authTargetType;

	@ApiModelProperty(value = "权限对象的 主键 id")
	private String authTargetId;

	@ApiModelProperty(value = "数据集 ID")
	private String datasetId;

	@ApiModelProperty(value = "数据集字段ID")
	private String datasetFieldId;

	/**
	 * 条件 ps: [{"term":"not like","value":"122131"},{"term":"like","value":"111"}]
	 * [{"term":"eq","value":"华为"}]
	 */
	@ApiModelProperty(value = "条件 ps: [{\"term\":\"not like\",\"value\":\"122131\"},{\"term\":\"like\",\"value\":\"111\"}]，[{\"term\":\"eq\",\"value\":\"华为\"}]")
	private String filter;

	/**
	 * 与/或 and/or
	 */
	@ApiModelProperty(value = "与/或  and/or")
	private String logic;

	/**
//	 * 过滤类型:逻辑、枚举 enum /logic
	 */
	@ApiModelProperty(value = "过滤类型:逻辑、枚举   logic/enum  ")
	private String filterType;

	/**
	 * 枚举值
	 */
	@ApiModelProperty(value = "枚举值")
	private String enumCheckField;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	private Long updateTime;

	private static final long serialVersionUID = 1L;
}
