package com.stdc.visual.auth.service.impl.own;

import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.dto.UserRoleDTO;
import com.stdc.visual.auth.entity.role.po.UserRolePO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.mapper.UserRoleMapper;
import com.stdc.visual.auth.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/30--18:55
 * @describe:
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.OWN, matchIfMissing = true)
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Boolean save(UserRoleDTO dto) {
        return userRoleMapper.save(dto) > 0 ? true : false;
    }

    @Override
    public Boolean delete(UserRoleDTO dto) {
        return userRoleMapper.deleteById(dto) > 0 ? true : false;
    }

    @Override
    public Boolean updateById(UserRoleDTO dto) {
        return userRoleMapper.updateById(dto) > 0 ? true : false;
    }

    @Override
    public List<UserRolePO> queryById(UserRoleDTO request) {
        return userRoleMapper.queryById(request);
    }

    @Override
    public List<UserRolePO> queryByUserId(String userId) {
        return userRoleMapper.queryByUserId(userId);
    }

    @Override
    public List<UserRolePO> queryAll() {
        return userRoleMapper.queryAll();
    }

    @Override
    public List<String> roles(Long userId) {
        return userRoleMapper.roleCodes(userId);
    }
}
