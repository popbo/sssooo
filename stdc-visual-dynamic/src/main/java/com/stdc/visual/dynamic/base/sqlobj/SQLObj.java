package com.stdc.visual.dynamic.base.sqlobj;

import lombok.Builder;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:39
 * @describe: sql语句构建对象
 */
@Data
@Builder
public class SQLObj {
    private String tableName;
    private String tableAlias;

    private String fieldName;
    private String fieldAlias;

    private String groupField;
    private String groupAlias;

    private String orderField;
    private String orderAlias;
    private String orderDirection;

    private String whereField;
    private String whereAlias;
    private String whereTermAndValue;

    private String limitFiled;
}
