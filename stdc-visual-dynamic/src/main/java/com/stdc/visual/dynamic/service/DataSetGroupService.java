package com.stdc.visual.dynamic.service;

import com.stdc.visual.dynamic.base.dataset.dto.DataSetGroupDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetGroup;
import com.stdc.visual.dynamic.base.dataset.request.DataSetGroupRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:33
 * @describe: 数据集分组业务类
 */
public interface DataSetGroupService {

    /**
     * 报错数据集分组
     *
     * @param datasetGroup
     * @return
     */
    DataSetGroupDTO save(DatasetGroup datasetGroup);

    /**
     * 删除分组
     *
     * @param id
     * @throws Exception
     */
    void delete(String id) throws Exception;

    /**
     * 查询树
     *
     * @param datasetGroup
     * @return
     */
    List<DataSetGroupDTO> tree(DataSetGroupRequest datasetGroup);

    /**
     * 查询树节点
     *
     * @param datasetGroup
     * @return
     */
    List<DataSetGroupDTO> treeNode(DataSetGroupRequest datasetGroup);

    /**
     * 获取父分组
     *
     * @param id
     * @return
     */
    List<DatasetGroup> getParents(String id);

    /**
     * 获取父分组
     *
     * @param list
     * @param datasetGroup
     */
    void getParent(List<DatasetGroup> list, DatasetGroup datasetGroup);

    DatasetGroup getScene(String id);
}
