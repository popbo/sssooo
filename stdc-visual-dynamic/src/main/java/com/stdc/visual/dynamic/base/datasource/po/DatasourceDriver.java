package com.stdc.visual.dynamic.base.datasource.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2023/3/15--19:13
 * @describe:
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "datasource_driver")
@ApiModel(value = "数据库驱动", description = "数据库驱动")
public class DatasourceDriver {

    /**
     * 主键（唯一）
     */
    @TableId
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 驱动类
     */
    @ApiModelProperty(value = "驱动类")
    private String driverClass;

    /**
     * 驱动jar包路径
     */
    @ApiModelProperty(value = "驱动jar包路径")
    private String jarPath;

    /**
     * 驱动jar包名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "驱动jar包名称")
    private String jarFileName;

}
