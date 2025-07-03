package com.stdc.visual.controller.dataset;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableUnionDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableUnion;
import com.stdc.visual.dynamic.service.DataSetTableUnionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/19--15:31
 * @describe: 数据集：关联关系
 */
@Api(tags = "数据集：关联关系")
@ApiSupport(author = "wangjie",order = 7)
@RestController
@RequestMapping("dataset/union")
public class DataSetTableUnionController {
    @Resource
    private DataSetTableUnionService dataSetTableUnionService;

    @ApiOperation("保存")
    @PostMapping("save")
    public R save(@RequestBody DatasetTableUnion datasetTableUnion) {
        return R.data(dataSetTableUnionService.save(datasetTableUnion));
    }

    @ApiOperation("删除")
    @PostMapping("delete/{id}")
    public void delete(@PathVariable String id) {
        dataSetTableUnionService.delete(id);
    }

    @ApiOperation("查询")
    @PostMapping("listByTableId/{tableId}")
    public R listByTableId(@PathVariable String tableId) {
        return R.data(dataSetTableUnionService.listByTableId(tableId));
    }
}
