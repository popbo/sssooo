package com.stdc.visual.dynamic.service;

import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.dynamic.base.datasource.dto.DBTableDTO;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--9:33
 * @describe: 数据源业务类
 */
public interface DatasourceService {

    /**
     * 新增数据源
     *
     * @param datasource
     * @return
     */
    Datasource addDatasource(Datasource datasource);

    /**
     * 获取所有数据源列表
     *
     * @return
     */
    List<Datasource> getDatasourceList();

    /**
     * 删除数据源
     *
     * @param datasourceId
     */
    R deleteDatasource(String datasourceId);

    /**
     * 更新数据源
     *
     * @param datasource
     */
    void updateDatasource(Datasource datasource);

    /**
     * 检验数据源
     *
     * @param datasource
     * @return
     */
    R validate(Datasource datasource);

    /**
     * 检验数据源
     *
     * @param datasourceId
     * @return
     */
    R validate(String datasourceId);

    /**
     * 查询数据源下属所有表
     *
     * @param datasource
     * @return
     */
    List<DBTableDTO> getTables(Datasource datasource) throws Exception;

    /**
     * 获取datasource
     * @param id
     * @return
     */
    Datasource get(String id);

    /**
     * 获取Schema
     * @param datasource
     * @return
     */
    List<String> getSchema(Datasource datasource);
}
