package com.stdc.visual.auth.service.impl.own;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.mapper.SysUserMapper;
import com.stdc.visual.auth.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/8/15--10:49
 * @describe:
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.OWN, matchIfMissing = true)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;


    @Override
    public SysUser queryOneByUserName(String userName){
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,userName));
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
        return sysUserMapper.selectById(userId);
    }

    @Override
    public List<SysUser> allUserS() {
        List<SysUser> sysUsers = sysUserMapper.selectList(Wrappers.emptyWrapper());
        if (CollectionUtils.isEmpty(sysUsers)){
            return null;
        }
        return sysUsers.stream().map(s -> {
            SysUser user = new SysUser();
            user.setUserId(s.getUserId());
            user.setUsername(s.getUsername());
            return user;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SysUserDTO> selectAllocatedList(SysUserDTO sysUser) {
        List<SysUserDTO> list = sysUserMapper.selectAllocatedList(sysUser);
        return list;
    }

    @Override
    public List<SysUserDTO> selectUnallocatedList(SysUserDTO sysUser) {
        List<SysUserDTO> list = sysUserMapper.selectUnallocatedList(sysUser);
        return list;
    }
}
