package com.stdc.visual.auth.service.impl.cas;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.core.http.util.RestTemplateUtil;
import com.stdc.visual.auth.entity.cas.dto.RemoteDto;
import com.stdc.visual.auth.entity.cas.po.UserPO;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.mapper.SysUserMapper;
import com.stdc.visual.auth.prop.sso.SsoConfigProperties;
import com.stdc.visual.auth.service.SysUserService;
import com.stdc.visual.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stdc.visual.auth.entity.user.AuthUtils.CAS_DEFAULT_PASSWORD;

/**
 * @author: wang_jie
 * @data: 2022/8/25--10:03
 * @describe: 统一权限用户表实现类
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.CAS, matchIfMissing = true)
public class SysUserCasServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SsoConfigProperties properties;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser queryOneByUserName(String username) {
        String roleUrl = properties.getRoleUrl();
        roleUrl = roleUrl.replace("{username}",username);
        RemoteDto remoteDto = JsonUtil.toJavaObj(RestTemplateUtil.doGet(roleUrl, null), RemoteDto.class);
        SysUser user = new SysUser();
        UserPO userPO = remoteDto.getData().getUser();
        user.setUserId(userPO.getId());
        user.setUsername(userPO.getUsername());
        user.setNickName(userPO.getChineseName());
        //cas权限时，设置默认占位密码,不做比对
        user.setPassword(CAS_DEFAULT_PASSWORD);
        return user;
    }

    @Override
    public String queryExtendByUserName(String userName) {
        return sysUserMapper.queryExtendByUserName(userName);
    }

    @Override
    public String queryUsernameByReleasePath(String path) {
        return sysUserMapper.queryUsernameByReleasePath(path);
    }

    @Override
    public SysUser queryOneByUserId(String userId) {
        return null;
    }

    @Override
    public List<SysUser> allUserS() {
        return null;
    }

    @Override
    public List<SysUserDTO> selectAllocatedList(SysUserDTO sysUser) {
        return null;
    }

    @Override
    public List<SysUserDTO> selectUnallocatedList(SysUserDTO sysUser) {
        return null;
    }
}
