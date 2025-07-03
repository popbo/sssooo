package com.stdc.visual.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.core.tool.entity.constant.SystemConstant;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.BeanUtils;
import com.stdc.visual.entity.po.VisualCategory;
import com.stdc.visual.entity.vo.VisualCategoryTreeVO;
import com.stdc.visual.mapper.VisualCategoryMapper;
import com.stdc.visual.service.IVisualCategoryService;
import com.stdc.visual.service.IVisualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 可视化分类表 服务实现类
 *
 * @author wangjie
 */
@Service
public class VisualCategoryServiceImpl extends ServiceImpl<VisualCategoryMapper, VisualCategory> implements IVisualCategoryService {

//    @Autowired
//    private IVisualService visualService;

    @Autowired
    private RoleSourceService roleSourceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(VisualCategory visualCategory) {
        VisualCategory oldCategory = baseMapper.selectById(visualCategory.getId());
        visualCategory.setCategoryValue(oldCategory.getCategoryValue());
//        List<Visual> visualS = visualService.list(new LambdaQueryWrapper<Visual>().eq(Visual::getCategory, oldCategory.getCategoryValue()));
//        if (CollectionUtils.isNotEmpty(visualS)){
//            visualS.forEach(v->{
//                try {
//                    v.setCategory(Integer.valueOf(visualCategory.getCategoryValue()));
//                } catch (Exception e) {}
//                visualService.update(v,new LambdaQueryWrapper<Visual>().eq(Visual::getId,v.getId()));
//            });
//        }
//        LambdaQueryWrapper<VisualCategory> lqw = Wrappers.<VisualCategory>query().lambda().eq(VisualCategory::getCategoryValue, visualCategory.getCategoryValue());
//        Long cnt = baseMapper.selectCount((Func.isEmpty(visualCategory.getId())) ? lqw : lqw.notIn(VisualCategory::getId, visualCategory.getId()));
//        if (cnt > 0) {
//            BaseException.throwException(ResultCode.get("group_id_is_exits"));
//        }
        visualCategory.setIsDeleted(SystemConstant.DB_NOT_DELETED);
        return updateById(visualCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCategory(VisualCategory visualCategory) {
        visualCategory.setCategoryValue(StringUtil.randomUUID());
        visualCategory.setIsDeleted(SystemConstant.DB_NOT_DELETED);
        return save(visualCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAICategory(VisualCategory visualCategory){
        LambdaQueryWrapper<VisualCategory> queryWrapper = new LambdaQueryWrapper<VisualCategory>().eq(VisualCategory::getCategoryKey, visualCategory.getCategoryKey());
        VisualCategory AICategory = this.getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(AICategory)){
            return false;
        }
        visualCategory.setIsDeleted(SystemConstant.DB_NOT_DELETED);
        boolean save = save(visualCategory);
        if (save){
            VisualCategory category = this.getOne(new LambdaQueryWrapper<VisualCategory>()
                    .eq(VisualCategory::getCategoryKey, visualCategory.getCategoryKey())
                    .eq(VisualCategory::getCategoryValue, visualCategory.getCategoryValue()));
            roleSourceService.saveRoleSource(category.getId(), SourceType.VISUAL_CATEGORY);
        }
        return save;
    }

    @Override
    public List<VisualCategoryTreeVO> getVisualCategoryTreeVO(List<VisualCategory> visualCategories) {
        List<VisualCategoryTreeVO> ret = new ArrayList<>();
        //找出根节点对象
        List<VisualCategory> rootS = visualCategories.stream()
                .filter(v -> StringUtil.equals("0", v.getParentId()))
                //创建时间倒排序
                .sorted(Comparator.comparing(VisualCategory::getCreateTime).reversed())
                .collect(Collectors.toList());
        for (VisualCategory root : rootS) {
            ret.add(BeanUtils.copyBean(new VisualCategoryTreeVO(),root));
        }
        //根节点对象添加,子节点
        for (VisualCategoryTreeVO rootItem : ret) {
            //根节点对象初始化子节点
            handlerVisualCategoryTreeChildren(rootItem,visualCategories);
        }
        return ret;
    }

    @Override
    public VisualCategory getByCategoryValue(String categoryValue) {
        return baseMapper.getByCategoryValue(categoryValue);
    }

    @Override
    public Set<VisualCategory> getAllCategoryValueByIds(List<Long> idS) {
        Set<VisualCategory> set = new HashSet<>();
        idS.stream().forEach(id->{
            VisualCategory visualCategory = baseMapper.selectById(id);
            getCategory(visualCategory, set);
        });
        return set;
    }

    //递归获取当前查询目录的所有子目录
    private void getCategory(VisualCategory visualCategory, Set<VisualCategory> set) {
        if (visualCategory != null) {
            //添加当前查询目录
            set.add(visualCategory);
            LambdaQueryWrapper<VisualCategory> queryWrapper = new LambdaQueryWrapper<VisualCategory>();
            queryWrapper.eq(VisualCategory::getParentId, visualCategory.getId());
            List<VisualCategory> categoryList = baseMapper.selectList(queryWrapper);
            if (categoryList != null) {
                categoryList.forEach(bean -> {
                    set.add(bean);
                    getCategory(bean, set);
                });
            }
        }
    }

    //根节点对象初始化子节点
    private VisualCategoryTreeVO handlerVisualCategoryTreeChildren(VisualCategoryTreeVO root,List<VisualCategory> visualCategories){
        //获取子节点目录
        List<VisualCategoryTreeVO> childrenS = new ArrayList<>();
        for (VisualCategory visualCategory : visualCategories.stream()
                .filter(v -> StringUtil.equals(String.valueOf(root.getId()), v.getParentId()))
                .collect(Collectors.toList())) {
            childrenS.add(BeanUtils.copyBean(new VisualCategoryTreeVO(),visualCategory));
        }
        //创建时间倒排序
        childrenS = childrenS.stream().sorted(Comparator.comparing(VisualCategoryTreeVO::getCreateTime).reversed()).collect(Collectors.toList());
        //遍历children,子节点也初始化子节点
        for (VisualCategoryTreeVO children : childrenS) {
            handlerVisualCategoryTreeChildren(children,visualCategories);
        }
        //设置子节点
        root.setChildren(childrenS);
        return root;
    }

}
