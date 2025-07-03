
package com.stdc.visual.controller.oss.past;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Minio参数配置类
 *
 * @author Chill
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss.minio")
public class OssProperties {

	/**
	 * 是否启用
	 */
	private Boolean enabled;

	/**
	 * 对象存储名称
	 */
	private String name;

	/**
	 * 是否开启租户模式
	 */
	private Boolean tenantMode = false;

	/**
	 * 对象存储服务的URL
	 */
	private String endpoint;

	/**
	 * 应用ID TencentCOS需要
	 */
	private String appId;

	/**
	 * 区域简称 TencentCOS需要
	 */
	private String region;

	/**
	 * Access key就像用户ID，可以唯一标识你的账户
	 */
	private String accessKey;

	/**
	 * Secret key是你账户的密码
	 */
	private String secretKey;

	/**
	 * 默认的存储桶名称
	 */
	private String bucketName = "visual";

	/**
	 * 默认的svg存储桶名称
	 */
	private String bucketNameSvg = "svg";

	/**
	 * 默认的otf字体文件存储桶名称
	 */
	private String bucketNameOtf = "otf";

	/**
	 * 默认的背景问价存储桶位置
	 */
	private String bucketNameBack = "caster";

	/**
	 * 默认的css文件存储桶名称
	 */
	private String bucketNameCss = "css";

	/**
	 * 默认的otf字体文件名称
	 */
	private String defaultOtfFileName;

	/**
	 * 自定义属性
	 */
//	private Kv args;

}
