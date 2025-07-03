package com.stdc.visual.dynamic.base.model.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/19--14:28
 * @describe:
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ModelWithBLOBs extends Model implements Serializable {
    private String name;

    private String label;

    private static final long serialVersionUID = 1L;
}