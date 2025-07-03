package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/5/30--15:23
 * @describe: 工程化编辑 - 滚动排行 - 复用配置 -配置类
 */
public enum ScrollRankingEditConfig implements VisualEdtConfigInterface {

    AUTOMATIC_ROTATION_SETTINGS("自动轮播","rotation"),

    ROTATION_TIME_SETTINGS("轮播时间","rotationTime"),

    ROTATION_NUMBER("每页显示数量","rotationNumber"),

    AUTOMATIC_MODEL("滚动模式","animationModel"),

    DATA_SETTINGS("数据设置","numericalunit,numericalFsize,numericalColor"),

    THEME_LABEL_SETTINGS("主题标签设置","theme,themeFsize,themeColor,icon,iconSpacing,iconSize,value,iconDirection"),

    PROGRESS_BAR_SETTINGS("进度条设置","progressWidth,progressHeight,progressBackground,gradient,progressProspect,gradientColorOne,gradientColorTwo"),

    ;


    private String name;

    private String config;

    ScrollRankingEditConfig(String name, String config) {
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
