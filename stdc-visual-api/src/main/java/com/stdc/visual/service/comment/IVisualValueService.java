package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.value.VisualValue;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/26--10:52
 * @describe: value数据格式
 */
public interface IVisualValueService {

    /**
     * @param dataSetTableRequest
     * @return 单条数据-结果集第一条 [度量-必选]
     * @throws Exception
     */
    VisualValue refreshForOneByMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * @param dataSetTableRequest
     * @return 多条数据-结果集 [度量-必选]
     * @throws Exception
     */
    List<VisualValue> refreshForListByMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * @param dataSetTableRequest
     * @return 单条数据-结果集第一条 [维度-必选]
     * @throws Exception
     */
    VisualValue refreshForOneByDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * @param dataSetTableRequest
     * @return 多条数据-结果集 [维度-必选]
     * @throws Exception
     */
    List<VisualValue> refreshForListByDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * @param dataSetTableRequest
     * @return 单条数据-结果集第一条 [维度,度量-必选]
     * @throws Exception
     */
    VisualValue refreshForOneByMeasureFieldAndDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * @param dataSetTableRequest
     * @return 多条数据-结果集 [维度,度量-必选]
     * @throws Exception
     */
    List<VisualValue> refreshForListByMeasureFieldAndDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception;

}
