package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/30--14:58
 * @describe:  工程化编辑 - 漏斗图 - 复用配置 -配置类
 */
public enum FunnelEditConfig implements VisualEdtConfigInterface{

    EDIT_TITLE("标题设置","titleShow,title,titleColor,titleFontSize,titlePostion,subtext,subTitleColor,subTitleFontSize"),

    REMINDER_SETTINGS("提示语设置","tipFontSize,tipColor"),

    LEGEND_SETTINGS("图例操作","legend,legendPostion,legendOrient,legendWidth,legendHeight,legendFontSize,legendColor"),

    CUSTOM_COLOR_SETTING("自定义配色","barColor"),
    ;

    private String name;

    private String config;

    FunnelEditConfig(String name, String config) {
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
