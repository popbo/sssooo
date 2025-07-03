package com.stdc.visual.service.impl;

import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.entity.po.GesturePassword;
import com.stdc.visual.mapper.GesturePasswordMapper;
import com.stdc.visual.service.GesturePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: wang_jie
 * @data: 2023/1/3--15:31
 * @describe:
 */
@Service
public class GesturePasswordServiceImpl implements GesturePasswordService {

    @Autowired
    private GesturePasswordMapper gesturePasswordMapper;

    /**
     * 新增手势密码
     */
    @Override
    public boolean save(GesturePassword gesturePassword) {
        return gesturePasswordMapper.insert(gesturePassword) > 0;
    }

    /**
     * 验证手势密码
     */
    @Override
    public boolean validate(GesturePassword gesturePassword) {
        GesturePassword entity = gesturePasswordMapper.selectById(gesturePassword.getId());
        if (ObjectUtil.isNotEmpty(entity)){
            return StringUtil.equals(gesturePassword.getVPsw(),entity.getGtPsw());
        }
        return false;
    }

    /**
     * 通过旧手势密码，修改新手势密码
     */
    @Override
    public boolean updateGtPsw(Long id, String oldGtPsw, String newGtPsw) {
        GesturePassword entity = gesturePasswordMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(entity) && StringUtil.equals(oldGtPsw,entity.getGtPsw())){
            entity.setGtPsw(newGtPsw);
            return gesturePasswordMapper.updateById(entity) > 0;
        }
        return false;
    }

    /**
     * 通过旧验证密码，修改新验证密码
     */
    @Override
    public boolean updateVPsw(Long id, String oldVPsw, String newVPsw) {
        GesturePassword entity = gesturePasswordMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(entity) && StringUtil.equals(oldVPsw,entity.getVPsw())){
            entity.setVPsw(newVPsw);
            return gesturePasswordMapper.updateById(entity) > 0;
        }
        return false;
    }

    /**
     * 通过验证密码，修改手势密码
     */
    @Override
    public boolean updateGtPswByVPsw(Long id, String vPsw, String gtPsw) {
        GesturePassword entity = gesturePasswordMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(entity) && StringUtil.equals(vPsw,entity.getVPsw())){
            entity.setGtPsw(gtPsw);
            return gesturePasswordMapper.updateById(entity) > 0;
        }
        return false;
    }

    /**
     * 删除
     */
    @Override
    public boolean delete(Long id) {
        return gesturePasswordMapper.deleteById(id) > 0;
    }
}
