package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.event.VisualEvent;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/2/16--13:41
 * @describe: 事件排行接口数据刷新
 */
public interface IVisualEventService {

    /**
     * 数据刷新
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    List<VisualEvent> refresh(DataSetTableRequest dataSetTableRequest) throws Exception;

}
