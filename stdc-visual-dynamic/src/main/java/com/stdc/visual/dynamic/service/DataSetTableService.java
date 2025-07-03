package com.stdc.visual.dynamic.service;

import com.stdc.visual.dynamic.base.dataset.dto.DataSetDetail;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableDTO;
import com.stdc.visual.dynamic.base.dataset.dto.component.bar.VisualBar;
import com.stdc.visual.dynamic.base.dataset.dto.component.keyvalue.VisualKeyValue;
import com.stdc.visual.dynamic.base.dataset.dto.component.value.VisualValue;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetEnumRequest;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.base.datasource.dto.TableFiled;

import java.util.List;
import java.util.Map;

/**
 * @author: wang_jie
 * @data: 2022/5/17--14:19
 * @describe: 数据集表服务
 */
public interface DataSetTableService {

    /**
     * 批量保存数据集表
     *
     * @param datasetTable
     * @throws Exception
     */
    void batchInsert(List<DataSetTableRequest> datasetTable) throws Exception;

    /**
     * 新增excel
     *
     * @param datasetTable
     * @throws Exception
     */
    void saveExcel(DataSetTableRequest datasetTable) throws Exception;

    /**
     * 新增
     *
     * @param datasetTable
     * @return
     * @throws Exception
     */
    DatasetTable save(DataSetTableRequest datasetTable) throws Exception;

    /**
     * 修改
     *
     * @param request
     * @throws Exception
     */
    void alter(DataSetTableRequest request) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @throws Exception
     */
    void delete(String id);

    /**
     * 查询
     *
     * @param dataSetTableRequest
     * @return
     */
    List<DataSetTableDTO> list(DataSetTableRequest dataSetTableRequest);

    /**
     * 查询
     *
     * @param datasetIds
     * @return
     */
    List<DatasetTable> list(List<String> datasetIds);

    /**
     * 查询组
     *
     * @param dataSetTableRequest
     * @return
     */
    List<DataSetTableDTO> listAndGroup(DataSetTableRequest dataSetTableRequest);

    /**
     * 搜索
     *
     * @param dataSetTableRequest
     * @return
     */
    List<DataSetTableDTO> search(DataSetTableRequest dataSetTableRequest);

    /**
     * 通过id获取
     *
     * @param id
     * @return
     */
    DatasetTable get(String id);

    /**
     * 查询原始字段
     *
     * @param datasetTable
     * @return
     * @throws Exception
     */
    List<TableFiled> getFields(DatasetTable datasetTable) throws Exception;

    /**
     * 查询生成字段
     *
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    Map<String, List<DatasetTableField>> getFieldsFromDE(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 查询预览数据 ☆
     *
     * @param dataSetTableRequest
     * @param page
     * @param pageSize
     * @param extFields
     * @return
     * @throws Exception
     */
    Map<String, Object> getPreviewData(DataSetTableRequest dataSetTableRequest, Integer page, Integer pageSize,
                                       List<DatasetTableField> extFields) throws Exception;


    /**
     * 根据sql查询预览数据
     *
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    Map<String, Object> getSQLPreview(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 查询字段枚举值
     * @return
     * @throws Exception
     */
    List<String>  getEnumPreview( DataSetEnumRequest request) throws Exception;

    /**
     * 关联数据集预览数据
     *
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    Map<String, Object> getUnionPreview(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 客户预览数据
     *
     * @param dataSetTableRequest
     * @return
     * @throws Exception
     */
    Map<String, Object> getCustomPreview(DataSetTableRequest dataSetTableRequest) throws Exception;

    /**
     * 数据库同步表结构
     *
     * @param id
     * @return
     * @throws Exception
     */
    DatasetTable syncDatasetTableField(String id) throws Exception;

    /**
     * 数据集详情
     * @param id
     * @return
     */
    DataSetDetail getDatasetDetail(String id);
}
