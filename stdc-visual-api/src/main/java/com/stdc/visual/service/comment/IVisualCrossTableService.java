package com.stdc.visual.service.comment;

import com.alibaba.fastjson.JSONObject;
import com.stdc.visual.dynamic.base.dataset.dto.component.crosstable.VisualCrossTable;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

/**
 * @author: wang_jie
 * @data: 2022/5/26--16:37
 * @describe: 交叉表
 */
public interface IVisualCrossTableService {


    /**
     * 交叉表不重写了 太麻烦了 第一版本的有一个小bug，但是不影响使用
     * 就是 后续应该修改成 排除空数据
     * @param dataSetTableRequest
     * @return
     */
    VisualCrossTable refreshByCrossTable(DataSetTableRequest dataSetTableRequest);

}
