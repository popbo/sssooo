package com.stdc.visual.entity.dto;

import com.stdc.visual.entity.po.Visual;
import com.stdc.visual.entity.po.VisualConfig;
import lombok.Data;

import java.io.Serializable;

/**
 * 大屏展示DTO
 *
 * @author wangjie
 */
@Data
public class VisualDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 可视化主信息
     */
    private Visual visual;

    /**
     * 可视化配置信息
     */
    private VisualConfig config;

}
