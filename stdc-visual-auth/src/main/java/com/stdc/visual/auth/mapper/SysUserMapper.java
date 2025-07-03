package com.stdc.visual.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/23--10:08
 * @describe: 用户表
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT `username` FROM `stdc_visual_v_release` WHERE `path` = #{path} AND `is_release` = 1")
    String queryUsernameByReleasePath(@Param("path")String path);

    @Select("SELECT `extend` FROM `stdc_visual_sys_user` WHERE `username` = #{userName}")
    String queryExtendByUserName(String userName);

    List<SysUserDTO> selectAllocatedList(@Param("sysUser") SysUserDTO sysUser);

    List<SysUserDTO> selectUnallocatedList(@Param("sysUser") SysUserDTO sysUser);

}
