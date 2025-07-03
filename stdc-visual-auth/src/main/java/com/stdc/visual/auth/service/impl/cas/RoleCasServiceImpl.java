package com.stdc.visual.auth.service.impl.cas;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.core.http.util.RestTemplateUtil;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.po.RolePO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.mapper.RoleMapper;
import com.stdc.visual.auth.prop.sso.SsoConfigProperties;
import com.stdc.visual.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: wang_jie
 * @data: 2022/8/25--10:19
 * @describe: 统一认证 角色 实现类
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.CAS, matchIfMissing = true)
public class RoleCasServiceImpl implements RoleService {

    @Autowired
    private SsoConfigProperties ssoConfigProperties;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Boolean save(RoleDTO dto) {
        return null;
    }

    @Override
    public Boolean delete(String roleId) {
        return null;
    }

    @Override
    public Boolean update(RoleDTO dto) {
        return null;
    }

    @Override
    public List<RolePO> query(RoleDTO request) {
        return null;
    }

    @Override
    public List<RoleDTO> queryByUser(SysUser user) {
        //根据名字-roles的base64获取当前用户的角色
        String jsonRole = RestTemplateUtil.doPost(ssoConfigProperties.getRoleUrl() + "?userName=" + user.getUsername());
//        String userRoleBase64 = Base64.getEncoder().encodeToString((user.getUsername()+"-roles").getBytes());
//        String jsonRole = casLettuceTemplate.getCacheString(userRoleBase64);
        if (!StringUtil.hasText(jsonRole)) return null;
        //获取角色数组
        JSONArray roleArry = JSONArray.parseArray(jsonRole);
        List<RoleDTO> roleDTOS = new ArrayList<>(roleArry.size());
        roleArry.forEach(d -> {
            JSONObject data = (JSONObject) d;
            RoleDTO rolePO = new RoleDTO();
            rolePO.setRoleId(data.getString("id"));
            rolePO.setName(data.getString("roleName"));
            roleDTOS.add(rolePO);
        });
        return roleDTOS;
    }

    @Override
    public RolePO queryByRoleId(String roleId) {
        return null;
    }

    @Override
    public List<RolePO> allRoles() {
        return null;
    }

    @Override
    public Set<String> selectRolePermissionByUserId(String userId) {
        List<RolePO> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (RolePO perm : perms) {
            if (ObjectUtil.isNotEmpty(perm)) {
                //permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

}
