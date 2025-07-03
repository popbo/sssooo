package com.stdc.visual.controller.auth;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.annotation.AuthEnable;
import com.stdc.visual.auth.entity.menu.dto.SysMenuDto;
import com.stdc.visual.auth.entity.menu.vo.RouterVo;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.service.DeptService;
import com.stdc.visual.auth.service.ISysMenuService;
import com.stdc.visual.auth.service.RoleService;
import com.stdc.visual.auth.service.SysUserService;
import com.stdc.visual.dynamic.service.PermissionService;
import com.stdc.visual.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author: wang_jie
 * @data: 2022/5/23--11:08
 * @describe: 登录接口
 */
@Api(tags = "权限:")
@RestController
@RequestMapping("/auth")
@ApiSupport(author = "var code=ke.response.data.code; if(code==200){ var token=ke.response.data.data.token; ke.global.setHeader(\"Authorization\",token);} ",order = 1)
public class AuthController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private SysUserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ISysMenuService menuService;

    @AuthEnable
    @ApiOperation("用户登录")
    @PostMapping("/user/login")
    @ApiOperationSupport(includeParameters = {"username","password"})
    public R login(@ApiParam(value = "登录用户信息 ", required = true) @RequestBody SysUser user){
        return loginService.login(user);
    }

    @AuthEnable
    @ApiOperation("用户登录提供给3D组态")
    @PostMapping("/topology/3D/login")
    public R loginForTopology(@ApiParam(value = "登录用户名 ", required = true) String username){
        return loginService.loginForTopology(username);
    }

    @AuthEnable
    @ApiOperation("cas用户登录")
    @PostMapping("/cas/login")
    @ApiOperationSupport(includeParameters = {"username","password"})
    public R login(@ApiParam(value = "登录用户信息 ", required = true) @RequestBody Map<String,String> paramMap){
        String ticket = paramMap.get("ticket");
        return loginService.login(ticket);
    }

    @ApiOperation("token验证是否过期")
    @PostMapping("/check")
    public R check(@RequestBody Map<String,String> body){
        String token = body.get("token");
        return loginService.check(token);
    }

    @AuthEnable
    @ApiOperation("用户注销")
    @PostMapping("/user/logout")
    public R logout(){
        return loginService.logOut();
    }

    @AuthEnable
    @ApiOperation("通过刷新token获取新token")
    @PostMapping("/refresh-token")
    public R refreshToken(@ApiParam(value = "刷新token", required = true) @RequestBody String refreshToken ){
        return loginService.refreshToken(refreshToken);
    }

    @ApiOperation("获取所有角色")
    @GetMapping("/roles")
    public R getRoles(){
        return R.data(roleService.allRoles());
    }

    @ApiOperation("获取所有组织")
    @GetMapping("/depts")
    public R getDeptS(){
        return R.data(deptService.allDepts());
    }

    @ApiOperation("获取所有用户:userId、userName")
    @GetMapping("/users")
    public R getUsers(){
        return R.data(userService.allUserS());
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/getInfo")
    public R getInfo(){
        SysUserDTO user = AuthUtils.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        user.setRolesSet(roles);
        user.setPermissionsSet(permissions);

        return R.data(user);
    }

    @ApiOperation("获取路由信息")
    @GetMapping("/getRouters")
    public R getRouters(){
        SysUserDTO user = AuthUtils.getUser();
        List<SysMenuDto> menus = menuService.selectMenuTreeByUserId(user);
        List<RouterVo> list = menuService.buildMenus(menus);
        return R.data(list);
    }

}
