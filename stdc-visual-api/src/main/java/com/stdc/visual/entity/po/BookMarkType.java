package com.stdc.visual.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/6/27--11:32
 * @describe: 可视化大屏收藏分类
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "bookmark_type")
@ApiModel(value = "可视化大屏收藏分类", description = "可视化大屏收藏分类")
public class BookMarkType {

    /**
     * 主键（唯一）
     */
    @TableId
    private String id;

    /**
     * 名称（唯一）
     */
    @ApiModelProperty(value = "名称")
    private String name;

}
