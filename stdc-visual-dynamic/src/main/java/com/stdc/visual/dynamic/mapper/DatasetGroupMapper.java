package com.stdc.visual.dynamic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetGroupDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetGroup;
import com.stdc.visual.dynamic.base.dataset.request.DataSetGroupRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:34
 * @describe:
 */
public interface DatasetGroupMapper extends BaseMapper<DatasetGroup> {

    List<DataSetGroupDTO> search(DataSetGroupRequest ChartGroup);

}
