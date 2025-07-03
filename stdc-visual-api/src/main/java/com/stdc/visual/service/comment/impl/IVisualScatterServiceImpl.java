package com.stdc.visual.service.comment.impl;

import com.stdc.visual.dynamic.base.dataset.dto.component.scatter.VisualScatter;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.ComponentService;
import com.stdc.visual.service.comment.IVisualScatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/24--16:10
 * @describe:
 */
@Service
public class IVisualScatterServiceImpl implements IVisualScatterService {

    @Autowired
    private ComponentService componentService;

    @Override
    public List<VisualScatter> refresh(DataSetTableRequest dataSetTableRequest) throws Exception {
        return componentService.refreshByScatter(dataSetTableRequest);
    }
}
