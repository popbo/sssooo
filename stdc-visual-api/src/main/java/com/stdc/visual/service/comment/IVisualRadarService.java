package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.rader.VisualRadar;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

/**
 * @author: wang_jie
 * @data: 2022/5/26--11:40
 * @describe: 雷达图数据格式
 */
public interface IVisualRadarService {

    /**
     * 由于雷达图数据格式使用较少（只有雷达图使用），二次整合版本时，刷新数据使用前一个版本的方法，不进行重构
     * @param dataSetTableRequest
     * @return
     */
    VisualRadar refreshByRadar(DataSetTableRequest dataSetTableRequest);


}
