package com.stdc.visual.controller.oss.past;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.entity.po.Visual;
import com.stdc.visual.service.IVisualService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author wang_jie
 * @Date 2022/4/8 13:38
 * @description:
 */
@Slf4j
//@RestController
@AllArgsConstructor
//@RequestMapping("/visual")
@Api(value = "大屏文件", tags = "大屏文件")
public class VisualFileController {

	private final OssTemplate ossTemplate;
	private final MinioTemplate minioTemplate;
	private final IVisualService visualService;


	/**
	 * 上传文件
	 */
	@SneakyThrows
	@PostMapping("/put-file")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "上传文件", notes = "传入文件")
	public R<VisualFileVO> putFile(@ApiParam(value = "上传文件", required = true) @RequestParam MultipartFile file) {
		VisualFileVO VisualFileVO = ossTemplate.putFile(file);
		return R.data(VisualFileVO);
	}

	/**
	 * 上传svg文件
	 */
	@SneakyThrows
	@PostMapping("/put-svg/{type}")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "上传svg文件", notes = "上传svg文件")
	public R<VisualFileVO> putSvgFile(@ApiParam(value = "上传文件", required = true) @RequestParam MultipartFile file, @PathVariable("type")String type) {
		final String otfSuffix = FileUtil.getFileExtension(file.getOriginalFilename());
		if(!StringUtils.endsWithIgnoreCase(otfSuffix,"svg")){
			return R.fail("文件格式错误");
		}
		if (StringUtil.isBlank(type)){
			return R.fail("type为空");
		}
		VisualFileVO VisualFileVO = minioTemplate.putSVG(file,type);
		return R.data(VisualFileVO);
	}

	/**
	 * 获取分类svg文件
	 */
	@SneakyThrows
	@GetMapping("/get-svg/{type}")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "获取分类svg文件", notes = "获取svg文件")
	public R<List<VisualFileVO>> listSvgS(@PathVariable("type") String type){
		return R.data(minioTemplate.listSvgS(type));
	}

	/**
	 * 获取所有svg文件
	 */
	@SneakyThrows
	@GetMapping("/get-svg")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "获取所有svg文件", notes = "获取svg文件")
	public R<List<VisualFileVO>> listSvgS(){
		return R.data(minioTemplate.listSvgS("all"));
	}


	/**
	 * 获取所有背景图片文件
	 */
	@SneakyThrows
	@GetMapping("/get-back")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "获取所有背景图片文件", notes = "获取背景图片文件")
	public R<List<VisualFileVO>> listBackS(){
		return R.data(minioTemplate.listBackS());
	}
	/**
	 * 上传otf文件
	 */
	@SneakyThrows
	@PostMapping("/put-otf")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "上传字体文件", notes = "上传字体文件")
	public R<? extends Object> putOtfFile(@ApiParam(value = "字体文件", required = true) @RequestParam MultipartFile file, @ApiParam(value = "自定义字体文件名称", required = true) @RequestParam String fileName) {
		final String otfSuffix = StringPool.DOT + FileUtil.getFileExtension(file.getOriginalFilename());
		if(!(StringUtils.endsWithIgnoreCase(otfSuffix,"otf") || StringUtils.endsWithIgnoreCase(otfSuffix,"ttf"))){
			return R.fail("文件格式错误");
		}
		if (StringUtil.isBlank(fileName)){
			fileName = file.getOriginalFilename();
		}else {
			fileName = fileName + otfSuffix;
		}
		List<VisualFileVO> VisualFileVOS = minioTemplate.listOtfS();
		List<String> fileNameS = VisualFileVOS.stream().map(otf -> otf.getOriginalName()).collect(Collectors.toList());
		if (fileNameS.contains(fileName)){
			return R.fail("字体已存在");
		}

		if (minioTemplate.getDefaultOtfFileName().contains(fileName)){
			return R.fail("禁止更改默认字体");
		}
		//上传成功
		VisualFileVO VisualFileVO = minioTemplate.putOTF(fileName, file);
		if (ObjectUtil.isNotEmpty(VisualFileVO)){
			VisualFileVOS.add(VisualFileVO);
		}else {
			return R.fail("文件上传失败");
		}
		AtomicReference<StringBuffer> css = new AtomicReference<>(new StringBuffer());
		List<String> voS = VisualFileVOS.stream().map(otf -> OtfCssVO.builder().fileName(FileUtil.getNameWithoutExtension(otf.getOriginalName())).url(otf.getLink()).build().toString()).collect(Collectors.toList());
		voS.forEach(cssVo-> css.get().append(cssVo));
		String cssStr = css.get().toString();
		//删除原先css
		minioTemplate.removeFile(minioTemplate.getOssProperties().getBucketNameCss(), minioTemplate.getOssRule().cssFilePath() + "font.css");
		VisualFileVO cssFile =minioTemplate.putFile(minioTemplate.getOssProperties().getBucketNameCss(), minioTemplate.getCssFileName(), new ByteArrayInputStream(cssStr.getBytes()));
		if (ObjectUtil.isNotEmpty(cssFile)){
			return R.success("上传成功");
		}else {
			if (ObjectUtil.isNotEmpty(VisualFileVO)){
				minioTemplate.removeFile(minioTemplate.getOssProperties().getBucketNameOtf(),VisualFileVO.getName());
			}
			return R.fail("上传失败");
		}
	}

	/**
	 * 获取字体css文件
	 */
	@SneakyThrows
	@GetMapping("/get-css")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "获取字体css文件", notes = "获取字体文件")
	public R<List<VisualFileVO>> listCSS(){
		return R.data(minioTemplate.listCSS().stream().filter(css->StringUtil.equals(css.getOriginalName(), BladeOssRule.CSS_NAME)).collect(Collectors.toList()));
	}

	/**
	 * 获取字体文件
	 */
	@SneakyThrows
	@GetMapping("/get-otf")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "获取字体文件", notes = "获取字体文件")
	public R<List<VisualFileVO>> listOtf(){
		return R.data(minioTemplate.listOtfS());
	}

	/**
	 * 删除字体文件
	 */
	@SneakyThrows
	@DeleteMapping("/del-otf/{fileName}")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "删除字体文件", notes = "删除字体文件")
	public R<? extends Object> delOtf(@ApiParam(value = "字体文件名称", required = true) @PathVariable("fileName") String fileName){
		try {
			//查找文件名
			List<VisualFileVO> visualFileVOS = minioTemplate.listOtfS().stream().filter(otf -> StringUtil.equals(FileUtil.getNameWithoutExtension(otf.getOriginalName()),fileName)).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(visualFileVOS)){
				return R.fail("文件不存在");
			}else if (visualFileVOS.size() > 1){
				log.error("字体文件重复");
				return R.fail("未知错误,请联系管理员");
			}
			VisualFileVO delFile = visualFileVOS.get(0);
			//删除字体
			minioTemplate.removeFile(minioTemplate.getOssProperties().getBucketNameOtf(), delFile.getName());
			//删除原先css
			minioTemplate.removeFile(minioTemplate.getOssProperties().getBucketNameCss(), minioTemplate.getOssRule().cssFilePath() + "font.css");
			//更新字体文件
			List<VisualFileVO> VisualFileVOS = minioTemplate.listOtfS();
			AtomicReference<StringBuffer> css = new AtomicReference<>(new StringBuffer());
			List<String> voS = VisualFileVOS.stream().map(otf -> OtfCssVO.builder().fileName(FileUtil.getNameWithoutExtension(otf.getOriginalName())).url(otf.getLink()).build().toString()).collect(Collectors.toList());
			voS.forEach(cssVo-> css.get().append(cssVo));
			String cssStr = css.get().toString();
			//上传新的css
			minioTemplate.putFile(minioTemplate.getOssProperties().getBucketNameCss(), minioTemplate.getCssFileName(), new ByteArrayInputStream(cssStr.getBytes()));
		} catch (Exception e) {
			log.error("删除字体文件失败--->"+e.getMessage());
			return R.data("系统异常");
		}
		return R.data("删除成功");
	}

	/**
	 * 更新字体文件名称
	 */
	@SneakyThrows
	@PostMapping("/update-otf")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "更新字体文件名称", notes = "更新字体文件名称")
	public R<? extends Object> updateOtf(@ApiParam(value = "原名称", required = true)  String oldName,@ApiParam(value = "新名称", required = true)  String newName){
		try {
			if(StringUtil.equals(oldName,newName)){
				return R.fail("新旧名称不能相同");
			}
			//查找文件名
			List<VisualFileVO> visualFileVOS = minioTemplate.listOtfS().stream().filter(otf -> StringUtil.equals(FileUtil.getNameWithoutExtension(otf.getOriginalName()),oldName)).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(visualFileVOS)){
				return R.fail("文件不存在");
			}else if (visualFileVOS.size() > 1){
				log.error("字体文件重复");
				return R.fail("未知错误,请联系管理员");
			}
			VisualFileVO oldFile = visualFileVOS.get(0);
			String oldPath = oldFile.getName();
			String newPath = oldFile.getName().replace(oldName,newName);
			//复制文件
			minioTemplate.copyFile(minioTemplate.getOssProperties().getBucketNameOtf(),oldPath,minioTemplate.getOssProperties().getBucketNameOtf(),newPath);
			//删除字体
			minioTemplate.removeFile(minioTemplate.getOssProperties().getBucketNameOtf(),oldPath);
			//删除原先css
			minioTemplate.removeFile(minioTemplate.getOssProperties().getBucketNameCss(), minioTemplate.getOssRule().cssFilePath() + "font.css");
			//更新字体文件
			List<VisualFileVO> VisualFileVOS = minioTemplate.listOtfS();
			AtomicReference<StringBuffer> css = new AtomicReference<>(new StringBuffer());
			List<String> voS = VisualFileVOS.stream().map(otf -> OtfCssVO.builder().fileName(FileUtil.getNameWithoutExtension(otf.getOriginalName())).url(otf.getLink()).build().toString()).collect(Collectors.toList());
			voS.forEach(cssVo-> css.get().append(cssVo));
			String cssStr = css.get().toString();
			//上传新的css
			minioTemplate.putFile(minioTemplate.getOssProperties().getBucketNameCss(), minioTemplate.getCssFileName(), new ByteArrayInputStream(cssStr.getBytes()));
		} catch (Exception e) {
			log.error("更新字体文件失败--->"+e.getMessage());
			return R.data("更新字体文件失败");
		}
		return R.data("更新成功");
	}



	/**
	 * 删除背景图片文件
	 */
	@SneakyThrows
	@DeleteMapping("/del-back/{fileName}")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "删除背景图片", notes = "删除背景图片")
	public R<? extends Object> delBack(@ApiParam(value = "字体文件名称", required = true) @PathVariable("fileName") String fileName){
		try {
			//查找文件名
			List<VisualFileVO> visualFileVOS = minioTemplate.listBackS().stream().filter(otf -> StringUtil.equals(FileUtil.getNameWithoutExtension(otf.getOriginalName()),fileName)).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(visualFileVOS)){
				return R.fail("文件不存在");
			}else if (visualFileVOS.size() > 1){
				log.error("字体文件重复");
				return R.fail("未知错误,请联系管理员");
			}
			VisualFileVO delFile = visualFileVOS.get(0);
			//删除背景图
			minioTemplate.removeFile(minioTemplate.getOssProperties().getBucketNameBack(), delFile.getName());
		} catch (Exception e) {
			String errorMsg =e.getMessage();
			log.error("删除字体文件失败--->"+errorMsg);
			return R.data("系统异常");
		}
		return R.data("删除成功");
	}

	/**
	 * 更新背景图片名称
	 */
	@SneakyThrows
	@PostMapping("/update-back")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "更新背景图片名称", notes = "更新背景图片名称")
	public R<? extends Object> updateBack(@ApiParam(value = "原名称", required = true)  String oldName,@ApiParam(value = "新名称", required = true)  String newName){
		try {
			if(StringUtil.equals(oldName,newName)){
				return R.fail("新旧名称不能相同");
			}
			//查找新文件名
			List<VisualFileVO> visualFileVOS = minioTemplate.listBackS().stream().filter(otf -> StringUtil.equals(FileUtil.getNameWithoutExtension(otf.getOriginalName()),newName)).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(visualFileVOS)){
				return R.fail("文件已存在");
			}else if (visualFileVOS.size() > 1){
				log.error("字体文件重复");
				return R.fail("未知错误,请联系管理员");
			}
			VisualFileVO oldFile =minioTemplate.listBackS().stream().filter(otf -> StringUtil.equals(FileUtil.getNameWithoutExtension(otf.getOriginalName()),oldName)).collect(Collectors.toList()).get(0);
			String oldPath = oldFile.getName();
			String newPath = oldFile.getName().replace(oldName,newName);
			//复制文件
			minioTemplate.copyFile(minioTemplate.getOssProperties().getBucketNameBack(),oldPath,minioTemplate.getOssProperties().getBucketNameBack(),newPath);
			//删除文件
			minioTemplate.removeFile(minioTemplate.getOssProperties().getBucketNameBack(),oldPath);
			} catch (Exception e) {
				String errorMsg =e.getMessage();
				log.error("更新字体文件失败--->"+errorMsg);
				return R.fail("更新字体文件失败");
		}
		return R.data("更新成功");
	}


	/**
	 * 更新背景图片名称
	 */
	@SneakyThrows
	@PostMapping("/upload/back/{visualId}")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "上传缩略图", notes = "上传缩略图")
	public R<? extends Object> uploadBackGround(@ApiParam(value = "字体文件", required = true) @RequestParam MultipartFile file, @ApiParam(value = "大屏id", required = true) @PathVariable("visualId") Long visualId){
		if (!checkFileSize(file.getSize(),3,"M")){
			return R.fail("背景图应该小于等于3MB");
		}
		String suffix = FileUtil.getFileExtension(file.getOriginalFilename());
		String suffixS = "bmp,jpg,jpeg,png,gif";
		if (!suffixS.contains(suffix)){
			return R.fail("图片格式错误");
		}
		List<Visual> visuals = visualService.list(new QueryWrapper<Visual>().eq("id", visualId));
		if (CollectionUtils.isEmpty(visuals)){
			return R.fail("大屏id错误");
		}
		VisualFileVO VisualFileVO = ossTemplate.putFile(file);
		Visual visual = new Visual();
		visual.setId(visualId);
		visual.setBackgroundUrl(VisualFileVO.getLink());
		boolean b = visualService.updateById(visual);
		if(b){
			return R.data("上传成功");
		}else{
			return R.fail("上传失败");
		}
	}

	/**
	 * 判断文件大小
	 *
	 * @param len
	 *            文件长度
	 * @param size
	 *            限制大小
	 * @param unit
	 *            限制单位（B,K,M,G）
	 * @return
	 */
	public static boolean checkFileSize(Long len, int size, String unit) {
		double fileSize = 0;
		if ("B".equals(unit.toUpperCase())) {
			fileSize = (double) len;
		} else if ("K".equals(unit.toUpperCase())) {
			fileSize = (double) len / 1024;
		} else if ("M".equals(unit.toUpperCase())) {
			fileSize = (double) len / 1048576;
		} else if ("G".equals(unit.toUpperCase())) {
			fileSize = (double) len / 1073741824;
		}
		if (fileSize > size) {
			return false;
		}
		return true;
	}

}
