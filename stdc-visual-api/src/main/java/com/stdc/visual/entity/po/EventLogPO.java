package com.stdc.visual.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2024/1/4--15:24
 * @describe:
 */
@Data
@NoArgsConstructor
@TableName(StdcVisualConstant.PROJECT_PREFIX + "event_log")
@ApiModel(value = "EventLogPO对象", description = "组件日志表")
public class EventLogPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 大屏id
     */
    @ApiModelProperty(value = "可视化表主键")
    private Long visualId;

    /**
     * 大屏名称
     */
    @TableField(exist = false)
    private String visualName;

    /**
     * 大屏页面id
     */
    @ApiModelProperty("大屏id")
    private Long configId;

    /**
     * 组件id
     */
    @ApiModelProperty("组件id")
    private String componentId;

    /**
     * 事件id
     */
    @ApiModelProperty(value = "事件id")
    private String eventId;

    /**
     * 日志详情
     */
    @ApiModelProperty(value = "日志详情")
    private String detail;

    /**
     * 创建时间(字符串)
     */
    @ApiModelProperty(value = "创建时间(字符串)")
    private String createTime;

    /**
     * 状态[0:未删除,1:删除]
     */
    @TableLogic
    @ApiModelProperty(value = "是否已删除")
    private Integer isDeleted = 0;

}
