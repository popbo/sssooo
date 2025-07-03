package com.stdc.visual.controller.dataset;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.dynamic.base.dataset.dto.DatasetTableFieldType;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.DataSetTableFieldsService;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.dynamic.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/19--14:54
 * @describe: 数据集：字段
 */
@Api(tags = "数据集：字段")
@ApiSupport(author = "wangjie",order = 6)
@RestController
@RequestMapping("/dataset/field")
public class DataSetTableFieldController {

    @Resource
    private DataSetTableFieldsService dataSetTableFieldsService;

    @Resource
    private DataSetTableService dataSetTableService;

    @Resource
    private PermissionService permissionService;

    @ApiOperation("查询表下属字段")
    @PostMapping("list/{tableId}")
    public R list(@PathVariable String tableId) {
        DatasetTableField datasetTableField = DatasetTableField.builder().build();
        datasetTableField.setTableId(tableId);
        List<DatasetTableField> fields = dataSetTableFieldsService.list(datasetTableField);
        return R.data(fields);
    }

    @ApiOperation("查询表下属字段")
    @PostMapping("listWithPermission/{tableId}")
    public List<DatasetTableField> listWithPermission(@PathVariable String tableId) {
        DatasetTableField datasetTableField = DatasetTableField.builder().build();
        datasetTableField.setTableId(tableId);
        List<DatasetTableField> fields = dataSetTableFieldsService.list(datasetTableField);
        List<String> desensitizationList = new ArrayList<>();
        fields = fields.stream().filter(item -> !desensitizationList.contains(item.getDataeaseName())).collect(Collectors.toList());
        return fields;
    }

    //管理权限，可以列出所有字段
    @ApiOperation("查询表下属字段")
    @PostMapping("listForPermissionSeting/{tableId}")
    public List<DatasetTableField> listForPermissionSeting(@PathVariable String tableId) {
        DatasetTableField datasetTableField = DatasetTableField.builder().build();
        datasetTableField.setTableId(tableId);
        List<DatasetTableField> fields = dataSetTableFieldsService.list(datasetTableField);
        return fields;
    }

    //管理权限，可以列出所有字段
    @ApiOperation("分组查询表下属字段")
    @PostMapping("listByDQ/{tableId}")
    public R listByDQ(@PathVariable String tableId) {
        DatasetTableField datasetTableField = DatasetTableField.builder().build();
        datasetTableField.setTableId(tableId);
        datasetTableField.setGroupType("d");
        List<DatasetTableField> dimensionList = dataSetTableFieldsService.list(datasetTableField);
        datasetTableField.setGroupType("q");
        List<DatasetTableField> quotaList = dataSetTableFieldsService.list(datasetTableField);
        DatasetTableFieldType datasetTableField4Type = new DatasetTableFieldType();
        //设置列权限 此处无需脱敏权限
        dimensionList = permissionService.filterColumnPermissions(dimensionList,null,tableId);
        quotaList = permissionService.filterColumnPermissions(quotaList,null,tableId);
        datasetTableField4Type.setDimensionList(dimensionList);
        datasetTableField4Type.setQuotaList(quotaList);
        return R.data(datasetTableField4Type);
    }

    @ApiOperation("批量更新")
    @PostMapping("batchEdit")
    public void batchEdit(@RequestBody List<DatasetTableField> list) {
        dataSetTableFieldsService.batchEdit(list);
    }

    @ApiOperation("保存")
    @PostMapping("save")
    public DatasetTableField save(@RequestBody DatasetTableField datasetTableField) {
        dataSetTableFieldsService.checkFieldName(datasetTableField);
        try {
            // 执行一次sql，确保数据集中所有字段均能正确执行
            DatasetTable datasetTable = dataSetTableService.get(datasetTableField.getTableId());
            DataSetTableRequest dataSetTableRequest = new DataSetTableRequest();
            BeanUtils.copyProperties(datasetTable, dataSetTableRequest);
            dataSetTableService.getPreviewData(dataSetTableRequest, 1, 1, Collections.singletonList(datasetTableField));
        } catch (Exception e) {
            BaseException.throwException(ResultCode.get("calc_field_error"));
        }
        return dataSetTableFieldsService.save(datasetTableField);
    }

    @ApiOperation("删除")
    @PostMapping("delete/{id}")
    public void delete(@PathVariable String id) {
        dataSetTableFieldsService.delete(id);
    }

}
