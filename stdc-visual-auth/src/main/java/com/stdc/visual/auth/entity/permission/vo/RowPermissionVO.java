package com.stdc.visual.auth.entity.permission.vo;

import com.stdc.visual.auth.entity.permission.po.RowPermissionPO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: wang_jie
 * @data: 2022/8/31--9:19
 * @describe:
 */
@Data
@Accessors(chain = true)
public class RowPermissionVO extends RowPermissionPO {

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 权限对象名称
     */
    private String name;

    /**
     * 字段名
     */
    private String fieldName;

}
