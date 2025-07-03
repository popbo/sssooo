package com.stdc.visual.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 可视化表实体类
 *
 * @author wangjie
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "visual")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Visual对象", description = "可视化表")
public class Visual extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 大屏标题
     */
    @ApiModelProperty(value = "大屏标题")
    private String title;
    /**
     * 大屏封面url
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "大屏封面url")
    private String backgroundUrl;
    /**
     * 大屏封面id
     */
    @ApiModelProperty(value = "大屏封面id")
    private String backgroundId;
    /**
     * 大屏封面类型
     */
    @ApiModelProperty(value = "大屏封面类型")
    private String backgroundType;
    /**
     * 大屏类型
     */
    @ApiModelProperty(value = "大屏类型")
    private String category;
    /**
     * 编辑密码
     */
    @ApiModelProperty(value = "编辑密码")
    private String editPassword;

    /**
     * 可视化发布详情
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "可视化发布详情")
    private VisualRelease release;

    @ApiModelProperty(value = "工程id")
    private String projectId;

}
