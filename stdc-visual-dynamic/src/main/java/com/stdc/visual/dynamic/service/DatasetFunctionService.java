package com.stdc.visual.dynamic.service;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTableFunction;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/20--9:43
 * @describe: 数据集方法
 */
public interface DatasetFunctionService {

    /**
     * 通过id获取
     * @param id
     * @return
     */
    DatasetTableFunction get(Long id);

    /**
     * 查询列表
     * @param datasetTableFunction
     * @return
     */
    List<DatasetTableFunction> list(DatasetTableFunction datasetTableFunction);

    /**
     * 查询列表
     * @param id
     * @return
     */
    List<DatasetTableFunction> listByTableId(String id);

}
