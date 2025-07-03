package com.stdc.topology2d.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: feng_qiang
 * @date: 2025/05/08--13:51
 * @describe:
 */
@Data
public class TopologyFileListVo {
    @ApiModelProperty("username")
    private String username;

    @ApiModelProperty("页码")
    private Integer current;

    @ApiModelProperty("每页数量")
    private Integer pageSize;

    @ApiModelProperty("文件夹")
    private String directory;

    @ApiModelProperty("类型")
    private String type;

}
