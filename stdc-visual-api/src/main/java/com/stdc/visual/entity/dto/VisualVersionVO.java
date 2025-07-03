package com.stdc.visual.entity.dto;

import com.stdc.visual.entity.po.VisualConfig;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/9/28--10:02
 * @describe: 大屏版本信息
 */
@Data
public class VisualVersionVO implements Serializable {

    /**
     * 大屏id
     */
    private String visualId;

    /**
     * 版本id
     */
    private String versionId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 版本备注
     */
    private String versionRemark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建时间
     */
    private Long updateTime;



}
