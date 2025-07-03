package com.stdc.visual.auth.entity.permission.dto;


import com.stdc.visual.auth.entity.permission.po.RowPermissionPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wang_jie
 * @data 2022/4/22 14:41
 * @describe:行权限实体类
 */
@Data
@Accessors(chain = true)
public class RowPermissionDTO extends RowPermissionPO implements Serializable {

	private static final long serialVersionUID = 1L;

}
