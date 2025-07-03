package com.stdc.visual.controller.auth;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 角色和菜单关联表 前端控制器
 * </p>
 *
 * @author dongbobo
 * @since 2025-06-19
 */
@RestController
@RequestMapping("/system/roleMenu")
@ApiSupport(author = "dongbobo")
@Api(tags = "菜单权限管理")
@Slf4j
public class SysRoleMenuController {

}
