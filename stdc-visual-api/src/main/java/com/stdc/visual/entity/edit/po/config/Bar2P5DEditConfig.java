package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/19--14:31
 * @describe: 工程化编辑 - 2.5D柱状图 - 复用配置 -配置类
 */
public enum Bar2P5DEditConfig implements VisualEdtConfigInterface{

    EDIT_TITLE("标题设置","titleShow,title,titleColor,titleFontSize,titlePostion,subtext,subTitleColor,subTitleFontSize"),

    COLUMN_SETTINGS("柱体设置","barWidth,barRadius,barGap,barMinHeight,TOPN,Ns,isStack"),

    X_AXIS_SETTINGS("X轴设置","xAxisShow,xAxisName,xAxisNameSize,xAxisSplitLineShow,xAxisSplitLineType,xAxisSplitLineTypeColor,xAxisinterval,xAxisRotate,xAxisInverse,xNameFontSize"),

    Y_AXIS_SETTINGS("Y轴设置","yAxisName,yAxisNameSize,yAxisShow,yAxisSplitLineShow,yAxisSplitLineType,yAxisSplitLineShow,yAxisSplitLineTypeColor,yAxisInverse,yNameFontSize"),

    NUMBER_SETTINGS("数值设置","labelShow,labelShowFontSize,labelShowColor,labelShowFontWeight,xLableRotate"),

    REMINDER_SETTINGS("提示语设置","tipFontSize,tipColor"),

    AXIS_MARGIN_SETTINGS("坐标轴边距","gridX,gridY,gridX2,gridY2"),

    LEGEND_SETTINGS("图例操作","legend,legendPostion,legendOrient,legendWidth,legendHeight,legendFontSize,legendColor"),

    CUSTOM_COLOR_SETTING("自定义配色","nameColor,lineColor,barColor"),
    ;




    private String name;

    private String config;

    Bar2P5DEditConfig(String name, String config) {
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
