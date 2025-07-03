package com.stdc.visual.dynamic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableFunction;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import com.stdc.visual.dynamic.mapper.DatasetTableFunctionMapper;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.dynamic.service.DatasetFunctionService;
import com.stdc.visual.dynamic.service.DatasourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/20--9:45
 * @describe:
 */
@Service
public class DatasetFunctionServiceImpl implements DatasetFunctionService {

    @Resource
    private DatasetTableFunctionMapper datasetTableFunctionMapper;
    @Resource
    private DataSetTableService dataSetTableService;
    @Resource
    private DatasourceService datasourceService;

    public DatasetTableFunction get(Long id) {
        return datasetTableFunctionMapper.selectById(id);
    }

    public List<DatasetTableFunction> list(DatasetTableFunction datasetTableFunction) {
        QueryWrapper<DatasetTableFunction> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(datasetTableFunction.getDbType())) {
            queryWrapper.eq("db_type",datasetTableFunction.getDbType());
        }
        queryWrapper.orderByAsc("name");
        return datasetTableFunctionMapper.selectList(queryWrapper);
    }

    public List<DatasetTableFunction> listByTableId(String id) {
        DatasetTable datasetTable = dataSetTableService.get(id);
        String dbType;
        if (datasetTable.getMode() == 0) {
            Datasource datasource = datasourceService.get(datasetTable.getDataSourceId());
            dbType = datasource.getType();
        } else {
            dbType = "doris";
        }
        DatasetTableFunction datasetTableFunction = new DatasetTableFunction();
        datasetTableFunction.setDbType(dbType);
        return list(datasetTableFunction);
    }
}
