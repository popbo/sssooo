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



import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * OssTemplate抽象API
 *
 * @author Chill
 */
public interface OssTemplate {

	/**
	 * 创建 存储桶
	 *
	 * @param bucketName 存储桶名称
	 */
	void makeBucket(String bucketName);

	/**
	 * 删除 存储桶
	 *
	 * @param bucketName 存储桶名称
	 */
	void removeBucket(String bucketName);

	/**
	 * 存储桶是否存在
	 *
	 * @param bucketName 存储桶名称
	 * @return boolean
	 */
	boolean bucketExists(String bucketName);

	/**
	 * 拷贝文件
	 *
	 * @param bucketName     存储桶名称
	 * @param fileName       存储桶文件名称
	 * @param destBucketName 目标存储桶名称
	 */
	void copyFile(String bucketName, String fileName, String destBucketName);

	/**
	 * 拷贝文件
	 *
	 * @param bucketName     存储桶名称
	 * @param fileName       存储桶文件名称
	 * @param destBucketName 目标存储桶名称
	 * @param destFileName   目标存储桶文件名称
	 */
	void copyFile(String bucketName, String fileName, String destBucketName, String destFileName);

	/**
	 * 获取文件信息
	 *
	 * @param fileName 存储桶文件名称
	 * @return InputStream
	 */
	OssFile statFile(String fileName);

	/**
	 * 获取文件信息
	 *
	 * @param bucketName 存储桶名称
	 * @param fileName   存储桶文件名称
	 * @return InputStream
	 */
	OssFile statFile(String bucketName, String fileName);

	/**
	 * 获取文件相对路径
	 *
	 * @param fileName 存储桶对象名称
	 * @return String
	 */
	String filePath(String fileName);

	/**
	 * 获取文件相对路径
	 *
	 * @param bucketName 存储桶名称
	 * @param fileName   存储桶对象名称
	 * @return String
	 */
	String filePath(String bucketName, String fileName);

	/**
	 * 获取文件地址
	 *
	 * @param fileName 存储桶对象名称
	 * @return String
	 */
	String fileLink(String fileName);

	/**
	 * 获取文件地址
	 *
	 * @param bucketName 存储桶名称
	 * @param fileName   存储桶对象名称
	 * @return String
	 */
	String fileLink(String bucketName, String fileName);

	/**
	 * 上传文件
	 *
	 * @param file 上传文件类
	 * @return VisualFileVO
	 */
	VisualFileVO putFile(MultipartFile file);

	/**
	 * 上传文件
	 *
	 * @param file     上传文件类
	 * @param fileName 上传文件名
	 * @return VisualFileVO
	 */
	VisualFileVO putFile(String fileName, MultipartFile file);

	/**
	 * 上传文件
	 *
	 * @param bucketName 存储桶名称
	 * @param fileName   上传文件名
	 * @param file       上传文件类
	 * @return VisualFileVO
	 */
	VisualFileVO putFile(String bucketName, String fileName, MultipartFile file);

	/**
	 * 上传文件
	 *
	 * @param fileName 存储桶对象名称
	 * @param stream   文件流
	 * @return VisualFileVO
	 */
	VisualFileVO putFile(String fileName, InputStream stream);

	/**
	 * 上传文件
	 *
	 * @param bucketName 存储桶名称
	 * @param fileName   存储桶对象名称
	 * @param stream     文件流
	 * @return VisualFileVO
	 */
	VisualFileVO putFile(String bucketName, String fileName, InputStream stream);

	/**
	 * 删除文件
	 *
	 * @param fileName 存储桶对象名称
	 */
	void removeFile(String fileName);

	/**
	 * 删除文件
	 *
	 * @param bucketName 存储桶名称
	 * @param fileName   存储桶对象名称
	 */
	void removeFile(String bucketName, String fileName);

	/**
	 * 批量删除文件
	 *
	 * @param fileNames 存储桶对象名称集合
	 */
	void removeFiles(List<String> fileNames);

	/**
	 * 批量删除文件
	 *
	 * @param bucketName 存储桶名称
	 * @param fileNames  存储桶对象名称集合
	 */
	void removeFiles(String bucketName, List<String> fileNames);


	/***
	 * 获取到服务器所有的 svg 文件
	 * @return
	 */
	List<VisualFileVO> listSvgS(String type);


	/***
	 * 获取到服务器所有的 背景图片 文件
	 * @return
	 */
	List<VisualFileVO> listBackS();

	/***
	 * 获取到服务器所有的 otf 文件
	 * @return
	 */
	List<VisualFileVO> listOtfS();

	/**
	 * 获取css 文件
	 * @return
	 */
	List<VisualFileVO> listCSS();

}
