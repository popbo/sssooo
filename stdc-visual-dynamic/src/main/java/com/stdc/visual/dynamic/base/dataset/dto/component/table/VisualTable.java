package com.stdc.visual.dynamic.base.dataset.dto.component.table;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author: wang_jie
 * @data: 2022/5/26--13:34
 * @describe: 表格组件数据
 */
@Data
public class VisualTable {

    /**表格总数*/
    private Integer total;

    /**数据*/
    private List<Map<String,String>> data = new LinkedList<Map<String,String>>();

    /**表头*/
    private List<Column> column = new LinkedList<Column>();

    @Data
    @Builder
    public static class Column{
        /**表头名称*/
        private String label;
        /**表头key*/
        private String prop;
        /**子表头*/
        @JsonInclude(value= JsonInclude.Include.NON_NULL)
        private List<Column> children;
    }

}
