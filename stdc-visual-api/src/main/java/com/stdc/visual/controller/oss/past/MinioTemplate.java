
package com.stdc.visual.controller.oss.past;

import com.stdc.core.http.util.RestTemplateUtil;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.tool.utils.*;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MinIOTemplate
 *
 * @author Chill
 */
@Slf4j
@Data
@Component("pastMinioTemplate")
@AllArgsConstructor
public class MinioTemplate implements OssTemplate {

	/**
	 * MinIO客户端
	 */
	private final MinioClient client;

	/**
	 * 存储桶命名规则
	 */
	private final OssRule ossRule;

	/**
	 * 配置类
	 */
	private final OssProperties ossProperties;


	@Override
	@SneakyThrows
	public void makeBucket(String bucketName) {
		if (
			!client.bucketExists(
				BucketExistsArgs.builder().bucket(getBucketName(bucketName)).build()
			)
		) {
			client.makeBucket(
				MakeBucketArgs.builder().bucket(getBucketName(bucketName)).build()
			);
			client.setBucketPolicy(
				SetBucketPolicyArgs.builder().bucket(getBucketName(bucketName)).config(getPolicyType(getBucketName(bucketName), PolicyType.READ)).build()
			);
		}
	}

	@SneakyThrows
	public Bucket getBucket() {
		return getBucket(getBucketName());
	}

	@SneakyThrows
	public Bucket getBucket(String bucketName) {
		Optional<Bucket> bucketOptional = client.listBuckets().stream().filter(bucket -> bucket.name().equals(getBucketName(bucketName))).findFirst();
		return bucketOptional.orElse(null);
	}

	@SneakyThrows
	public List<Bucket> listBuckets() {
		return client.listBuckets();
	}

	@Override
	public List<VisualFileVO> listSvgS(String type) {
		if (!ObjectUtil.isEmpty(type) && Arrays.asList("system,custom".split(",")).contains(type)){
			return listSvgS(ossProperties.getBucketNameSvg(),ossRule.svgFilePath(type));
		}else if (!ObjectUtil.isEmpty(type) && StringUtil.equals(type,"all")){
			List<VisualFileVO> systemFileVOS = listSvgS(ossProperties.getBucketNameSvg(), ossRule.svgFilePath("system"));
			List<VisualFileVO> customFileVOS = listSvgS(ossProperties.getBucketNameSvg(), ossRule.svgFilePath("custom"));
			systemFileVOS.addAll(customFileVOS);
			return systemFileVOS;
		}else {
			return null;
		}
	}
	/***
	 * 获取到对应 bucket 的，对应前缀的 所有文件信息
	 * @return
	 */
	@Override
	@SneakyThrows
	public List<VisualFileVO> listBackS() {
		String bucketName = ossProperties.getBucketNameBack();

		Iterable<Result<Item>> results = client.listObjects(ListObjectsArgs.builder()
			.bucket(bucketName)
			.prefix(ossRule.filePath())
			.build());
		Iterator<Result<Item>> iterator = results.iterator();
		List<VisualFileVO> VisualFileVOS = new ArrayList<>();
		while(iterator.hasNext()) {
			Item item = iterator.next().get();
			String objectName = item.objectName();
			VisualFileVO file = new VisualFileVO();
			file.setOriginalName(FileUtil.getOriginalName(objectName));
			file.setName(objectName);
			file.setDomain(getOssHost(bucketName));
			file.setLink(fileLink(bucketName, objectName));
			VisualFileVOS.add(file);
		}
		//筛选图片
		return VisualFileVOS
			.stream().filter(v->
				StringUtil.equalsIgnoreCase("jpg", FileUtil.getFileExtension(v.getOriginalName()))
					|| StringUtil.equalsIgnoreCase("jpeg", FileUtil.getFileExtension(v.getOriginalName()))
						|| StringUtil.equalsIgnoreCase("png", FileUtil.getFileExtension(v.getOriginalName()))
			).collect(Collectors.toList());
	}
	@Override
	public List<VisualFileVO> listOtfS() {
		return listOtfS(ossProperties.getBucketNameOtf(),ossRule.otfFilePath());
	}
	@Override
	public List<VisualFileVO> listCSS() {
		return listOtfS(ossProperties.getBucketNameCss(),ossRule.cssFilePath());
	}

