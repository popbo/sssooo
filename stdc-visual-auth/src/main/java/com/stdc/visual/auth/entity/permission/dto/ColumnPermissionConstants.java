package com.stdc.visual.auth.entity.permission.dto;

/**
 * @author: wang_jie
 * @data: 2022/9/1--9:59
 * @describe: 列权限 限制类型
 */
public enum ColumnPermissionConstants {

    /**
     * 禁用
     */
    Prohibit("Prohibit","禁用"),

    /**
     * 脱敏
     */
    Desensitization("Desensitization","脱敏");


    /**
     * 值
     */
    private String value;

    /**
     * 名称
     */
    private String name;

    /**
     * 脱敏数据模板
     */
    public static final String DesensitizationTemplate = "******";

    ColumnPermissionConstants(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
