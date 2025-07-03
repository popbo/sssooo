package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.keyvalue.VisualKeyValue;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/23--16:54
 * @describe: key-value数据格式 数据刷新
 */
public interface IVisualKeyValueService {

    /**
     * 组件刷新
     * @param dataSetTableRequest
     * @return
     */
    List<VisualKeyValue> refresh(DataSetTableRequest dataSetTableRequest) throws Exception;

}
