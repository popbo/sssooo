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

/**
 * Oss通用规则
 *
 * @author Chill
 */
public interface OssRule {

	/**
	 * 获取存储桶规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	String bucketName(String bucketName);

	/**
	 * 获取文件名规则
	 *
	 * @param originalFilename 文件名
	 * @return String
	 */
	String fileName(String originalFilename);

	/**
	 * 获取svg适量文件名规则
	 *
	 * @param originalFilename 文件名
	 * @return String
	 */
	String svgFileName(String originalFilename,String type);

	/**
	 * 获取otf字体文件名规则
	 *
	 * @param originalFilename 文件名
	 * @return String
	 */
	String otfFileName(String originalFilename);

	/**
	 * 获取otf字体文件名规则
	 * @return String
	 */
	String cssFileName();

	/**
	 * 获取today()默认文件存储地址
	 *
	 * @return String
	 */
	String filePath();

	/**
	 * 获取svg文件存储地址
	 *
	 * @return String
	 */
	String svgFilePath(String type);

	/**
	 * 获取otf文件存储地址
	 *
	 * @return String
	 */
	String otfFilePath();
	/**
	 * 获取css文件存储地址
	 *
	 * @return String
	 */
	String cssFilePath();

}
