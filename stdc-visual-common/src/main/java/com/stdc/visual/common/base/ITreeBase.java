package com.stdc.visual.common.base;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:40
 * @describe: 树定义
 */
public interface ITreeBase<T> {

    String getNodeType();

    String getId();

    void setId(String id);

    String getPid();

    void setPid(String pid);

    List<T> getChildren();

    void setChildren(List<T> children);
}
