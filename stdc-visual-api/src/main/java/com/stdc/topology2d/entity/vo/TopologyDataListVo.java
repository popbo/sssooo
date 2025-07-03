package com.stdc.topology2d.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author: feng_qiang
 * @date: 2025/04/14--15:07
 * @describe:
 */
@Data
public class TopologyDataListVo {

    @ApiModelProperty("所属分类topo2d 图纸 topo2d-conpoment")
    private  String collection;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("大屏/组件名字")
    private String name;

    @ApiModelProperty("当前页码")
    private Integer current;

    @ApiModelProperty("每页数量")
    private Integer pageSize;

    @ApiModelProperty("分类值")
    private String categoryValue;

}
