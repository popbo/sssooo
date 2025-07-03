package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/30--14:50
 * @describe: 工程化编辑 - 山峰柱状图 - 复用配置 -配置类
 */
public enum PeakBarEditConfig implements VisualEdtConfigInterface{

    EDIT_TITLE("标题设置","titleShow,title,titleColor,titleFontSize,titlePostion,subtext,subTitleColor,subTitleFontSize"),

    X_AXIS_SETTINGS("X轴设置","xAxisShow,xAxisName,xAxisNameSize,xAxisSplitLineShow,xAxisSplitLineType,xAxisSplitLineTypeColor,xAxisinterval,xAxisRotate,xAxisInverse,xNameFontSize"),

    Y_AXIS_SETTINGS("Y轴设置","yAxisName,yAxisNameSize,yAxisShow,yAxisSplitLineShow,yAxisSplitLineType,yAxisSplitLineShow,yAxisSplitLineTypeColor,yAxisInverse,yNameFontSize"),

    NUMBER_SETTINGS("数值设置","labelShow,labelShowFontSize,labelShowColor,labelShowFontWeight"),

    REMINDER_SETTINGS("提示语设置","tipFontSize,tipColor"),

    AXIS_MARGIN_SETTINGS("坐标轴边距","gridX,gridY,gridX2,gridY2"),

    LEGEND_SETTINGS("图例操作","legend,legendPostion,legendOrient,legendWidth,legendHeight,legendFontSize,legendColor"),

    CUSTOM_COLOR_SETTING("自定义配色","nameColor,lineColor,barColor"),

    COLUMN_SETTINGS("柱体设置","cylinderStyle,barCategoryGap,barGap,TOPN,Ns"),
    ;
    private String name;

    private String config;

    PeakBarEditConfig(String name, String config) {
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
