package com.stdc.visual.auth.service.impl.own;

import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.po.RolePO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.mapper.RoleMapper;
import com.stdc.visual.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: wang_jie
 * @data: 2022/5/30--18:54
 * @describe: 增强包实现类
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.OWN, matchIfMissing = true)
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 保存角色
     * @param dto
     */
    @Override
    public Boolean save(RoleDTO dto) {
        long times = System.currentTimeMillis();
        dto.setCreateTime(times);
        dto.setUpdateTime(times);
        int insert = roleMapper.save(dto);
        return insert > 0 ? true : false ;
    }

    /**
     * 删除角色
     * @param roleId
     */
    @Override
    public Boolean delete(String roleId) {
        int del = roleMapper.deleteByPrimaryKey(roleId);
        return del > 0 ? true : false ;
    }

    /**
     * 更新角色
     * @param dto
     */
    @Override
    public Boolean update(RoleDTO dto) {
        dto.setUpdateTime(System.currentTimeMillis());
        int update = roleMapper.update(dto);
        return update > 0 ? true : false ;
    }

    /**
     * 查询角色
     * @param query
     * @return
     */
    @Override
    public List<RolePO> query(RoleDTO query) {
        List<RolePO> RoleS = roleMapper.query(query);
        return RoleS;
    }

    @Override
    public List<RoleDTO> queryByUser(SysUser user) {
        return null;
    }

    /**
     * 查询角色
     * @param roleId
     * @return
     */
    @Override
    public RolePO queryByRoleId(String roleId) {
        return roleMapper.queryByRoleId(roleId);
    }

    /**
     * 获取所有角色
     * @return
     */
    @Override
    public List<RolePO> allRoles() {
        return roleMapper.queryAll();
    }

    @Override
    public Set<String> selectRolePermissionByUserId(String userId) {
        List<RolePO> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (RolePO perm : perms) {
            if (ObjectUtil.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

}
