package com.stdc.visual.dynamic.service;

import com.stdc.visual.dynamic.base.dataset.dto.component.bar.VisualBar;
import com.stdc.visual.dynamic.base.dataset.dto.component.event.VisualEvent;
import com.stdc.visual.dynamic.base.dataset.dto.component.keyvalue.VisualKeyValue;
import com.stdc.visual.dynamic.base.dataset.dto.component.scatter.VisualScatter;
import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;
import com.stdc.visual.dynamic.base.dataset.dto.component.table.VisualTable;
import com.stdc.visual.dynamic.base.dataset.dto.component.tree.VisualTree;
import com.stdc.visual.dynamic.base.dataset.dto.component.value.VisualValue;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.List;
import java.util.Set;

/**
 * @author: wang_jie
 * @data: 2022/5/26--11:04
 * @describe: 组件刷新接口
 */
public interface ComponentService {

    /**
     * key-value数据格式组件刷新数据 ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualKeyValue> freshDataForKeyValue(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * visualBar 数据格式组件刷新数据 ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    VisualBar freshDataForBar(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * VisualValue 数据格式组件刷新数据 [度量-必选] ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualValue> freshDataForValueByMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * VisualValue 数据格式组件刷新数据 [维度-必选] ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualValue> freshDataForValueByDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * VisualValue 数据格式组件刷新数据 [维度,度量-必选] ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualValue> freshDataForValueByMeasureFieldAndDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 表格 数据格式组件刷新数据 ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    VisualTable refreshByTable(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 表格组件专属刷新数据添加表头 两层表头 ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    VisualTable refreshByTableWithTableHead(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * VisualSelect 数据格式刷新数据  只有维度
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualSelect> refreshSelectWithDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * VisualSelect 数据格式刷新数据     只有度量
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualSelect> refreshSelectWithDimensionFieldAndMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 事件排行组件数据刷新 ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualEvent> refreshByEvent(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 事件排行组件数据刷新 ☆
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualScatter> refreshByScatter(DataSetTableRequest dataSetTableRequest) throws Exception;


}
