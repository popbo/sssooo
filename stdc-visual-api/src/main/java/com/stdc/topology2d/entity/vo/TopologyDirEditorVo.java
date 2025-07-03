package com.stdc.topology2d.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.topology2d.constants.ModelConstants;
import com.stdc.topology2d.entity.po.TopologyDir;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TopologyDirEditorVo extends TopologyDir {

    @ApiModelProperty("旧路径")
    private String oldFullpath;
    @ApiModelProperty("新路径")
    private String newFullpath;
}
