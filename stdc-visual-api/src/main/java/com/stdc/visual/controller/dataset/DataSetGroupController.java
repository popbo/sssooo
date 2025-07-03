package com.stdc.visual.controller.dataset;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetGroupDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetGroup;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.request.DataSetGroupRequest;
import com.stdc.visual.dynamic.mapper.DatasetGroupMapper;
import com.stdc.visual.dynamic.mapper.DatasetTableMapper;
import com.stdc.visual.dynamic.service.DataSetGroupService;
import com.stdc.visual.entity.po.VisualConfig;
import com.stdc.visual.service.IVisualConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/20--10:48
 * @describe: 数据集：分组
 */
@Api(tags = "数据集：分组")
@ApiSupport(author = "wangjie",order = 4)
@RestController
@RequestMapping("dataset/group")
public class DataSetGroupController {
    @Resource
    private DataSetGroupService dataSetGroupService;

    @Autowired
    private DatasetGroupMapper datasetGroupMapper;

    @Autowired
    private DatasetTableMapper datasetTableMapper;

    @ApiOperation("保存")
    @PostMapping("/save")
    public R save(@RequestBody DatasetGroup datasetGroup) {
        return  R.data(dataSetGroupService.save(datasetGroup));
    }

    @ApiOperation("查询树")
    @PostMapping("/tree")
    public R tree(@RequestBody DataSetGroupRequest datasetGroup) {
        return  R.data(dataSetGroupService.tree(datasetGroup));
    }

    @ApiOperation("查询树节点")
    @PostMapping("/treeNode")
    public R treeNode(@RequestBody DataSetGroupRequest datasetGroup) {
        return R.data(dataSetGroupService.treeNode(datasetGroup));
    }

    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    public R tree(@PathVariable String id) throws Exception {
        LambdaQueryWrapper<DatasetTable> eq = new LambdaQueryWrapper<DatasetTable>().eq(DatasetTable::getSceneId, id);
        List<DatasetTable> datasetTables = datasetTableMapper.selectList(eq);
        if (CollectionUtils.isNotEmpty(datasetTables)){
            return R.fail("删除失败,该目录下有正在使用的数据集");
        }
        LambdaQueryWrapper<DatasetGroup> eq2 = new LambdaQueryWrapper<DatasetGroup>().eq(DatasetGroup::getPid, id);
        List<DatasetGroup> datasetGroups = datasetGroupMapper.selectList(eq2);
        if (CollectionUtils.isNotEmpty(datasetGroups)){
            return R.fail("删除失败,该目录下存在子目录");
        }
        dataSetGroupService.delete(id);
        return R.success("删除成功");
    }

    @ApiIgnore
    @PostMapping("/getScene/{id}")
    public R getScene(@PathVariable String id) {
        return R.data(dataSetGroupService.getScene(id));
    }


}
