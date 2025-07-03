package com.stdc.visual.controller.dataset;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.request.DataSetEnumRequest;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.entity.po.VisualConfig;
import com.stdc.visual.service.IVisualConfigService;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/19--14:35
 * @describe: 数据集：数据集表
 */
@Api(tags = "数据集：数据集")
@ApiSupport(author = "wangjie",order = 5)
@RestController
@RequestMapping("dataset/table")
public class DataSetTableController {
    @Resource
    private DataSetTableService dataSetTableService;

    @Autowired
    private IVisualConfigService configService;

    @ApiOperation("批量保存")
    @PostMapping("batchAdd")
    public void batchAdd(@RequestBody List<DataSetTableRequest> datasetTable) throws Exception {
        dataSetTableService.batchInsert(datasetTable);
    }

    @ApiOperation("更新")
    @PostMapping("update")
    public void save(@RequestBody DataSetTableRequest datasetTable) throws Exception {
        if (datasetTable.getType().equalsIgnoreCase("excel")) {
            dataSetTableService.saveExcel(datasetTable);
        } else {
            dataSetTableService.save(datasetTable);
        }
    }

    @ApiOperation("修改")
    @PostMapping("alter")
    public void alter(@RequestBody DataSetTableRequest request) throws Exception {
        dataSetTableService.alter(request);
    }

    @ApiOperation("删除")
    @PostMapping("delete/{id}")
    public R delete(@ApiParam(name = "id", value = "数据集ID", required = true) @PathVariable String id) throws Exception {
        List<VisualConfig> configs = configService.list();
        for (VisualConfig config : configs) {
            if (config.getComponent().contains(id)){
                return R.fail("数据集已使用");
            }
        }
        dataSetTableService.delete(id);
        return R.success("");
    }

    @ApiOperation("查询")
    @PostMapping("list")
    public R list(@RequestBody DataSetTableRequest dataSetTableRequest) {
        return R.data(dataSetTableService.list(dataSetTableRequest));
    }

    @ApiOperation("查询组")
    @PostMapping("listAndGroup")
    public R listAndGroup(@RequestBody DataSetTableRequest dataSetTableRequest) {
        return R.data(dataSetTableService.listAndGroup(dataSetTableRequest));
    }

    @ApiOperation("详息")
    @PostMapping("get/{id}")
    public R get(@ApiParam(name = "id", value = "数据集ID", required = true) @PathVariable String id) {
        return R.data(dataSetTableService.get(id));
    }

    @ApiOperation("带权限查询")
    @PostMapping("getWithPermission/{id}")
    public R getWithPermission(@PathVariable String id) {
        DataSetTableRequest dt = new DataSetTableRequest();
        dt.setId(id);
        List<DataSetTableDTO> res = dataSetTableService.list(dt);
        if (CollectionUtils.isNotEmpty(res)){
            return R.data(res.get(0));
        }
        return R.data(null);
    }

    @ApiOperation("查询原始字段")
    @PostMapping("getFields")
    public R getFields(@RequestBody DatasetTable datasetTable) throws Exception {
        return R.data(dataSetTableService.getFields(datasetTable));
    }

    @ApiOperation("查询生成字段")
    @PostMapping("getFieldsFromDE")
    public R getFieldsFromDE(@RequestBody DataSetTableRequest dataSetTableRequest) throws Exception {
        return R.data(dataSetTableService.getFieldsFromDE(dataSetTableRequest));
    }

    @ApiOperation("查询预览数据")
    @PostMapping("getPreviewData/{page}/{pageSize}")
    public R getPreviewData(@RequestBody DataSetTableRequest dataSetTableRequest, @PathVariable Integer page, @PathVariable Integer pageSize) throws Exception {
        return R.data(dataSetTableService.getPreviewData(dataSetTableRequest, page, pageSize, null));
    }

    @ApiOperation("根据sql查询预览数据")
    @PostMapping("sqlPreview")
    public R getSQLPreview(@RequestBody DataSetTableRequest dataSetTableRequest) throws Exception {
        //去除sql中的";"符号
        dataSetTableRequest.setInfo(dataSetTableRequest.getInfo().replaceAll(";",""));
        return R.data(dataSetTableService.getSQLPreview(dataSetTableRequest));
    }

    @ApiOperation("查询字段枚举值")
    @PostMapping("enumPreview")
    public R getEnumPreview(@RequestBody DataSetEnumRequest request) throws Exception {
        return R.data(dataSetTableService.getEnumPreview(request));
    }

    @ApiOperation("客户预览数据")
    @PostMapping("customPreview")
    public R customPreview(@RequestBody DataSetTableRequest dataSetTableRequest) throws Exception {
        return R.data(dataSetTableService.getCustomPreview(dataSetTableRequest));
    }

    @ApiOperation("数据集详息")
    @PostMapping("datasetDetail/{id}")
    public R datasetDetail(@PathVariable String id) {
        return R.data(dataSetTableService.getDatasetDetail(id));
    }

    @ApiOperation("搜索")
    @PostMapping("search")
    public R search(@RequestBody DataSetTableRequest dataSetTableRequest) {
        return R.data(dataSetTableService.search(dataSetTableRequest));
    }

    @ApiOperation("数据集同步表结构")
    @PostMapping("syncField/{id}")
    public R syncDatasetTableField(@PathVariable String id) throws Exception {
        return R.data(dataSetTableService.syncDatasetTableField(id));
    }

    @ApiOperation("关联数据集预览数据")
    @PostMapping("unionPreview")
    public R unionPreview(@RequestBody DataSetTableRequest dataSetTableRequest) throws Exception {
        return R.data(dataSetTableService.getUnionPreview(dataSetTableRequest));
    }
}
