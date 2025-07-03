package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/30--14:53
 * @describe: 工程化编辑 - 雷达图 - 复用配置 -配置类
 */
public enum RadarEditConfig implements VisualEdtConfigInterface {

    EDIT_TITLE("标题设置","titleShow,title,titleColor,titleFontSize,titlePostion,subtext,subTitleColor,subTitleFontSize"),

    NUMBER_SETTINGS("数值设置","labelShow,labelShowFontSize,labelShowColor,labelShowFontWeight"),

    REMINDER_SETTINGS("提示语设置","tipFontSize,tipColor"),

    LEGEND_SETTINGS("图例操作","legend,legendPostion,legendOrient,legendWidth,legendHeight,legendFontSize,legendColor"),

    CUSTOM_COLOR_SETTING("自定义配色","nameColor,lineColor,barColor"),

    FONT_SIZE("字体大小","radarNameSize"),

    FONT_COLOR("字体颜色","radarNameColor"),

    AREA_OPACITY_SETTINGS("区域透明度","areaOpacity"),
    ;
    private String name;

    private String config;

    RadarEditConfig(String name, String config) {
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
