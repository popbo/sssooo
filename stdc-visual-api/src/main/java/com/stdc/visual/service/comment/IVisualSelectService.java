package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/12/13--10:08
 * @describe: 下拉框 选项卡 斑马进度条 数据格式
 */
public interface IVisualSelectService {

    /**
     * VisualSelect 数据格式刷新数据  只有维度
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualSelect> refreshWithDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * VisualSelect 数据格式刷新数据     只有度量
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualSelect> refreshWithDimensionFieldAndMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 斑马进度条刷新数据
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    VisualSelect refreshByOne(DataSetTableRequest dataSetTableRequest) throws Exception;

}
