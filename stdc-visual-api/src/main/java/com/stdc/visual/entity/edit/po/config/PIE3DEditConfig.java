package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/23--13:54
 * @describe:  工程化编辑 - 3D饼图 - 复用配置 -配置类
 */
public enum PIE3DEditConfig implements VisualEdtConfigInterface{

    PIE_3_D_EDIT_SETTING("饼图设置","isAnnulus,radius,insideRadius,outsideRadius,depth,sort,notCount"),

    CUSTOM_COLOR_SETTING("自定义配色","nameColor,lineColor,barColor"),

    LEGEND_SETTINGS("图例操作","legend,legendHorizontal,legendVertical,legendOrient,legendWidth,legendHeight,legendFontSize,legendColor"),

    REMINDER_SETTINGS("提示语设置","tipFontSize,tipColor"),

    NUMBER_SETTINGS("数值设置","labelShow,labelShowFontSize,labelShowColor,labelShowFontWeight"),

    EDIT_TITLE("标题设置","isShowTitle,titleText,titleTextColor,titleTextFontSize,textAlign"),

    ;


    private String name;

    private String config;

    PIE3DEditConfig(String name, String config) {
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
