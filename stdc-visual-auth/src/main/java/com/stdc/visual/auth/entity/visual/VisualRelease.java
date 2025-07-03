package com.stdc.visual.auth.entity.visual;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/10/18--18:33
 * @describe: 大屏发布实体类
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "v_release")
@ApiModel(value = "VisualRelease对象", description = "大屏发布实体类")
public class VisualRelease  implements Serializable {

    /**
     * 可视化表主键
     * 主键
     */
    @TableId
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 发布地址
     */
    @ApiModelProperty("发布地址")
    private String path;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String psw;

    /**
     * 是否加密 1 加密 0 不加密
     */
    @ApiModelProperty("是否加密 1 加密 0 不加密 默认不加密")
    private Integer isEnc = 0;

    /**
     * 是否发布 1 发布 0 未发布
     */
    @ApiModelProperty("是否发布 1 发布 0 未发布")
    private Integer isRelease = 0;

    /**
     * 是否自定义 0不是自定义 1 自定义
     */
    @ApiModelProperty("是否自定义 0不是自定义 1 自定义")
    private Integer isCustom = 0;

    /**
     * 配置id
     */
    @ApiModelProperty("配置id")
    private Long configId;

    /**
     * 大屏config配置
     */
    @ApiModelProperty("大屏config配置")
    private String detail;

    /**
     * 大屏组件配置
     */
    @ApiModelProperty("大屏组件配置")
    private String component;

    /**
     * 发布者用户名
     */
    @ApiModelProperty("发布者用户名")
    private String username;

    /**
     * 版本
     */
    @TableField(exist = false)
    private String version;

    /**
     * 发布者用户名
     */
    @ApiModelProperty("发布类型 0可视化 1组态")
    private Integer source=0;

}
