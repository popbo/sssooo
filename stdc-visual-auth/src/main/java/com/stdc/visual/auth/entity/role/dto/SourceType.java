package com.stdc.visual.auth.entity.role.dto;

/**
 * @author: wang_jie
 * @data: 2022/8/25--15:43
 * @describe: 资源类型
 */
public enum SourceType {

    /**
     * 大屏
     */
    VISUAL("VISUAL"),

    /**
     * 大屏类型
     */
    VISUAL_CATEGORY("VISUAL_CATEGORY"),

    /**
     * 数据源
     */
    DATASOURCE("DATASOURCE"),

    /**
     * 数据集
     */
    DATA_SET("DATA_SET"),

    /**
     * 数据集分组
     */
    DATA_SET_GROUP("DATA_SET_GROUP"),

    /**
     * 收藏类型
     */
    BOOKMARK_TYPE("BOOKMARK_TYPE"),

    /**
     * 收藏
     */
    BOOKMARK("BOOKMARK"),
    /**
     * 资源文件
     */
    OSS_FILE("OSS_FILE"),
    /**
     * 组态2D平台资源文件
     */
    OSS_FILE_TOPOLOGY_2D("OSS_FILE_TOPOLOGY_2D"),
    /**
     * 组态3D平台资源文件
     */
    OSS_FILE_TOPOLOGY_3D("OSS_FILE_TOPOLOGY_3D"),
    /**
     * 资源文件
     */
    OSS_FILE_SVG("OSS_FILE_SVG"),
    /**
     * 资源文件 - 3D文件
     */
    OSS_FILE_GLB("OSS_FILE_GLB");

    private String value;

    public String getValue() {
        return value;
    }

    SourceType(String value) {
        this.value = value;
    }

}
