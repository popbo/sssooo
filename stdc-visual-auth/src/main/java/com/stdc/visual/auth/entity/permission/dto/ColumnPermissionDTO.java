package com.stdc.visual.auth.entity.permission.dto;


import com.stdc.visual.auth.entity.permission.po.ColumnPermissionPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wang_jie
 * @data 2022/4/22 14:55
 * @describe:列权限实体类
 */
@Data
@Accessors(chain = true)
public class ColumnPermissionDTO extends ColumnPermissionPO implements Serializable {

	private static final long serialVersionUID = 1L;

}
