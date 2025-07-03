package com.stdc.visual.entity.edit.po.config;

import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/19--10:21
 * @describe: 工程化编辑 - 折线图 - 复用配置 -配置类
 */
public enum  LineEditConfig implements VisualEdtConfigInterface{

    EDIT_TITLE("标题设置","titleShow,title,titleColor,titleFontSize,titlePostion,subtext,subTitleColor,subTitleFontSize"),

    COLUMN_SETTINGS("柱体设置","barWidth,barRadius,barGap,barMinHeight,TOPN,Ns,isStack"),

    X_AXIS_SETTINGS("X轴设置","xAxisShow,xAxisName,xAxisNameSize,xAxisSplitLineShow,xAxisSplitLineType,xAxisSplitLineTypeColor,xAxisinterval,xAxisRotate,xAxisInverse,xNameFontSize"),

    Y_AXIS_SETTINGS("Y轴设置","yAxisName,yAxisNameSize,yAxisShow,yAxisSplitLineShow,yAxisSplitLineType,yAxisSplitLineShow,yAxisSplitLineTypeColor,yAxisInverse,yNameFontSize"),

    NUMBER_SETTINGS("数值设置","labelShow,labelShowFontSize,labelShowColor,labelShowFontWeight,xLableRotate"),

    REMINDER_SETTINGS("提示语设置","tipFontSize,tipColor"),

    AXIS_MARGIN_SETTINGS("坐标轴边距","gridX,gridY,gridX2,gridY2"),

    LEGEND_SETTINGS("图例操作","legend,legendPostion,legendOrient,legendWidth,legendHeight,legendFontSize,legendColor"),

    CUSTOM_COLOR_SETTING("自定义配色","nameColor,lineColor,barColor"),

    VERTICAL_DISPLAY_SETTING("竖展示","category"),

    LINE_SETTING("折线设置","smooth,areaStyle,showSymbol,lineWidth,symbolSize"),

    ;


    private String name;

    private String config;

    LineEditConfig(String name, String config) {
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
