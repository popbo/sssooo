package com.stdc.visual.service.comment.impl;

import com.stdc.visual.dynamic.base.dataset.dto.component.value.VisualValue;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.ComponentService;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.service.comment.IVisualValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author: wang_jie
 * @data: 2022/5/26--10:53
 * @describe:
 */
@Service
public class VisualValueServiceImpl implements IVisualValueService {

    @Autowired
    private ComponentService componentService;

    /**
     * @param dataSetTableRequest
     * @return 单条数据-结果集第一条 [度量-必选]
     * @throws Exception
     */
    @Override
    public VisualValue refreshForOneByMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception {
        return refreshForListByMeasureField(dataSetTableRequest).stream().findFirst().isPresent() ? refreshForListByMeasureField(dataSetTableRequest).stream().findFirst().get() : null;
    }

    /**
     * @param dataSetTableRequest
     * @return 多条数据-结果集 [度量-必选]
     * @throws Exception
     */
    @Override
    public List<VisualValue> refreshForListByMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualValue> visualValues = componentService.freshDataForValueByMeasureField(dataSetTableRequest);
        return visualValues;
    }

    /**
     * @param dataSetTableRequest
     * @return 单条数据-结果集第一条 [度量-必选]
     * @throws Exception
     */
    @Override
    public VisualValue refreshForOneByDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception {
        Optional<VisualValue> first = refreshForListByDimensionField(dataSetTableRequest).stream().findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }

    /**
     * @param dataSetTableRequest
     * @return 多条数据-结果集 [维度-必选]
     * @throws Exception
     */
    @Override
    public List<VisualValue> refreshForListByDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualValue> visualValues = componentService.freshDataForValueByDimensionField(dataSetTableRequest);
        return visualValues;
    }

    @Override
    public VisualValue refreshForOneByMeasureFieldAndDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception {
        Optional<VisualValue> first = refreshForListByMeasureFieldAndDimensionField(dataSetTableRequest).stream().findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }

    @Override
    public List<VisualValue> refreshForListByMeasureFieldAndDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualValue> visualValues = componentService.freshDataForValueByMeasureFieldAndDimensionField(dataSetTableRequest);
        return visualValues;
    }
}
