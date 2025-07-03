package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/30--15:01
 * @describe: 工程化编辑 - 散点图 - 复用配置 -配置类
 */
public enum  ScatterEditConfig implements VisualEdtConfigInterface {

    X_AXIS_SETTINGS("X轴设置","xAxisShow,xAxisName,xAxisNameSize,xAxisSplitLineShow,xAxisSplitLineType,xAxisSplitLineTypeColor,xAxisinterval,xAxisRotate,xAxisInverse,xNameFontSize"),

    Y_AXIS_SETTINGS("Y轴设置","yAxisName,yAxisNameSize,yAxisShow,yAxisSplitLineShow,yAxisSplitLineType,yAxisSplitLineShow,yAxisSplitLineTypeColor,yAxisInverse,yNameFontSize"),

    NUMBER_SETTINGS("数值设置","labelShow,labelShowFontSize,labelShowColor,labelShowFontWeight"),

    REMINDER_SETTINGS("提示语设置","tipFontSize,tipColor"),

    CUSTOM_COLOR_SETTING("自定义配色","barColor"),
    ;

    private String name;

    private String config;

    ScatterEditConfig(String name, String config) {
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
