package com.stdc.visual.entity.edit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2023/5/17--15:45
 * @describe: 工程化编辑 - 全局替换 操作类
 */
@Data
@ApiModel(value = "全局替换内容", description = "全局替换内容")
public class ReplaceAllEditDTO {

    /**
     * 查找内容
     */
    @ApiModelProperty(value = "查找内容")
    private String findText;

    /**
     * 替换内容
     */
    @ApiModelProperty(value = "替换内容")
    private String replaceText;

    /**
     * 将要替换字符的个数
     */
    @ApiModelProperty(value = "将要替换字符的个数")
    private int count;


}
