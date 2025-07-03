package com.stdc.visual.entity.po;

/**
 * @author: wang_jie
 * @data: 2022/11/24--10:09
 * @describe: 大屏类型
 */
public enum BackGroundType {

    //默认
    DEFAULT("DEFAULT"),

    //自定义
    CUSTOM("CUSTOM"),

    //缩略图
    THUMBNAIL("THUMBNAIL");

    private String value;

    public String getValue() {
        return value;
    }

    BackGroundType(String value) {
        this.value = value;
    }
}
