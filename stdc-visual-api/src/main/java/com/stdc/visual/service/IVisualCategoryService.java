package com.stdc.visual.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.visual.entity.po.VisualCategory;
import com.stdc.visual.entity.vo.VisualCategoryTreeVO;

import java.util.List;
import java.util.Set;

/**
 * 可视化分类表 服务类
 *
 * @author wangjie
 */
public interface IVisualCategoryService extends IService<VisualCategory> {

    /***
     * 大屏模板分类value
     */
    Integer VISUAL_TEMPLATE_VALUE = 999999;

    /**
     * 修改分类信息
     *
     * @param visualCategory 分类
     * @return boolean
     */
    boolean updateCategory(VisualCategory visualCategory);

    /**
     * 保存分类信息
     *
     * @param visualCategory 分类
     * @return boolean
     */
    boolean saveCategory(VisualCategory visualCategory);

    /**
     * 保存AI驾驶舱分类信息
     *
     * @param visualCategory 分类
     * @return boolean
     */
    boolean saveAICategory(VisualCategory visualCategory);

    /**
     * 将 分类数组对象 处理为 分类数组树对象
     * @return
     */
    List<VisualCategoryTreeVO> getVisualCategoryTreeVO(List<VisualCategory> visualCategories);

    VisualCategory getByCategoryValue(String categoryValue);

    //根据ids获取目录和其子目录
    Set<VisualCategory> getAllCategoryValueByIds(List<Long> idS);
}
