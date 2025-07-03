package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.scatter.VisualScatter;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/24--16:09
 * @describe: 散点图刷新
 */
public interface IVisualScatterService {

    List<VisualScatter> refresh(DataSetTableRequest dataSetTableRequest) throws Exception;



}
