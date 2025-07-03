package com.stdc.visual.dynamic.base.dataset.dto.component.crosstable;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/26--16:38
 * @describe: 交叉表表格数据
 */
@Data
@Accessors(chain = true)
public class VisualCrossTable {

    /**
     * 总数
     */
    private Integer total = 0;

    /**
     * 数据
     */
    private List<Object> data = new LinkedList<>();

    /**
     * 字段详情
     */
    private List<DatasetTableField> fieldS = new LinkedList<>();

    /**
     * 行表头
     */
    private List<TableHead> rowHeads = new LinkedList<>();

    /**
     * 列表头
     */
    private List<TableHead> colHeads = new LinkedList<>();

    public VisualCrossTable VO(){
        for (TableHead tableHead : this.getRowHeads()) {
            setChildVO(tableHead);
        }
        for (TableHead tableHead : this.getColHeads()) {
            setChildVO(tableHead);
        }
        return this;
    }

    public void setChildVO(TableHead tableHead){
        tableHead.setParentId(null);
        tableHead.setFilter(null);
        tableHead.setOriginName(null);
        if (tableHead.getChild().size() > 0){
            tableHead.getChild().forEach(c->{
                setChildVO(c);
            });
        }
    }
}