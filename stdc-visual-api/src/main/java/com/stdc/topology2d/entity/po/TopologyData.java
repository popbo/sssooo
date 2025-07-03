package com.stdc.topology2d.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.topology2d.constants.ModelConstants;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;


@Data
@TableName(ModelConstants.TOPOLOGY_DATA_TABLE_NAME)
@ApiModel(value = "组态大屏数据", description = "组态大屏数据")
public class TopologyData {

    @TableId(value = ModelConstants.TOPOLOGY_DIR_ID_PROPERTY, type = IdType.ASSIGN_ID)
    private String id;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY)
    private String collection;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_DATA_PROPERTY)
    private String data;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_NAME_PROPERTY)
    private String name;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_SHARED_PROPERTY)
    private Boolean shared;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_VERSION_PROPERTY)
    private String version;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_SHAREDURL_PROPERTY)
    private String sharedUrl;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_PATH_PROPERTY)
    private String path;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_SHAREDCUSTOM_PROPERTY)
    private Boolean sharedCustom;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_SHAREDENCRYPTION_PROPERTY)
    private Boolean sharedEncryption;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_CREATETIME_PROPERTY)
    private Date createTime;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_UPDATETIME_PROPERTY)
    private Date updateTime;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_CATEGORYKEY_PROPERTY)
    private String categoryKey;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY)
    private String categoryValue;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_MODULETYPE_PROPERTY)
    private String moduleType;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_PASSWORD_PROPERTY)
    private String password;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_FOLDER_PROPERTY)
    private String folder;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY)
    private String username;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_SHAREDPASSWORD_PROPERTY)
    private String sharedPassword;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_SHAREDIMAGE_PROPERTY)
    private String sharedImage;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_VISUALTYPE_PROPERTY)
    private String visualType;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_IMAGE_PROPERTY)
    private String image;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_EXTERNALID_PROPERTY)
    private String externalId;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_CREATEBY_PROPERTY)
    private String createBy;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_UPDATEBY_PROPERTY)
    private String updateBy;
    @TableField(value = ModelConstants.TOPOLOGY_DATA_PROJECTID_PROPERTY)
    private String projectId;
}
