package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/30--15:07
 * @describe: 工程化编辑 - 斑马柱状图 - 复用配置 -配置类
 */
public enum  NewPictorialBarEditConfig implements VisualEdtConfigInterface{

    AXIS_MARGIN_SETTINGS("坐标轴边距","gridX,gridY,gridX2,gridY2"),

    DIMENSION_DISPLAY_POSITION_SETTINGS("维度显示位置","category"),

    ICON_TYPE_SETTINGS("图标类型","geometricOrImage,symbol"),

    ICON_HEIGHT_WIDTH_TYPE_SETTINGS("图标高宽度","symbolSize"),

    ICON_SPACE_SETTINGS("图标间距","symbolSplit"),

    BAT_SPLIT_SETTINGS("柱间距","barSplit"),

    X_AXIS_SETTINGS("X轴设置","xAxisShow,xAxisLabelFontSize,xAxisLineShow,xAxisSplitLineShow,xAxisSplitLineType,xAxisSplitLineWidth,xAxisSplitLineColor"),

    Y_AXIS_SETTINGS("Y轴设置","yAxisShow,yAxisLabelFontSize,yAxisLineShow,yAxisSplitLineShow,yAxisSplitLineType,yAxisSplitLineWidth,yAxisSplitLineColor"),

    NUMBER_SETTINGS("数值设置","labelShow,labelShowFontSize,labelShowColor,labelShowFontWeight"),

    LEGEND_SETTINGS("图例操作","legend,legendPostion,legendOrient,legendWidth,legendHeight,legendFontSize"),

    CUSTOM_COLOR_SETTING("自定义配色","nameColor,lineColor,barColor"),
    ;

    private String name;

    private String config;

    NewPictorialBarEditConfig(String name, String config) {
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
