package com.stdc.visual.dynamic.base.dataset.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:23
 * @describe: 数据集字段详情
 */
@Data
@Builder
@TableName(StdcVisualConstant.PROJECT_PREFIX + "dataset_table_field")
@AllArgsConstructor //全参构造函数
@NoArgsConstructor  //无参构造函数
public class DatasetTableField implements Serializable {
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("表ID")
    private String tableId;
    @ApiModelProperty("原始名称")
    private String originName;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("DE名称")
    private String dataeaseName;
    @ApiModelProperty("分组类型,维度还是度量")
    private String groupType;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("大小")
    private Integer size;
    @ApiModelProperty("de类型")
    private Integer deType;
    @ApiModelProperty("de类型格式")
    private Integer deTypeFormat;
    @ApiModelProperty("de抽取类型")
    private Integer deExtractType;
    @ApiModelProperty("额外字段")
    private Integer extField;
    @ApiModelProperty("是否选中")
    private Boolean checked;
    @ApiModelProperty("列号")
    private Integer columnIndex;
    @ApiModelProperty("上次同步时间")
    private Long lastSyncTime;

    private static final long serialVersionUID = 1L;
}
