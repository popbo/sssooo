package com.stdc.topology2d.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author: feng_qiang
 * @date: 2025/04/30--11:03
 * @describe:
 */
@Data
public class TopoloyDateJumpVo  {

    @ApiModelProperty("id")
    private String  id;

    @ApiModelProperty("是否组件")
    private boolean component;

    @ApiModelProperty("文件名字")
    private String headtitle;

    @ApiModelProperty("时间戳")
    private Long r;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("是否编辑")
    private boolean visualizationEdit;

}
