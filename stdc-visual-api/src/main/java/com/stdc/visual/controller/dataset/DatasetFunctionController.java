package com.stdc.visual.controller.dataset;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.dynamic.service.DatasetFunctionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wang_jie
 * @data: 2022/5/20--9:49
 * @describe: 数据集方法
 */
@Api(tags = "数据集：自定义字段SQL方法")
@ApiSupport(author = "wangjie",order = 3)
@RestController
@RequestMapping("dataset/function")
public class DatasetFunctionController {
    @Resource
    private DatasetFunctionService datasetFunctionService;

    @ApiOperation("查询")
    @PostMapping("listByTableId/{tableId}")
    public R listByTableId(@PathVariable String tableId) {
        return R.data(datasetFunctionService.listByTableId(tableId));
    }
}