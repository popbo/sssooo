package com.stdc.visual.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.visual.auth.entity.TreeSelect;
import com.stdc.visual.auth.entity.menu.dto.SysMenuDto;
import com.stdc.visual.auth.entity.menu.po.SysMenu;
import com.stdc.visual.auth.entity.menu.vo.RouterVo;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author dongbobo
 * @since 2025-06-19
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenuDto> selectMenuList(String userId);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenuDto> selectMenuList(SysMenu menu, String userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByUserId(String userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByRoleId(String roleId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param user
     * @return 菜单列表
     */
    public List<SysMenuDto> selectMenuTreeByUserId(SysUserDTO user);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<Long> selectMenuListByRoleId(String roleId);

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenuDto> buildMenuTree(List<SysMenuDto> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenuDto> menus);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenuDto> menus);


}
