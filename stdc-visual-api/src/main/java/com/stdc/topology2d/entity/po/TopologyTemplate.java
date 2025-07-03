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


@Data
@TableName(ModelConstants.TOPOLOGY_TEMPLATE_TABLE_NAME)
@ApiModel(value = "组态模板数据", description = "组态模板数据")
public class TopologyTemplate {

    @TableId(value = ModelConstants.TOPOLOGY_DIR_ID_PROPERTY, type = IdType.ASSIGN_ID)
    private String id;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_DATA_PROPERTY)
    private String data;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_NAME_PROPERTY)
    private String name;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_VERSION_PROPERTY)
    private String version;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_CREATETIME_PROPERTY)
    private Date createTime;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_UPDATETIME_PROPERTY)
    private Date updateTime;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_MODULETYPE_PROPERTY)
    private String templateId;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_FOLDER_PROPERTY)
    private String folder;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_USERNAME_PROPERTY)
    private String username;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_CREATEBY_PROPERTY)
    private String createBy;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_UPDATEBY_PROPERTY)
    private String updateBy;
    @TableField(value = ModelConstants.TOPOLOGY_TEMPLATE_PROJECTID_PROPERTY)
    private String projectId;

    @ApiModelProperty("当前页码")
    @TableField(exist = false)
    private Integer current;
    @ApiModelProperty("每页数量")
    @TableField(exist = false)
    private Integer pageSize;

}
