package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.tree.VisualTree;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.Set;

/**
 * @author: wang_jie
 * @data: 2022/5/26--16:56
 * @describe: 层级树
 */
public interface IVisualTreeService {

    /**
     * 层级树刷新
     * @param dataSetTableRequest
     * @return
     */
    Set<VisualTree>  refreshByTree(DataSetTableRequest dataSetTableRequest) throws Exception;
}
