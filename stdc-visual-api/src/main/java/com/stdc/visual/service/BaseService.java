package com.stdc.visual.service;

import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/12--18:06
 * @describe: 基础业务接口
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 逻辑删除
     *
     * @param ids id集合
     * @return
     */
    boolean deleteLogic(@NotEmpty List<Long> ids);

    /**
     * 变更状态
     *
     * @param ids    id集合
     * @param status 状态值
     * @return
     */
    boolean changeStatus(@NotEmpty List<Long> ids, Integer status);

}