	/***
	 * 获取到对应 bucket 的，对应前缀的 所有文件信息
	 * @param bucketName
	 * @return
	 */
	@SneakyThrows
	public List<VisualFileVO> listSvgS(String bucketName,String prefix) {
		Iterable<Result<Item>> results = client.listObjects(ListObjectsArgs.builder()
			.bucket(bucketName)
			.prefix(prefix)
			.build());
		Iterator<Result<Item>> iterator = results.iterator();
		List<VisualFileVO> VisualFileVOS = new ArrayList<>();
		while(iterator.hasNext()) {
			String objectName = iterator.next().get().objectName();
			SvgFileVO file = new SvgFileVO();
			file.setOriginalName(FileUtil.getOriginalName(objectName));
			file.setName(objectName);
			file.setDomain(getOssHost(bucketName));
			file.setSource(RestTemplateUtil.doGet(fileLink(bucketName, objectName),null));
			file.setLink(fileLink(bucketName, objectName));
			VisualFileVOS.add(file);
		}
		return VisualFileVOS;
	}
	/***
	 * 获取到对应 bucket 的，对应前缀的 所有文件信息
	 * @param bucketName
	 * @return
	 */
	@SneakyThrows
	public List<VisualFileVO> listOtfS(String bucketName,String prefix) {
		Iterable<Result<Item>> results = client.listObjects(ListObjectsArgs.builder()
			.bucket(bucketName)
			.prefix(prefix)
			.build());
		Iterator<Result<Item>> iterator = results.iterator();
		List<VisualFileVO> VisualFileVOS = new ArrayList<>();
		while(iterator.hasNext()) {
			String objectName = iterator.next().get().objectName();
			SvgFileVO file = new SvgFileVO();
			file.setOriginalName(FileUtil.getOriginalName(objectName));
			file.setName(objectName);
			file.setDomain(getOssHost(bucketName));
			file.setLink(fileLink(bucketName, objectName));
			VisualFileVOS.add(file);
		}
		return VisualFileVOS;
	}
	private static String formatFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + " B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + " KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + " MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + " GB";
		}
		return fileSizeString;
	}
	@Override
	@SneakyThrows
	public void removeBucket(String bucketName) {
		client.removeBucket(
			RemoveBucketArgs.builder().bucket(getBucketName(bucketName)).build()
		);
	}

	@Override
	@SneakyThrows
	public boolean bucketExists(String bucketName) {
		return client.bucketExists(
			BucketExistsArgs.builder().bucket(getBucketName(bucketName)).build()
		);
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName) {
		copyFile(bucketName, fileName, destBucketName, fileName);
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
		client.copyObject(
			CopyObjectArgs.builder()
				.source(CopySource.builder().bucket(getBucketName(bucketName)).object(fileName).build())
				.bucket(getBucketName(destBucketName))
				.object(destFileName)
				.build()
		);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String fileName) {
		return statFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String bucketName, String fileName) {
		StatObjectResponse stat = client.statObject(
			StatObjectArgs.builder().bucket(getBucketName(bucketName)).object(fileName).build()
		);
		OssFile ossFile = new OssFile();
		ossFile.setName(Func.isEmpty(stat.object()) ? fileName : stat.object());
		ossFile.setLink(fileLink(ossFile.getName()));
		ossFile.setHash(String.valueOf(stat.hashCode()));
		ossFile.setLength(stat.size());
		ossFile.setPutTime(DateUtil.toDate(stat.lastModified().toLocalDateTime()));
		ossFile.setContentType(stat.contentType());
		return ossFile;
	}

	@Override
	public String filePath(String fileName) {
		return getBucketName().concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	public String filePath(String bucketName, String fileName) {
		return getBucketName(bucketName).concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String fileName) {
		return ossProperties.getEndpoint().concat(StringPool.SLASH).concat(getBucketName()).concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String bucketName, String fileName) {
		return ossProperties.getEndpoint().concat(StringPool.SLASH).concat(getBucketName(bucketName)).concat(StringPool.SLASH).concat(fileName);
	}

	@Override
	@SneakyThrows
	public VisualFileVO putFile(MultipartFile file) {
		return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
	}

	@SneakyThrows
	public VisualFileVO putSVG(MultipartFile file, String type) {
		if (StringUtil.isNotBlank(type) && Arrays.asList("system,custom".split(",")).contains(type)){
			return putSvgFile(ossProperties.getBucketNameSvg(), file.getOriginalFilename(),type, file.getInputStream());
		}
		return null;
	}

	@SneakyThrows
	public VisualFileVO putOTF(String fileName, MultipartFile file) {
		return putFile(ossProperties.getBucketNameOtf(), fileName, file);
	}

	@SneakyThrows
	public VisualFileVO putCSS(String fileName, MultipartFile file) {
		return putFile(ossProperties.getBucketNameCss(), fileName, file);
	}

	@SneakyThrows
	public String getDefaultOtfFileName() {
		return StringUtil.isBlank(ossProperties.getDefaultOtfFileName())?"":ossProperties.getDefaultOtfFileName();
	}

	@Override
	@SneakyThrows
	public VisualFileVO putFile(String fileName, MultipartFile file) {
		return putFile(ossProperties.getBucketName(), fileName, file);
	}

	@Override
	@SneakyThrows
	public VisualFileVO putFile(String bucketName, String fileName, MultipartFile file) {
		return putFile(bucketName, fileName, file.getInputStream());
	}

	@Override
	@SneakyThrows
	public VisualFileVO putFile(String fileName, InputStream stream) {
		return putFile(ossProperties.getBucketName(), fileName, stream);
	}

	@Override
	@SneakyThrows
	public VisualFileVO putFile(String bucketName, String fileName, InputStream stream) {
			//判断是否上传otf字体文件
		if (StringUtil.equals(bucketName,ossProperties.getBucketNameOtf())){
			fileName = getOtfFileName(fileName);
			//判断是否上传css字体文件
		}else if (StringUtil.equals(bucketName,ossProperties.getBucketNameCss())){

		}else {
			fileName = getFileName(fileName);
		}
		return putFile(StdcVisualConstant.OSS_PREFIX_BUCKET, bucketName + StringPool.SLASH + fileName, stream, "application/octet-stream");
	}

	@SneakyThrows
	public VisualFileVO putSvgFile(String bucketName, String fileName, String svgType, InputStream stream) {
		return putFile(StdcVisualConstant.OSS_PREFIX_BUCKET, bucketName + StringPool.SLASH + getSvgFileName(fileName,svgType), stream, "application/octet-stream");
	}

	@SneakyThrows
	public VisualFileVO putFile(String bucketName, String fileName, InputStream stream, String contentType) {
		makeBucket(bucketName);
		client.putObject(
			PutObjectArgs.builder()
				.bucket(getBucketName(bucketName))
				.object(fileName)
				.stream(stream, stream.available(), -1)
				.contentType(contentType)
				.build()
		);
		VisualFileVO file = new VisualFileVO();
		file.setOriginalName(FileUtil.getOriginalName(fileName));
		String[] name = fileName.split("/");
		file.setName(name[name.length-1]);
		file.setDomain(getOssHost(bucketName));
		file.setLink(fileLink(bucketName,fileName));
		return file;
	}

	@Override
	@SneakyThrows
	public void removeFile(String fileName) {
		removeFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFile(String bucketName, String fileName) {
		client.removeObject(
			RemoveObjectArgs.builder().bucket(getBucketName(bucketName)).object(fileName).build()
		);
	}

	@Override
	@SneakyThrows
	public void removeFiles(List<String> fileNames) {
		removeFiles(ossProperties.getBucketName(), fileNames);
	}

	@Override
	@SneakyThrows
	public void removeFiles(String bucketName, List<String> fileNames) {
		Stream<DeleteObject> stream = fileNames.stream().map(DeleteObject::new);
		client.removeObjects(RemoveObjectsArgs.builder().bucket(getBucketName(bucketName)).objects(stream::iterator).build());
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @return String
	 */
	private String getBucketName() {
		return getBucketName(ossProperties.getBucketName());
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	private String getBucketName(String bucketName) {
		return ossRule.bucketName(bucketName);
	}

	/**
	 * 根据规则生成文件名称规则
	 *
	 * @param originalFilename 原始文件名
	 * @return string
	 */
	private String getFileName(String originalFilename) {
		return ossRule.fileName(originalFilename);
	}

	/**
	 * 根据规则生成文件名称规则
	 *
	 * @param originalFilename 原始文件名
	 * @return string
	 */
	private String getSvgFileName(String originalFilename,String type) {
		return ossRule.svgFileName(originalFilename,type);
	}

	/**
	 * 根据规则生成文件名称规则
	 *
	 * @param originalFilename 原始文件名
	 * @return string
	 */
	private String getOtfFileName(String originalFilename) {
		return ossRule.otfFileName(originalFilename);
	}

	/**
	 * 根据规则生成文件名称规则
	 * @return string
	 */
	public String getCssFileName() {
		return ossRule.cssFileName();
	}

	/**
	 * 获取文件外链
	 *
	 * @param bucketName bucket名称
	 * @param fileName   文件名称
	 * @param expires    过期时间 <=7 秒级
	 * @return url
	 */
	@SneakyThrows
	public String getPresignedObjectUrl(String bucketName, String fileName, Integer expires) {
		return client.getPresignedObjectUrl(
			GetPresignedObjectUrlArgs.builder()
				.method(Method.GET)
				.bucket(getBucketName(bucketName))
				.object(fileName)
				.expiry(expires)
				.build()
		);
	}

	/**
	 * 获取存储桶策略
	 *
	 * @param policyType 策略枚举
	 * @return String
	 */
	public String getPolicyType(PolicyType policyType) {
		return getPolicyType(getBucketName(), policyType);
	}

	/**
	 * 获取存储桶策略
	 *
	 * @param bucketName 存储桶名称
	 * @param policyType 策略枚举
	 * @return String
	 */
	public static String getPolicyType(String bucketName, PolicyType policyType) {
		StringBuilder builder = new StringBuilder();
		builder.append("{\n");
		builder.append("    \"Statement\": [\n");
		builder.append("        {\n");
		builder.append("            \"Action\": [\n");

		switch (policyType) {
			case WRITE:
				builder.append("                \"s3:GetBucketLocation\",\n");
				builder.append("                \"s3:ListBucketMultipartUploads\"\n");
				break;
			case READ_WRITE:
				builder.append("                \"s3:GetBucketLocation\",\n");
				builder.append("                \"s3:ListBucket\",\n");
				builder.append("                \"s3:ListBucketMultipartUploads\"\n");
				break;
			default:
				builder.append("                \"s3:GetBucketLocation\"\n");
				break;
		}

		builder.append("            ],\n");
		builder.append("            \"Effect\": \"Allow\",\n");
		builder.append("            \"Principal\": \"*\",\n");
		builder.append("            \"Resource\": \"arn:aws:s3:::");
		builder.append(bucketName);
		builder.append("\"\n");
		builder.append("        },\n");
		if (PolicyType.READ.equals(policyType)) {
			builder.append("        {\n");
			builder.append("            \"Action\": [\n");
			builder.append("                \"s3:ListBucket\"\n");
			builder.append("            ],\n");
			builder.append("            \"Effect\": \"Deny\",\n");
			builder.append("            \"Principal\": \"*\",\n");
			builder.append("            \"Resource\": \"arn:aws:s3:::");
			builder.append(bucketName);
			builder.append("\"\n");
			builder.append("        },\n");

		}
		builder.append("        {\n");
		builder.append("            \"Action\": ");

		switch (policyType) {
			case WRITE:
				builder.append("[\n");
				builder.append("                \"s3:AbortMultipartUpload\",\n");
				builder.append("                \"s3:DeleteObject\",\n");
				builder.append("                \"s3:ListMultipartUploadParts\",\n");
				builder.append("                \"s3:PutObject\"\n");
				builder.append("            ],\n");
				break;
			case READ_WRITE:
				builder.append("[\n");
				builder.append("                \"s3:AbortMultipartUpload\",\n");
				builder.append("                \"s3:DeleteObject\",\n");
				builder.append("                \"s3:GetObject\",\n");
				builder.append("                \"s3:ListMultipartUploadParts\",\n");
				builder.append("                \"s3:PutObject\"\n");
				builder.append("            ],\n");
				break;
			default:
				builder.append("\"s3:GetObject\",\n");
				break;
		}

		builder.append("            \"Effect\": \"Allow\",\n");
		builder.append("            \"Principal\": \"*\",\n");
		builder.append("            \"Resource\": \"arn:aws:s3:::");
		builder.append(bucketName);
		builder.append("/*\"\n");
		builder.append("        }\n");
		builder.append("    ],\n");
		builder.append("    \"Version\": \"2012-10-17\"\n");
		builder.append("}\n");
		return builder.toString();
	}

	/**
	 * 获取域名
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	public String getOssHost(String bucketName) {
		return ossProperties.getEndpoint() + StringPool.SLASH + getBucketName(bucketName);
	}

	/**
	 * 获取域名
	 *
	 * @return String
	 */
	public String getOssHost() {
		return getOssHost(ossProperties.getBucketName());
	}

}
