package com.stdc.visual.dynamic.service;

import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableUnionDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableUnion;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--15:16
 * @describe: 表关联关系
 */
public interface DataSetTableUnionService {

    /**
     * 报存表关联关系
     *
     * @param datasetTableUnion
     * @return
     */
    DatasetTableUnion save(DatasetTableUnion datasetTableUnion);

    /**
     * 删除表关联关系
     *
     * @param id
     */
    void delete(String id);

    /**
     * 通过表id获取表关联关系
     *
     * @param tableId
     * @return
     */
    List<DataSetTableUnionDTO> listByTableId(String tableId);

    /**
     * 通过表id删除表关联关系
     */
    void deleteUnionByTableId(String tableId);


}
