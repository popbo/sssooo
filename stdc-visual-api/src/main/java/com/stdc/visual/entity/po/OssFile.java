package com.stdc.visual.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/27--11:33
 * @describe: 对象存储
 */
@Data
@Accessors(chain = true)
@TableName(StdcVisualConstant.PROJECT_PREFIX + "oss_file")
@ApiModel(value = "OssFile", description = "对象存储实体")
public class OssFile implements Serializable {

    //文件来源 可视化平台
    public static final String FILE_SOURCE_VISUAL = "VISUAL";

    //文件来源 2d组态平台
    public static final String FILE_SOURCE_TOPOLOGY_2D = "TOPOLOGY_2D";

    //文件来源 3d组态平台
    public static final String FILE_SOURCE_TOPOLOGY_3D = "TOPOLOGY_3D";

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("文件原名称前缀")
    private String namePrefix;

    @ApiModelProperty("文件原名称后缀")
    private String nameSuffix;

    @ApiModelProperty("文件访问地址")
    private String link;

    @ApiModelProperty("存储地址")
    private String doMain;

    @ApiModelProperty("大屏id")
    private String visualId;

    @ApiModelProperty("大屏id")
    private String configId;

    @ApiModelProperty("组件id")
    private String componetId;

    @ApiModelProperty("文件分类")//(font)字体、(back)大屏背景、(componet)组件文件，(thumbnail)大屏截图缩略图、(svg)svg文件、(css)css文件
    private String type;

    @ApiModelProperty(value = "是否为系统文件: system 系统 custom自定义")
    private String systemType;

    @ApiModelProperty(value = "数据来源: VISUAL 可视化 , TOPOLOGY_2D 组态2d , TOPOLOGY_3D 组态3d , 默认为可视化")
    private String source;

    @TableLogic
    @ApiModelProperty(value = "状态[0:未删除,1:删除]")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

}
