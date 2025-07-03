package com.stdc.visual.dynamic.base.dataset.dto;

import com.alibaba.fastjson.JSONObject;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:35
 * @describe: api数据源请求
 */
@Data
@ApiModel(description = "api数据源请求")
public class ApiDefinition {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("")
    private String desc;
    @ApiModelProperty("请求地址")
    private String url;
    @ApiModelProperty("请求类型")
    private String method = "GET";
    private List<DatasetTableField> fields;
    @ApiModelProperty("请求参数数据")
    private String request;
    @ApiModelProperty("提取数据")
    private String dataPath;
    private String status;
    private List<JSONObject> datas = new ArrayList<>();
}
