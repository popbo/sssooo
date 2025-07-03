package com.stdc.visual.controller.auth;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.role.dto.UserRoleDTO;
import com.stdc.visual.auth.entity.role.po.UserRolePO;
import com.stdc.visual.auth.service.UserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/user/role")
@ApiSupport(author = "dongbobo")
@Api(tags = "角色信息")
@Slf4j
public class SysUserRoleController {

    @Autowired
    private UserRoleService uesrRoleService;

    @ApiOperation("角色下添加用户")
    @PostMapping("/add")
    public R add(@RequestBody UserRoleDTO userRole) {
        uesrRoleService.save(userRole);
        return R.success("添加成功");
    }

    @ApiOperation("角色下删除用户")
    @PostMapping("/delete")
    public R delete(@RequestBody UserRoleDTO userRole) {
        uesrRoleService.delete(userRole);
        return R.success("删除成功");
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public R list(@RequestBody UserRoleDTO userRole) {
        List<UserRolePO> list = uesrRoleService.queryById(userRole);
        return R.data(list);
    }

}
