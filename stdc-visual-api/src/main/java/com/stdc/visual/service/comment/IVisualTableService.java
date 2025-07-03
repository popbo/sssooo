package com.stdc.visual.service.comment;

import com.stdc.visual.dynamic.base.dataset.dto.component.table.VisualTable;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

/**
 * @author: wang_jie
 * @data: 2022/5/26--13:35
 * @describe: 表格数据格式
 */
public interface IVisualTableService {

    /**
     * @param dataSetTableRequest
     * @return 单条数据-结果集第一条
     * @throws Exception
     */
    VisualTable refreshByTable(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * @param dataSetTableRequest
     * @return 单条数据-结果集第一条
     * @throws Exception
     */
    VisualTable refreshByTableWithTableHead(DataSetTableRequest dataSetTableRequest) throws Exception;

}
