package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.bar.VisualBar;
import com.stdc.visual.dynamic.base.dataset.dto.component.bar.VisualLineBar;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

/**
 * @author: wang_jie
 * @data: 2022/5/25--15:37
 * @describe: 柱状图数据格式、折线图数据格式、象形图数据格式
 */
public interface IVisualBarService {
    /**
     * 组件刷新
     * @param dataSetTableRequest
     * @return
     */
    VisualBar refresh(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 折线柱状图、预测面积图
     * @return
     */
    VisualLineBar refreshLineBar(DataSetTableRequest dataSetTableRequest) throws Exception;

}
