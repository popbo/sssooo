package com.stdc.visual.dynamic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableUnionDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableUnion;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--15:18
 * @describe:
 */
public interface DatasetTableUnionMapper extends BaseMapper<DatasetTableUnion> {

    List<DataSetTableUnionDTO> selectBySourceTableId(String tableId);

    List<DataSetTableUnionDTO> selectByTargetTableId(String tableId);

    List<DataSetTableUnionDTO> selectUsedFieldBySource(DatasetTableUnion datasetTableUnion);

    List<DataSetTableUnionDTO> selectUsedFieldByTarget(DatasetTableUnion datasetTableUnion);

}
