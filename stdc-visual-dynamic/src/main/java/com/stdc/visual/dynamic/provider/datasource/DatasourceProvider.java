package com.stdc.visual.dynamic.provider.datasource;

import com.stdc.visual.dynamic.base.datasource.dto.TableDesc;
import com.stdc.visual.dynamic.base.datasource.dto.TableFiled;
import com.stdc.visual.dynamic.base.datasource.request.DatasourceRequest;

import java.util.List;
import java.util.Map;

/**
 * @author: wang_jie
 * @data: 2022/5/16--19:18
 * @describe: 数据源生产者抽象
 */
public abstract class DatasourceProvider {

    private int resultLimit = 30000;

    //获取数据
    abstract public List<String[]> getData(DatasourceRequest datasourceRequest) throws Exception;

    //获取当前数据源下表
    abstract public List<TableDesc> getTables(DatasourceRequest datasourceRequest) throws Exception;

    //测试数据集是否可用
    public void checkStatus(DatasourceRequest datasourceRequest) throws Exception {
        getData(datasourceRequest);
    }

    //获取结果
    abstract public List<String[]> fetchResult(DatasourceRequest datasourceRequest) throws Exception;

    abstract public List<TableFiled> fetchResultField(DatasourceRequest datasourceRequest) throws Exception;

    abstract public Map<String, List> fetchResultAndField(DatasourceRequest datasourceRequest) throws Exception;

    abstract public void handleDatasource(DatasourceRequest datasourceRequest, String type) throws Exception;

    abstract public List<String> getSchema(DatasourceRequest datasourceRequest) throws Exception;

    public abstract List<TableFiled> getTableFields(DatasourceRequest datasourceRequest) throws Exception;
}
