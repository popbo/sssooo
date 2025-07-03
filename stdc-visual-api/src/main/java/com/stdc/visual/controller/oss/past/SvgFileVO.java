package com.stdc.visual.controller.oss.past;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author wang_jie
 * @Date 2022/3/8 19:12
 * @description:
 */
@Data
@ApiModel(description = "svg文件返回信息")
public class SvgFileVO extends VisualFileVO {
	@ApiModelProperty(value = "svg文件xml信息", required = true)
	private String source;
}
