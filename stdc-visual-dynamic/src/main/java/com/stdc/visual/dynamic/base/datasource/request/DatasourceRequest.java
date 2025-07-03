package com.stdc.visual.dynamic.base.datasource.request;

import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/5/16--19:57
 * @describe: 数据源请求
 */
@Data
public class DatasourceRequest {
    protected String query;
    protected String table;
    protected Datasource datasource;
    private Integer pageSize;
    private Integer page;
    private Integer realSize;
    private Integer fetchSize = 10000;
    private boolean pageable = false;
    private boolean previewData = false;
}