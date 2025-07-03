package com.stdc.visual.service.comment.impl;

import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.ComponentService;
import com.stdc.visual.service.comment.IVisualSelectService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/12/13--10:09
 * @describe: 下拉框 选项卡 数据格式
 */
@Service
public class IVisualSelectServiceImpl implements IVisualSelectService {

    @Autowired
    private ComponentService componentService;

    @Override
    public List<VisualSelect> refreshWithDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception {
        return componentService.refreshSelectWithDimensionField(dataSetTableRequest);
    }

    @Override
    public List<VisualSelect> refreshWithDimensionFieldAndMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception {
        return componentService.refreshSelectWithDimensionFieldAndMeasureField(dataSetTableRequest);
    }

    @Override
    public VisualSelect refreshByOne(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualSelect> refresh = refreshWithDimensionFieldAndMeasureField(dataSetTableRequest);
        if (CollectionUtils.isEmpty(refresh)){
            return null;
        }
        return refresh.get(0);
    }
}
