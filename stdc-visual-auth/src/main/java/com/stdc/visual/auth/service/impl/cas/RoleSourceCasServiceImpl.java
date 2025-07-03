package com.stdc.visual.auth.service.impl.cas;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.role.po.RoleSourcePO;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.mapper.RoleSourceMapper;
import com.stdc.visual.auth.service.RoleSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/10/24--15:54
 * @describe: 统一认证-角色-用户关系实现类
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.CAS, matchIfMissing = true)
public class RoleSourceCasServiceImpl  implements RoleSourceService {

    @Autowired
    private RoleSourceMapper roleSourceMapper;

    @Override
    @Transactional
    public Boolean saveRoleSource(Object sourceId, SourceType sourceType) {
        //资源-资源类型
        RoleSourcePO roleSource = new RoleSourcePO();
        roleSource.setSourceId(String.valueOf(sourceId));
        roleSource.setSourceType(sourceType.getValue());
        //获取当前用户，当前用户角色
        SysUserDTO user = AuthUtils.getUser();
        List<RoleDTO> roles = user.getRoles();
        //遍历角色 新增 角色-资源
        for (RoleDTO role : roles) {
            roleSource.setRoleId(role.getRoleId());
            if (!saveRoleSource(roleSource)){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<RoleSourcePO> queryRoleSource(Object sourceId, SourceType sourceType) {
        List<RoleSourcePO> roleSourcePOS = new ArrayList<>();
        //资源-资源类型
        RoleSourcePO roleSource = new RoleSourcePO();
        if (ObjectUtil.isNotEmpty(sourceId)){
            roleSource.setSourceId(String.valueOf(sourceId));
        }
        roleSource.setSourceType(sourceType.getValue());
        //获取当前用户，当前用户角色
        SysUserDTO user = AuthUtils.getUser();
        List<RoleDTO> roles = user.getRoles();
        //遍历角色 新增 角色-资源
        for (RoleDTO role : roles) {
            roleSource.setRoleId(role.getRoleId());
            roleSourcePOS.addAll(queryRoleSource(roleSource));
        }
        return roleSourcePOS;
    }

    @Override
    public List<String> queryCurrentRoleSourceId(SourceType sourceType) {
        return queryRoleSource(null,sourceType).stream().map(RoleSourcePO::getSourceId).collect(Collectors.toList());
    }

    @Override
    public Boolean updateRoleSource(RoleSourcePO roleSource) {
        return null;
    }

    @Override
    public Boolean delRoleSource(Object sourceId, SourceType sourceType) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean saveRoleSource(RoleSourcePO roleSource) {
        roleSource.setId(StringUtil.randomUUID());
        return roleSourceMapper.insert(roleSource) > 0;
    }

//    @Transactional(rollbackFor = Exception.class)
    public List<RoleSourcePO> queryRoleSource(RoleSourcePO roleSource) {
        LambdaQueryWrapper<RoleSourcePO> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(roleSource.getId())){
            queryWrapper.eq(RoleSourcePO::getId,roleSource.getId());
        }
        if (ObjectUtil.isNotEmpty(roleSource.getRoleId())){
            queryWrapper.eq(RoleSourcePO::getRoleId,roleSource.getRoleId());
        }
        if (ObjectUtil.isNotEmpty(roleSource.getSourceId())){
            queryWrapper.eq(RoleSourcePO::getSourceId,roleSource.getSourceId());
        }
        if (ObjectUtil.isNotEmpty(roleSource.getSourceType())){
            queryWrapper.eq(RoleSourcePO::getSourceType,roleSource.getSourceType());
        }
        if (ObjectUtil.isNotEmpty(roleSource.getAuthType())){
            queryWrapper.eq(RoleSourcePO::getAuthType,roleSource.getAuthType());
        }
        return roleSourceMapper.selectList(queryWrapper);
    }
}
