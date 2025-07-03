package com.stdc.visual.auth.service.impl.own;

import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.visual.auth.entity.user.CustomUserDetails;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.service.SysUserService;
import com.stdc.visual.common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author: wang_jie
 * @data: 2022/5/23--10:13
 * @describe: 实现UserDetailsService
 */

@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.OWN, matchIfMissing = true)
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //根据用户名查询用户信息
        SysUser user = sysUserService.queryOneByUserName(userName);
        // 如果查询不到数据就通过抛出异常来给出提示
        if(Objects.isNull(user)){
            BaseException.throwException(ResultCode.get("username_or_password_error"));
        }
        //TODO 根据用户查询权限信息 添加到LoginUser中
        SysUserDTO sysUserDTO = new SysUserDTO();
        BeanUtils.copyBean(sysUserDTO,user);
        //封装成UserDetails对象返回
        return new CustomUserDetails(sysUserDTO);
    }
}

