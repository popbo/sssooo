package com.stdc.visual.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.visual.auth.entity.visual.VisualRelease;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/10/18--18:31
 * @describe: 大屏发布
 */
public interface IVisualReleaseService extends IService<VisualRelease> {

    /**
     * 新增大屏发布信息
     * @param release
     * @return 是否成功
     */
    Boolean releaseVisual(VisualRelease release);

    /**
     * 通过主键id删除大屏发布信息
     * @param release
     * @return 是否删除成功
     */
    Boolean delReleaseById(VisualRelease release);

    /**
     * 通过主键id删除大屏发布信息
     * @param id
     * @return 是否删除成功
     */
    Boolean delReleaseById(Long id);

    /**
     * 通过主键id更新大屏发布信息
     * @param release
     * @return 是否更新成功
     */
    Boolean updateReleaseById(VisualRelease release);

    /**
     * 查询所有大屏发布链接
     * @return 发布信息列表
     */
    List<VisualRelease> queryAll();

    /**
     * 通过 主键id 查询大屏信息
     * @param id
     * @return 大屏发布信息
     */
    VisualRelease queryOneById(String id);

    /**
     * 通过 发布地址 查询大屏信息
     * @param path
     * @return 大屏发布信息
     */
    VisualRelease queryOneByPath(String path, Integer source);

    /**
     * 通过大屏id查询发布信息
     * @param visualId
     * @return 大屏发布信息
     */
    VisualRelease queryOneByVisualId(Long visualId);

}
