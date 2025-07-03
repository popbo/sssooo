package com.stdc.visual.dynamic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--10:13
 * @describe:
 */
public interface DatasetTableMapper extends BaseMapper<DatasetTable> {

    List<DataSetTableDTO> search(DataSetTableRequest request);

    DataSetTableDTO searchOne(DataSetTableRequest request);

}
