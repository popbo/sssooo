package com.stdc.visual.dynamic.base.dataset.dto.component.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stdc.core.tool.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author: wang_jie
 * @data: 2022/5/26--16:56
 * @describe: key-label格式 用于：层级树
 */
@Data
public class VisualTree implements Serializable {
    /**
     * id
     */
    @JsonIgnore //前端不返回id
    private String id;
    /**
     * label 展示值
     */
    private String label;
    /**
     * value 值
     */
    private String value;
    /**
     * children
     */
    private Set<VisualTree> children = new LinkedHashSet<>();
    @Override
    public int hashCode(){
        try {
            return id.hashCode() + label.hashCode();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VisualTree)){
            return false;
        }
        VisualTree treeObj = (VisualTree) obj;
        if (StringUtil.equals(treeObj.id,this.id) && StringUtil.equals(treeObj.label,this.label) ){
            return true;
        }else {
            return false;
        }
    }
}