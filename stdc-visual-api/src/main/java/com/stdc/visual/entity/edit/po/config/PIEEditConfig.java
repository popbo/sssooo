package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/26--10:38
 * @describe:  工程化编辑 - 饼图 - 复用配置 -配置类
 */
public enum PIEEditConfig implements VisualEdtConfigInterface {

    EDIT_TITLE("标题设置","titleShow,title,titleColor,titleFontSize,titlePostion,subtext,subTitleColor,subTitleFontSize"),

    PIE_EDIT_SETTING("饼图设置", "isAnnulus,radius,insideRadius,outsideRadius,depth,sort,notCount"),

    ROTATION_DYNAMIC_EFFECT_SETTINGS("轮播动效", "showAnimation,animationInterval"),

    CUSTOM_COLOR_SETTING("自定义配色","barColor"),

    LEGEND_SETTINGS("图例操作","legend,legendPostion,legendOrient,legendWidth,legendHeight,legendFontSize,legendColor"),

    REMINDER_SETTINGS("提示语设置","tipFontSize,tipColor"),

    NUMBER_SETTINGS("数值设置","labelShow,labelShowFontSize,labelShowColor,labelShowFontWeight,xLableRotate"),
    ;


    private String name;

    private String config;

    PIEEditConfig(String name, String config) {
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