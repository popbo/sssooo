package com.stdc.visual.auth.entity.permission.dto;

import com.stdc.core.tool.utils.StringUtil;

/**
 * @author: wang_jie
 * @data: 2022/8/31--9:31
 * @describe: 权限对象类型
 */
public enum AuthTargetType {

    USER("user","用户"),

    ROLE("role","角色"),

    DEPT("dept","组织");

    private String type;

    private String name;

    AuthTargetType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String getNameByType(String type) {
        AuthTargetType[] values = AuthTargetType.values();
        for (AuthTargetType value : values) {
            if (StringUtil.equals(value.type,type)){
                return value.name;
            }
        }
        return null;
    }
}
