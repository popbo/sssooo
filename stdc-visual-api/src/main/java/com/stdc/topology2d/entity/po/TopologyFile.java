package com.stdc.topology2d.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.topology2d.constants.ModelConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: feng_qiang
 * @date: 2025/05/08--15:27
 * @describe:
 */
@Data
@TableName(ModelConstants.TOPOLOGY_FILE_TABLE_NAME)
@ApiModel(value = "文件信息表", description = "文件信息表")
public class TopologyFile {

    @TableId(value = ModelConstants.ID_PROPERTY, type = IdType.INPUT)
    @ApiModelProperty(value = "id", required = true)
    private String id;
    @TableField(value = ModelConstants.TOPOLOGY_FILE_FILENAME)
    @ApiModelProperty(value = "完整文件名", required = true)
    private String filename;

    @TableField(ModelConstants.TOPOLOGY_FILE_URL)
    @ApiModelProperty(value = "文件连接", required = true)
    private String url;

    @TableField(ModelConstants.TOPOLOGY_FILE_LENGTH)
    @ApiModelProperty(value = "文件长度")
    private Long length;

    @TableField(ModelConstants.TOPOLOGY_FILE_NAME)
    @ApiModelProperty(value = "文件名", required = true)
    private String name;

    @TableField(ModelConstants.TOPOLOGY_FILE_TAGS)
    @ApiModelProperty(value = "文件标签")
    private String tags;

    @TableField(ModelConstants.TOPOLOGY_FILE_SHARED)
    @ApiModelProperty(value = "是否分享")
    private Boolean shared;

    @TableField(ModelConstants.TOPOLOGY_FILE_DIRECTORY)
    @ApiModelProperty(value = "文件文件夹")
    private String directory;

    @TableField(ModelConstants.TOPOLOGY_FILE_REMARKS)
    @ApiModelProperty(value = "文件备注")
    private String remarks;

    @TableField(ModelConstants.TOPOLOGY_FILE_TYPE)
    @ApiModelProperty(value = "文件类型")
    private String type;

    @TableField(ModelConstants.TOPOLOGY_FILE_USERID)
    @ApiModelProperty(value = "User ID")
    private String userId;

    @TableField(ModelConstants.TOPOLOGY_FILE_USERNAME)
    @ApiModelProperty(value = "User name", required = true)
    private String username;

    @TableField(ModelConstants.TOPOLOGY_FILE_UPLOAD_DATE)
    @ApiModelProperty(value = "上传时间", required = true)
    private Date uploadDate;
}