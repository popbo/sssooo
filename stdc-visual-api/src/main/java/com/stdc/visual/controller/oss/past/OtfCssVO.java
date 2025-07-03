package com.stdc.visual.controller.oss.past;

import lombok.Builder;
import lombok.Data;

/**
 * @Author wang_jie
 * @Date 2022/4/2 14:06
 * @description: 字体css表达类
 */
@Data
@Builder
public class OtfCssVO {

	private static final String cssTemplate =
		"@font-face {\n" +
		"     font-family: \"%s\";\n" +
		"     src: url(\"%s\");\n" +
		"     font-weight: normal;\n" +
		"     font-style: normal;\n" +
		"}\n";

	private String fileName;

	private String url;

	@Override
	public String toString() {
		return String.format(cssTemplate,fileName,url);
	}
}

