package com.stdc.visual.controller.auth;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.TreeSelect;
import com.stdc.visual.auth.entity.menu.dto.SysMenuDto;
import com.stdc.visual.auth.entity.menu.po.SysMenu;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/menu")
@ApiSupport(author = "dongbobo")
@Api(tags = "菜单权限管理")
@Slf4j
public class SysMenuController {

    @Autowired
    private ISysMenuService sysMenuService;

    @ApiOperation("菜单权限列表")
    @PostMapping("/list")
    public R list(@RequestBody SysMenu sysMenu) {
        List<SysMenuDto> list = sysMenuService.selectMenuList(sysMenu, AuthUtils.getUser().getUserId());
        return R.data(list);
    }

    @ApiOperation("查询菜单")
    @PostMapping("/get/{menuId}")
    public R get(@PathVariable String menuId) {
        SysMenu menu = sysMenuService.getById(menuId);
        return R.data(menu);
    }

    @ApiOperation("新增菜单")
    @PostMapping("/add")
    public R add(@RequestBody SysMenu menu) {
        sysMenuService.save(menu);
        return R.success("新增成功");
    }

    @ApiOperation("修改菜单")
    @PostMapping("/update")
    public R update(@RequestBody SysMenu menu) {
        sysMenuService.updateById(menu);
        return R.success("修改成功");
    }

    @ApiOperation("删除菜单")
    @PostMapping("/delete/{menuId}")
    public R delete(@PathVariable String menuId) {
        sysMenuService.removeById(menuId);
        return R.success("删除成功");
    }

    @ApiOperation("获取菜单下拉树列表")
    @PostMapping("/treeselect")
    public R treeselect(@RequestBody SysMenu menu) {
        List<SysMenuDto> menus = sysMenuService.selectMenuList(menu, AuthUtils.getUser().getUserId());
        List<TreeSelect> list = sysMenuService.buildMenuTreeSelect(menus);
        return R.data(list);
    }

    @ApiOperation("加载对应角色菜单列表树")
    @PostMapping("/roleMenuTreeselect/{roleId}")
    public R roleMenuTreeselect(@PathVariable("roleId") String roleId) {
        List<SysMenuDto> menus = sysMenuService.selectMenuList(AuthUtils.getUser().getUserId());
        Map<String, Object> map = new HashedMap();
        map.put("checkedKeys", sysMenuService.selectMenuListByRoleId(roleId));
        map.put("menus", sysMenuService.buildMenuTreeSelect(menus));
        return R.data(map);
    }

}
