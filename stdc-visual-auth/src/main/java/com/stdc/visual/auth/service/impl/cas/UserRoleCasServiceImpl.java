package com.stdc.visual.auth.service.impl.cas;

import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.visual.auth.entity.role.dto.UserRoleDTO;
import com.stdc.visual.auth.entity.role.po.UserRolePO;
import com.stdc.visual.auth.service.UserRoleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/8/25--10:14
 * @describe:
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.CAS, matchIfMissing = true)
public class UserRoleCasServiceImpl implements UserRoleService {
    @Override
    public Boolean save(UserRoleDTO dto) {
        return null;
    }

    @Override
    public Boolean delete(UserRoleDTO dto) {
        return null;
    }

    @Override
    public Boolean updateById(UserRoleDTO dto) {
        return null;
    }

    @Override
    public List<UserRolePO> queryById(UserRoleDTO request) {
        return null;
    }

    @Override
    public List<UserRolePO> queryByUserId(String userId) {
        return null;
    }

    @Override
    public List<UserRolePO> queryAll() {
        return null;
    }

    @Override
    public List<String> roles(Long userId) {
        return null;
    }
}
