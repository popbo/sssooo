package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/22--15:36
 * @describe: 工程化编辑 - 折线图 - 复用配置 -配置类
 */
public enum TextEditConfig implements VisualEdtConfigInterface{

    FONT_SIZE_SETTINGS("字体大小","fontSize"),

    FONT_COLOR_SETTINGS("字体颜色","color"),

    FONT_SPACE_SETTINGS("字体间距","split"),

    FONT_LINE_HEIGHT_SETTINGS("字体行高","lineHeight"),

    FONT_BACKGROUND_SETTINGS("字体背景","backgroundColor"),

    SHADOW_SETTINGS("阴影设置","textShadow"),

    FONT_THICKNESS_SETTINGS("文字粗细","fontWeight"),

    FONT_TYPE_SETTINGS("字体系列","fontFamily"),

    FONT_ALIGN_SETTINGS(" 对齐方式","textAlign"),

    ;


    private String name;

    private String config;

    TextEditConfig(String name, String config) {
        this.name = name;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public String getConfig() {
        return config;
    }
}
