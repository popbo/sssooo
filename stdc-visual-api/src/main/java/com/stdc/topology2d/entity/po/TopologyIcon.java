package com.stdc.topology2d.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.topology2d.constants.ModelConstants;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@TableName(ModelConstants.TOPOLOGY_ICON_TABLE_NAME)
@ApiModel(value = "组态icon", description = "组态icon")
public class TopologyIcon {

    @TableId
    @TableField(value = ModelConstants.TOPOLOGY_ICON_UNICODE_PROPERTY)
    private String unicode;
    @TableField(value = ModelConstants.TOPOLOGY_ICON_ICONFAMILY_PROPERTY)
    private String iconFamily;
    @TableField(value = ModelConstants.TOPOLOGY_ICON_NAME_PROPERTY)
    private String name;
    @TableField(value = ModelConstants.TOPOLOGY_ICON_ICONID_PROPERTY)
    private String iconId;
    @TableField(value = ModelConstants.TOPOLOGY_ICON_FONTCLASS_PROPERTY)
    private String fontClass;
    @TableField(value = ModelConstants.TOPOLOGY_ICON_UNICODEDECIMAL_PROPERTY)
    private String unicodeDecimal;
}
