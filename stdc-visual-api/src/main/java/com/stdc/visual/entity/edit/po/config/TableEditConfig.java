package com.stdc.visual.entity.edit.po.config;

/**
 * @author: wang_jie
 * @data: 2023/6/5--16:42
 * @describe:
 */
public enum  TableEditConfig implements VisualEdtConfigInterface {

    TABLE_HEAD_SETTINGS("表头设置","showHeader,headerBorder,headerBorderSize,headerBorderColor,headerHeight,headerFontSize,headerFontWeight,headerBackground,headerColor,headerTextAlign"),

    TABLE_HEAD_PAGING_CONFIG("表头分页设置","showPagination,paginationPosition,paginationType,paginationFontSize,paginationColor,paginationScolor,paginationIconcolor,paginationIconBcolor,paginationBcolor,paginationXbcolor,paginationBorder,paginationBorderColor,paginationMargin,paginationSelectFonit,paginationSelectColor,paginationSelectBcolor,paginationSelectScolor,paginationSelectHcolor"),
    ;


    private String name;

    private String config;

    TableEditConfig(String name, String config) {
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
