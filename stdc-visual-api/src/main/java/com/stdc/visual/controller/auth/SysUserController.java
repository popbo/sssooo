package com.stdc.visual.controller.auth;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.dept.dto.DeptDTO;
import com.stdc.visual.auth.entity.dept.po.DeptPO;
import com.stdc.visual.auth.service.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/user")
@ApiSupport(author = "dongbobo")
@Api(tags = "用户信息")
@Slf4j
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleSourceService roleSourceService;


    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public R deptTree(DeptPO dept) {
        List<DeptDTO> deptDTOS = deptService.allDepts();
        return R.data(deptDTOS);
    }

}
