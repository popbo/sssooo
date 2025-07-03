package com.stdc.visual.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/8/15--10:46
 * @describe: 用户表抽象类
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 通过用用户名查询用户信息
     * @return
     */
    SysUser queryOneByUserName(String userName);

    /**
     * 查询额外字段
     * @param userName
     * @return
     */
    String queryExtendByUserName(String userName);

    /**
     * 通过发布地址查询用户信息
     * @return
     */
    String queryUsernameByReleasePath(String userName);

    /**
     * 通过用户id查询用户信息
     * @return
     */
    SysUser queryOneByUserId(String userId);

    /**
     * 获取用户 userId 和 userName 键值对
     * @return
     */
    List<SysUser> allUserS();

    List<SysUserDTO> selectAllocatedList(SysUserDTO sysUser);

    List<SysUserDTO> selectUnallocatedList(SysUserDTO sysUser);

}
