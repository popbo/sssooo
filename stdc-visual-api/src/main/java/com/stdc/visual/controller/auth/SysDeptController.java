package com.stdc.visual.controller.auth;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.dept.dto.DeptDTO;
import com.stdc.visual.auth.entity.dept.po.DeptPO;
import com.stdc.visual.auth.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dept")
@ApiSupport(author = "dongbobo")
@Api(tags = "部门信息")
@Slf4j
public class SysDeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 新增部门
     */
    @ApiOperation("新增部门")
    @PostMapping("/add")
    public R add(@RequestBody DeptDTO dept) {
        deptService.save(dept);
        return R.success("新增成功");
    }

    /**
     * 修改部门
     */
    @ApiOperation("修改部门")
    @PostMapping("/update")
    public R update(@RequestBody DeptDTO dept) {
        deptService.update(dept);
        return R.success("修改成功");
    }

    /**
     * 删除部门
     */
    @ApiOperation("删除部门")
    @PostMapping("/delete/{deptId}")
    public R delete(@PathVariable String deptId) {
        deptService.deleteById(deptId);
        return R.success("删除成功");
    }

    /**
     * 部门列表
     */
    @ApiOperation("部门列表")
    @PostMapping("/list")
    public R list(@RequestBody DeptDTO dept) {
        List<DeptDTO> list = deptService.query(dept);
        return R.data(list);
    }

    /**
     * 查询部门
     */
    @ApiOperation("查询部门")
    @PostMapping("/get/{deptId}")
    public R get(@PathVariable String deptId) {
        DeptPO deptPO = deptService.queryDeptById(deptId);
        return R.data(deptPO);
    }

    /**
     * 获取部门树列表
     */
    @ApiOperation("获取部门树列表")
    @GetMapping("/deptTree")
    public R deptTree(DeptPO dept) {
        List<DeptDTO> deptDTOS = deptService.allDepts();
        return R.data(deptDTOS);
    }

}
