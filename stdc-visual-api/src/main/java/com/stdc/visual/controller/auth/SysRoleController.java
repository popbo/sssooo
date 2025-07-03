package com.stdc.visual.controller.auth;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.po.RolePO;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.service.RoleService;
import com.stdc.visual.auth.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/role")
@ApiSupport(author = "dongbobo")
@Api(tags = "角色信息")
@Slf4j
public class SysRoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private SysUserService userService;

    @ApiOperation("新增角色")
    @PostMapping("/add")
    public R add(@RequestBody RoleDTO role) {
        roleService.save(role);
        return R.success("新增成功");
    }

    @ApiOperation("修改角色")
    @PostMapping("/update")
    public R update(@RequestBody RoleDTO role) {
        roleService.update(role);
        return R.success("修改成功");
    }

    @ApiOperation("删除角色")
    @PostMapping("/delete/{roleId}")
    public R delete(@PathVariable String roleId) {
        roleService.delete(roleId);
        return R.success("删除成功");
    }

    @ApiOperation("角色列表")
    @PostMapping("/list")
    public R list(@RequestBody RoleDTO role) {
        List<RolePO> list = roleService.query(role);
        return R.data(list);
    }

    @ApiOperation("查询角色")
    @PostMapping("/get/{roleId}")
    public R get(@PathVariable String roleId) {
        RolePO role = roleService.queryByRoleId(roleId);
        return R.data(role);
    }

    /**
     * 查询已分配用户角色列表
     */
    @ApiOperation("查询已分配用户角色列表")
    @PostMapping("/allocatedList")
    public R allocatedList(@RequestBody SysUserDTO sysUser) {
        List<SysUserDTO> list = userService.selectAllocatedList(sysUser);
        return R.data(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    @ApiOperation("查询未分配用户角色列表")
    @PostMapping("/unallocatedList")
    public R unallocatedList(@RequestBody SysUserDTO sysUser) {
        List<SysUserDTO> list = userService.selectUnallocatedList(sysUser);
        return R.data(list);
    }

}
