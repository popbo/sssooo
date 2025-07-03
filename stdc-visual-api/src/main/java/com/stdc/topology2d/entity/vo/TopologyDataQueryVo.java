package com.stdc.topology2d.entity.vo;

import com.stdc.topology2d.entity.po.TopologyData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class TopologyDataQueryVo extends TopologyData {

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

    @ApiModelProperty("排序 1按创建时间 2按修改时间")
    private String sortFlag;
}
