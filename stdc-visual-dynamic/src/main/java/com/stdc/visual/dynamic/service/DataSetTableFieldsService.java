package com.stdc.visual.dynamic.service;

import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--14:35
 * @describe: 数据集字段编辑
 */
public interface DataSetTableFieldsService {

    /**
     * 批量编辑
     *
     * @param list
     */
    void batchEdit(List<DatasetTableField> list);

    /**
     * 保存
     *
     * @param datasetTableField
     * @return
     */
    DatasetTableField save(DatasetTableField datasetTableField);

    /**
     * 分组查询表下属字段 默认不需要主键
     *
     * @param datasetTableField
     * @return
     */
    List<DatasetTableField> list(DatasetTableField datasetTableField);

    /**
     * 分组查询表下属字段 是否需要主键
     *
     * @param datasetTableField
     * @return
     */
    List<DatasetTableField> list(DatasetTableField datasetTableField, Boolean primaryKey);

    /**
     * 通过id删除表字段
     *
     * @param tableId
     */
    void deleteByTableId(String tableId);

    /**
     * 通过id获取表字段
     *
     * @param ids
     * @return
     */
    List<DatasetTableField> getListByIds(List<String> ids);

    /**
     * 通过id获取表字段
     *
     * @param id
     * @return
     */
    DatasetTableField getOneById(String id);

    /**
     * 通过id获取表字段
     *
     * @param id
     * @return
     */
    DatasetTableField selectByPrimaryKey(String id);

    /**
     * 通过id获取表字段
     *
     * @param id
     * @return
     */
    DatasetTableField get(String id);

    /**
     * 通过id获取表字段
     *
     * @param ids
     * @return
     */
    List<DatasetTableField> getListByIdsEach(List<String> ids);

    /**
     * 通过表字段id
     *
     * @param id
     * @return
     */
    List<DatasetTableField> getFieldsByTableId(String id);

    /**
     * 删除字段
     *
     * @param id
     */
    void delete(String id);

    /*
     * 检验数据集字段名称
     */
    void checkFieldName(DatasetTableField datasetTableField);

}
