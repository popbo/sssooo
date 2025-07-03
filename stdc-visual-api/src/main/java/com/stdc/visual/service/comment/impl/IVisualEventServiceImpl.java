package com.stdc.visual.service.comment.impl;

import com.stdc.visual.dynamic.base.dataset.dto.component.event.VisualEvent;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.ComponentService;
import com.stdc.visual.service.comment.IVisualEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/2/16--13:45
 * @describe: 事件排行接口数据刷新
 */
@Service
public class IVisualEventServiceImpl implements IVisualEventService {

    @Autowired
    private ComponentService componentService;

    @Override
    public List<VisualEvent> refresh(DataSetTableRequest dataSetTableRequest) throws Exception {
        return componentService.refreshByEvent(dataSetTableRequest);
    }
}
