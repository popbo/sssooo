package com.stdc.visual.common.utils;

import com.stdc.core.tool.utils.StringPool;

/**
 * @author: wang_jie
 * @data: 2023/6/27--14:22
 * @describe: 可视化项目前缀常量
 */
public interface StdcVisualConstant {

    /**
     * 可视化项目前缀,  stdc_visual_
     */
    String PROJECT_PREFIX = "stdc_visual" + StringPool.UNDERSCORE;

    /**
     * 可视化项目OSS目录前缀,oss  stdc_visual/
     */
    String OSS_PREFIX_BUCKET = "stdc-visual";

    /**
     * 组态项目OSS目录前缀,oss  stdc-topology/
     */
    //String OSS_TOPOLOGY_PREFIX_BUCKET = "stdc-topology";
    String OSS_TOPOLOGY_PREFIX_BUCKET = "stdc-meta";

}
