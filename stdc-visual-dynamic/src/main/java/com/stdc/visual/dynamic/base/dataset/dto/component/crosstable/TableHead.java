package com.stdc.visual.dynamic.base.dataset.dto.component.crosstable;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * @author: wang_jie
 * @data: 2022/5/26--16:38
 * @describe: 表头
 */
@Data
@Accessors(chain = true)
public class TableHead implements Serializable {


    /**
     * 表头id  dataeaseName
     */
    private String id;

    /**
     * 父表头id  父dataeaseName
     */
    private String parentId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * value
     */
    private String value;

    /**
     * 子表头
     */
    private LinkedHashSet<TableHead> child = new LinkedHashSet<>();

    /**
     * 数据库字段名
     */
    private String originName;

    /**
     * 筛选字段
     */
    private String filter;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

