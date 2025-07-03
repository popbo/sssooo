package com.stdc.topology2d.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.topology2d.constants.ModelConstants;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@TableName(ModelConstants.TOPOLOGY_DIR_TABLE_NAME)
@ApiModel(value = "组态文件夹", description = "组态文件夹")
public class TopologyDir {

    @TableId(value = ModelConstants.TOPOLOGY_DIR_ID_PROPERTY, type = IdType.AUTO)
    //@TableField(value = ModelConstants.TOPOLOGY_DIR_ID_PROPERTY)
    private String id;
    @TableField(value = ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY)
    private String type;
    @TableField(value = ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY)
    private String fullPath;
    @TableField(value = ModelConstants.TOPOLOGY_DIR_USERNAME_PROPERTY)
    private String username;
    @TableField(value = ModelConstants.TOPOLOGY_DIR_CATEGORYVALUE_PROPERTY)
    private String categoryValue;
    @TableField(value = ModelConstants.TOPOLOGY_DIR_PROJECTID_PROPERTY)
    private String projectId;
}
