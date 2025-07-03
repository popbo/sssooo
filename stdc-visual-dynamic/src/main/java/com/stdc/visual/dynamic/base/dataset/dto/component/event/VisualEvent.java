package com.stdc.visual.dynamic.base.dataset.dto.component.event;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: wang_jie
 * @data: 2023/2/16--13:42
 * @describe: 事件排行组件数据结构
 */
@Data
@Accessors(chain = true)
public class VisualEvent {

    /**
     * 标题
     */
    private String title;

    /**
     * 详情
     */
    private String detail;

    /**
     * 事件
     */
    private String time;
}
