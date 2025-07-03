package com.stdc.visual.dynamic.base.chart.dto;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:24
 * @describe: 自定义条件
 */
@Data
public class ChartFieldCustomFilterDTO extends ChartViewFieldBaseDTO implements Serializable {
    /***
     * 条件名称
     */
    private String conditionName;
    /***
     * 条件id
     */
    private String conditionId;
    /***
     * 条件类型-字段-自定义
     */
    private String conditionType;

    /***
     * 是否编辑
     */
    private String isEdit;

    /***
     * where子句条件
     */
    private List<ChartCustomFilterItemDTO> filter;

    /**
     * 字段详情
     */
    private DatasetTableField field;

    /**
     * in 条件语句 参数
     */
    private List<String> enumCheckField;

    /***
     * 上一级查询请求
     */
    private DataSetTableRequest SuperRequest;

}
