package com.stdc.visual.dynamic.base.dataset.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * @author: wang_jie
 * @data: 2022/5/25--11:36
 * @describe: 封装 mysql查询需展示的数据
 */
@Data
@Builder
public class DataView implements Serializable {

    // DataSetField 数据集字段id
    private String id;

    // DataSetField 数据集字段id
    private String dataeaseName;

    // value
    private String value;

    public String get(){
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

}
