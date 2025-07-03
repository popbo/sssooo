package com.stdc.visual.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2023/1/3--13:49
 * @describe: 手势密码实体类
 */
@Data
@TableName("gesture_password")
public class GesturePassword {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "大屏id")
    private Long visualId;

    @ApiModelProperty(value = "手势密码数组")
    private String gtPsw;

    @ApiModelProperty(value = "验证密码")
    private String vPsw;

    @TableLogic
    @ApiModelProperty(value = "是否已删除 [0:未删除,1:删除]")
    private Integer isDeleted;

    @ApiModelProperty(value = "更新时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

}
