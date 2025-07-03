package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/26--11:14
 * @describe:  工程化编辑 - 象形图 - 复用配置 -配置类
 */
public enum PictorialBarEditConfig implements VisualEdtConfigInterface{

    ICON_TYPE_SETTINGS("图标类型","geometricOrImage,symbol"),

    ICON_WIDTH_SETTINGS("图标宽度","symbolSize"),

    ICON_HEIGHT_SETTINGS("图标高度","symbolSize"),

    AXIS_MARGIN_SETTINGS("坐标轴边距","gridX,gridY,gridX2,gridY2"),

    FONT_COLOR_SETTINGS("字体颜色","color"),

    AXIS_FONT_COLOR_SETTINGS("轴字体颜色","nameColor"),

    ICON_COLOR_SETTINGS("图标颜色设置","pictorialBarColor"),

    X_AXIS_SETTINGS("X轴设置","xAxisShow,xNameFontSize"),

    Y_AXIS_SETTINGS("Y轴设置","yAxisShow,yNameFontSize,fontSize"),

    ;


    private String name;

    private String config;

    PictorialBarEditConfig(String name, String config) {
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
