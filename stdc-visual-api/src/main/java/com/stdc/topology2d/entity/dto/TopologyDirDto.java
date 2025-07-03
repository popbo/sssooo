package com.stdc.topology2d.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.stdc.topology2d.constants.ModelConstants;
import com.stdc.topology2d.entity.po.TopologyDir;
import lombok.Data;

@Data
public class TopologyDirDto extends TopologyDir {

    private String folder;

}
