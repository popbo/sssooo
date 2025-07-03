package com.stdc.visual.controller.dataset;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.permission.dto.ColumnPermissionDTO;
import com.stdc.visual.auth.entity.permission.dto.RowPermissionDTO;
import com.stdc.visual.auth.service.ColumnPermissionService;
import com.stdc.visual.auth.service.RowPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wang_jie
 * @data: 2022/8/29--9:59
 * @describe: 数据集 行/列  权限
 */
@Api(tags = "数据集：权限")
@ApiSupport(author = "wangjie",order = 4)
@RestController
@RequestMapping("/dataset/permission")
public class DataSetPermissionController {

    @Autowired
    private ColumnPermissionService columnPermissionService;

    @Autowired
    private RowPermissionService rowPermissionService;


    @ApiOperation("行权限:新增")
    @PostMapping("/row/save")
    public R save(@RequestBody RowPermissionDTO rowPermissionDTO) {
        return R.data(rowPermissionService.save(rowPermissionDTO));
    }

    @ApiOperation("行权限:更新")
    @PostMapping("/row/update")
    public R update(@RequestBody RowPermissionDTO rowPermissionDTO) {
        return R.data(rowPermissionService.update(rowPermissionDTO));
    }


    @ApiOperation("行权限:删除")
    @PostMapping("/row/del")
    public R del(@RequestBody RowPermissionDTO rowPermissionDTO) {
        return R.data(rowPermissionService.deleteById(rowPermissionDTO.getId()));
    }


    @ApiOperation("行权限:查询")
    @PostMapping("/row/query")
    public R query(@RequestBody RowPermissionDTO rowPermissionDTO) {
        return R.data(rowPermissionService.queryVO(rowPermissionDTO));
    }

    @ApiOperation("列权限:新增")
    @PostMapping("/column/save")
    public R save(@RequestBody ColumnPermissionDTO columnPermissionDTO) {
        return R.data(columnPermissionService.save(columnPermissionDTO));
    }

    @ApiOperation("列权限:更新")
    @PostMapping("/column/update")
    public R update(@RequestBody ColumnPermissionDTO columnPermissionDTO) {
        return R.data(columnPermissionService.update(columnPermissionDTO));
    }


    @ApiOperation("列权限:删除")
    @PostMapping("/column/del")
    public R del(@RequestBody ColumnPermissionDTO columnPermissionDTO) {
        return R.data(columnPermissionService.deleteById(columnPermissionDTO.getId()));
    }


    @ApiOperation("列权限:查询")
    @PostMapping("/column/query")
    public R query(@RequestBody ColumnPermissionDTO columnPermissionDTO) {
        return R.data(columnPermissionService.query(columnPermissionDTO));
    }




}
