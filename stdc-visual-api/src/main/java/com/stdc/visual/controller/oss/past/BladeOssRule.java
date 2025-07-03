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

import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 默认存储桶生成规则
 *
 * @author Chill
 */
@Component
@AllArgsConstructor
public class BladeOssRule implements OssRule {

	public static final String CSS_NAME = "font.css";

	@Override
	public String bucketName(String bucketName) {
//		String prefix = (tenantMode) ? AuthUtil.getTenantId().concat(StringPool.DASH) : StringPool.EMPTY;
//		return prefix + bucketName;
		return bucketName;
	}

	@Override
	public String fileName(String originalFilename) {
		return filePath() + StringUtil.randomUUID() + StringPool.UNDERSCORE + FileUtil.getNameWithoutExtension(originalFilename) + StringPool.DOT + FileUtil.getFileExtension(originalFilename);
	}

	@Override
	public String svgFileName(String originalFilename,String type) {
		return svgFilePath(type) + StringUtil.randomUUID()  + "_" + FileUtil.getNameWithoutExtension(originalFilename) + StringPool.DOT + FileUtil.getFileExtension(originalFilename);
	}

	@Override
	public String otfFileName(String originalFilename) {
		return otfFilePath() + StringUtil.randomUUID() + StringPool.UNDERSCORE + FileUtil.getNameWithoutExtension(originalFilename) + StringPool.DOT + FileUtil.getFileExtension(originalFilename);
	}

	@Override
	public String cssFileName() {
		return cssFilePath() + CSS_NAME;
	}

	@Override
	public String filePath() {
		return "upload" + StringPool.SLASH;
	}

	@Override
	public String svgFilePath(String type) {
		if (!StringUtil.isBlank(type)){
			return "upload" + StringPool.SLASH + type + StringPool.SLASH;
		}
		return "upload" + StringPool.SLASH;
	}

	@Override
	public String otfFilePath() {
		return "upload" + StringPool.SLASH;
	}

	@Override
	public String cssFilePath() {
		return "upload" + StringPool.SLASH;
	}


}
