/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.stdc.visual.controller.oss.past;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * VisualFileVO
 *
 * @author Chill
 */
@Data
@ApiModel(description = "文件返回信息")
public class VisualFileVO {
	/**
	 * 文件地址
	 */
	@ApiModelProperty(value = "文件地址", required = true)
	private String link;
	/**
	 * 域名地址
	 */
	@ApiModelProperty(value = "域名地址", required = true)
	private String domain;
	/**
	 * 文件名
	 */
	@ApiModelProperty(value = "文件名", required = true)
	private String name;
	/**
	 * 初始文件名
	 */
	@ApiModelProperty(value = "初始文件名", required = true)
	private String originalName;
	/**
	 * 附件表ID
	 */
	@ApiModelProperty(value = "附件表ID", required = true)
	private Long attachId;
}
