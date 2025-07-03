package com.stdc.visual.entity.request;

import com.stdc.visual.entity.po.Visual;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/11/3--10:04
 * @describe: 大屏查询请求实体类
 */
@Data
@ApiModel(value = "Visual对象", description = "大屏查询请求实体类")
public class VisualRequest extends Visual {

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段 create_time 创建时间 update_time 更新时间 ")
    private String order;

}
