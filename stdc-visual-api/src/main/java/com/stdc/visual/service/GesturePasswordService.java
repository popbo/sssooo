package com.stdc.visual.service;

import com.stdc.visual.entity.po.GesturePassword;

/**
 * @author: wang_jie
 * @data: 2023/1/3--15:30
 * @describe:
 */
public interface GesturePasswordService {

    /**
     * 新增手势密码
     */
    boolean save(GesturePassword gesturePassword);

    /**
     * 验证手势密码
     */
    boolean validate(GesturePassword gesturePassword);

    /**
     * 通过旧手势密码，修改为新手势密码
     */
    boolean updateGtPsw(Long id,String oldGtPsw,String newGtPsw);

    /**
     * 通过旧验证密码，修改为新验证密码
     */
    boolean updateVPsw(Long id,String oldVPsw,String newVPsw);

    /**
     * 通过验证密码，修改手势密码
     */
    boolean updateGtPswByVPsw(Long id,String vPsw,String gtPsw);

    /**
     * 删除
     */
    boolean delete(Long id);

}
