package com.stdc.visual.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.visual.entity.po.BaseEntity;
import com.stdc.visual.service.BaseService;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/12--18:07
 * @describe: 业务封装基础类
 */
@Validated
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {


    @Override
    public boolean deleteLogic(@NotEmpty List<Long> ids) {
        return false;
    }

    @Override
    public boolean changeStatus(@NotEmpty List<Long> ids, Integer status) {
        return false;
    }
}
