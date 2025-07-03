package com.stdc.visual.auth.entity.permission.vo;

import com.stdc.visual.auth.entity.permission.po.ColumnPermissionPO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: wang_jie
 * @data: 2022/9/1--17:00
 * @describe:
 */
@Data
@Accessors(chain = true)
public class ColumnPermissionVO extends ColumnPermissionPO {
    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 权限对象名称
     */
    private String name;

}
