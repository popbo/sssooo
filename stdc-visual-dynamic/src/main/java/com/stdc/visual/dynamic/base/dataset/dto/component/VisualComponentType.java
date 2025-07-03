package com.stdc.visual.dynamic.base.dataset.dto.component;

/**
 * @author: wang_jie
 * @data: 2022/5/23--15:55
 * @describe: 大屏组件分类
 */
public interface VisualComponentType {

    /**
     * 柱状图
     */
    String BAR_CHART = "bar";

    /**
     * 斑马柱状图
     */
    String newPictorialBar = "newPictorialBar";

    /**
     * 折线图
     */
    String LINE_CHART = "line";

    /**
     * 柱状折线图
     */
    String LINE_BAR = "line-bar";

    /**
     * 预测面积图
     */
    String BASIC_AREA = "basicarea";

    /**
     * 立体柱状图
     */
    String STEREOSCOPIC_BAR = "stereoscopicBar";

    /**
     * 山峰柱状图
     */
    String PEAK_BAR = "PeakBar";

    /***
     * 饼状图
     */
    String PIE_CHART = "pie";

    /**
     * 3D饼状图
     */
    String STEREOSCOPIC_PIE = "stereoscopicPie";

    /**
     * 象形图
     */
    String PICTOGRAM = "pictorialbar";

    /***
     * 选项卡
     */
    String TABS = "tabs";

    /**
     * 滚动排行
     */
    String SCROLL_RANKING = "scrollRanking";

    /**
     * tabs
     */
    String UNIVERSAL_TABS = "universalTabs";

    /**
     * 斑马进度条
     */
    String ZEBRA_PROGRESS_BAR = "ZebraProgressBar";

    /***
     * 雷达图
     */
    String RADAR_CHART = "radar";

    /***
     * 散点图
     */
    String SCATTER_CHART = "scatter";

    /**
     * 漏斗图
     */
    String FUNNEL_CHART = "funnel";

    /**
     * 轮播图
     */
    String SWIPER  = "swiper";

    /**
     * 下拉框
     */
    String SELECT  = "select";

    /**
     * 文本框
     */
    String TEXT = "text";

    /**
     * 表格
     */
    String TABLE = "table";

    /**
     * 数据源组件
     */
    String DATA_STORAGE = "dataStorage";

    /**
     * 交叉表
     */
    String CROSS_TABLE = "crosstable";

    /**
     * 颜色块
     */
    String COLOR_BLOCK = "colorblock";

    /**
     * 字符云
     */
    String CHAR = "char";

    /**
     * 仪表盘
     */
    String GAUGE = "gauge";


    /**
     * 进度条-环形
     */
    String PROGRESS_VIEW = "progressView";

    /**
     * 进度条-条形
     */
    String PROGRESS_BAR = "progressBar";

    /**
     * iframe标签
     */
    String IFRAME = "iframe";

    /**
     * button按钮
     */
    String BUTTON = "button";

    /**
     * 多选卡
     */
    String MULTIPLE_TABS = "multipletabs";

    /**
     * 层级树
     */
    String TREE = "tree";

    /**
     * 新 层级树
     */
    String NEW_TREE = "newTree";


    /**
     * 自定义组件
     */
    String COMMON = "common";

    /**
     * 事件排行组件
     */
    String EVENT_RANKING_LIST = "eventRankingList";

}
