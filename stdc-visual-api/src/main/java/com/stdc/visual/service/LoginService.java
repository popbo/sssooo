package com.stdc.visual.service;

import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.user.po.SysUser;

/**
 * @author: wang_jie
 * @data: 2022/5/23--11:09
 * @describe: 登录
 */
public interface LoginService {

    /**
     * 登录接口
     * @param user
     * @return
     */
    R login(SysUser user);

    /**
     * 登录接口,提供给3D组态使用,通过用户名获取,无需验证密码
     * @param userName 用户名
     * @return
     */
    R loginForTopology(String userName);


    /**
     * cas登录接口
     * @param ticket
     * @return
     */
    R login(String ticket);

    /**
     * 登出接口
     * @return
     */
    R logOut();

    /**
     * 通过旧refresh-token获取新token和新refresh-token
     * @return
     */
    R refreshToken(String refreshToken);

    /**
     * 验证token是否过期
     * @param token
     * @return
     */
    R check(String token);
}
