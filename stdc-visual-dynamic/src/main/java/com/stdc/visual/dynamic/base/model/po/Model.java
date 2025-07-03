package com.stdc.visual.dynamic.base.model.po;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/19--14:28
 * @describe:
 */
@Data
public class Model implements Serializable {
    private String id;

    private String pid;

    private String nodeType;

    private String modelType;

    private String modelInnerType;

    private String authType;

    private String createBy;

    private Long level;

    private Long mode;

    private String dataSourceId;

    private static final long serialVersionUID = 1L;
}