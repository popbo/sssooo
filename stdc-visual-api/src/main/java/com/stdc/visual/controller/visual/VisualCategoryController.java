package com.stdc.visual.controller.visual;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.mp.utils.Condition;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.Func;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.service.ITopologyDataService;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.entity.po.Visual;
import com.stdc.visual.entity.po.VisualCategory;
import com.stdc.visual.entity.vo.VisualCategoryTreeVO;
import com.stdc.visual.service.IVisualCategoryService;
import com.stdc.visual.service.IVisualService;
import com.stdc.visual.util.PageData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.stdc.visual.service.IVisualCategoryService.VISUAL_TEMPLATE_VALUE;

/**
 * @author: wang_jie
 * @data: 2022/5/12--19:16
 * @describe: 可视化分类表 控制器
 */
@RestController
@AllArgsConstructor
@ApiSupport(author = "wangjie",order = 12)
@RequestMapping("/category")
@Api(tags = "大屏：大屏分类")
public class VisualCategoryController {

    private final IVisualService visualService;

    private final IVisualCategoryService visualCategoryService;

    private final RoleSourceService roleSourceService;

    private final ITopologyDataService topologyDataService;

    /**
     * 详情
     */
    @PostMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入visualCategory")
    public R<VisualCategory> detail(@RequestBody VisualCategory visualCategory) {
        VisualCategory detail = visualCategoryService.getOne(Condition.getQueryWrapper(visualCategory));
        return R.data(detail);
    }

    /**
     * 列表 全部
     */
    @PostMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表", notes = "传入visualCategory")
    public R<List<VisualCategoryTreeVO>> list(@RequestBody VisualCategory visualCategory) {
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.VISUAL_CATEGORY);
        if (CollectionUtils.isEmpty(ids)){
            return R.data(null);
        }
        QueryWrapper<VisualCategory> queryWrapper = Condition.getQueryWrapper(visualCategory);
        queryWrapper.ne("category_value",VISUAL_TEMPLATE_VALUE);
        queryWrapper.in("id",ids);
        //筛选可视化分类
        //queryWrapper.eq("category_source",0);
        List<VisualCategory> list = visualCategoryService.list(queryWrapper);
        //将 分类数组对象 处理为 分类数组树对象
        List<VisualCategoryTreeVO> visualCategoryTreeVOS = visualCategoryService.getVisualCategoryTreeVO(list);
        return R.data(visualCategoryTreeVOS);
    }

    /**
     * 分页 组态分类表
     */
    @PostMapping("/topology/list")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表-组态分类表", notes = "传入visualCategory")
    public R<List<VisualCategoryTreeVO>> topologyCategory(@RequestBody VisualCategory query) {
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.VISUAL_CATEGORY);
        if (CollectionUtils.isEmpty(ids)){
            return R.data(null);
        }
        QueryWrapper<VisualCategory> queryWrapper = Condition.getQueryWrapper(query);
        queryWrapper.ne("category_value",VISUAL_TEMPLATE_VALUE);
        queryWrapper.in("id",ids);
        //筛选组态分类
        queryWrapper.eq("category_source",1);
        List<VisualCategory> topologyCategoryS = visualCategoryService.list(queryWrapper);
        //将 分类数组对象 处理为 分类数组树对象
        List<VisualCategoryTreeVO> visualCategoryTreeVOS = visualCategoryService.getVisualCategoryTreeVO(topologyCategoryS);
        if (query.getParentId() != null && "0".equals(query.getParentId())) {
            return R.data(visualCategoryTreeVOS.get(0).getChildren());
        }
        return R.data(visualCategoryTreeVOS);
    }


    /**
     * 分页 可视化分类表
     */
    @PostMapping("/visual/list")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表-可视化分类表", notes = "传入visualCategory")
    public R<List<VisualCategoryTreeVO>> visualCategory(@RequestBody VisualCategory query) {
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.VISUAL_CATEGORY);
        if (CollectionUtils.isEmpty(ids)){
            return R.data(null);
        }
        QueryWrapper<VisualCategory> queryWrapper = Condition.getQueryWrapper(query);
        queryWrapper.ne("category_value",VISUAL_TEMPLATE_VALUE);
        queryWrapper.in("id",ids);
        //筛选可视化分类
        queryWrapper.eq("category_source",0);
        List<VisualCategory> topologyCategoryS = visualCategoryService.list(queryWrapper);
        //将 分类数组对象 处理为 分类数组树对象
        List<VisualCategoryTreeVO> visualCategoryTreeVOS = visualCategoryService.getVisualCategoryTreeVO(topologyCategoryS);
        if (query.getParentId() != null && "0".equals(query.getParentId())) {
            return R.data(visualCategoryTreeVOS.get(0).getChildren());
        }
        return R.data(visualCategoryTreeVOS);
    }

    /**
     * 分页查询可视化分类数据
     * 
     * @param visualCategory 查询条件对象，包含可视化分类的基本信息
     * @param query          分页查询参数，包含分页大小和当前页码
     * @return R<PageData>  返回封装了分页数据的响应对象
     */
    @PostMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页-可视化,组态", notes = "传入visualCategory")
    public R<PageData> page(@RequestBody VisualCategory visualCategory) {
//        if (ObjectUtil.isEmpty(visualCategory.getCategorySource())){
//            visualCategory.setCategorySource("0");
//        }
        // 获取当前角色的可视化分类权限ID列表
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.VISUAL_CATEGORY);
        // 如果没有权限ID，直接返回空数据
        if (CollectionUtils.isEmpty(ids)){
            return R.data(null);
        }
        // 构建查询条件
        QueryWrapper<VisualCategory> queryWrapper = Condition.getQueryWrapper(visualCategory);
        // 排除模板值
        queryWrapper.ne("category_value",VISUAL_TEMPLATE_VALUE);
//        queryWrapper.eq("category_source",visualCategory.getCategorySource());
        // 筛选当前用户有权限的分类
        queryWrapper.in("id",ids);
//        IPage<VisualCategory> pages = visualCategoryService.page(Condition.getPage(query),queryWrapper);

        // 获取符合条件的所有数据
        List<VisualCategory> visualCategories = visualCategoryService.list(queryWrapper);

        // 将分类数组对象处理为分类数组树对象
        List<VisualCategoryTreeVO> visualCategoryTreeVOS = visualCategoryService.getVisualCategoryTreeVO(visualCategories);

        // 封装成分页对象
        Integer pageSize = visualCategory.getSize() == null ? 10 : visualCategory.getSize();
        Integer pageNumber = visualCategory.getCurrent() == null ? 1 : visualCategory.getCurrent();
        PageData pagedData = PageData.getPagedData(visualCategoryTreeVOS, pageNumber, pageSize);
        return R.data(pagedData);
    }

    /**
     * 新增 可视化分类表
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入visualCategory")
    public R save(@Valid @RequestBody VisualCategory visualCategory) {
        if (ObjectUtil.isEmpty(visualCategory.getCategorySource())){
            visualCategory.setCategorySource("0");
        }
        // 判断是否存在
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.VISUAL_CATEGORY);
        if (!CollectionUtils.isEmpty(ids)){
            List<VisualCategory> isExit = visualCategoryService.list(new LambdaQueryWrapper<VisualCategory>()
                    .eq(VisualCategory::getCategoryKey, visualCategory.getCategoryKey())
                    .eq(VisualCategory::getCategorySource, visualCategory.getCategorySource())
                    .eq(VisualCategory::getProjectId, visualCategory.getProjectId())
                    .in(VisualCategory::getId,ids));
            if (!CollectionUtils.isEmpty(isExit)){
                return R.fail("分类名称已存在");
            }
        }
        //使用时间戳当做创建时间
        visualCategory.setCreateTime(System.currentTimeMillis());
        //设置root级别父级id
        if (StringUtil.isBlank(visualCategory.getParentId())){
            visualCategory.setParentId("0");
        }
        boolean save = visualCategoryService.saveCategory(visualCategory);
        if (!save){
            return R.status(false);
        }
        VisualCategory category = visualCategoryService.getOne(new LambdaQueryWrapper<VisualCategory>()
                .eq(VisualCategory::getCategoryKey, visualCategory.getCategoryKey())
                .eq(VisualCategory::getCategoryValue, visualCategory.getCategoryValue()));
        roleSourceService.saveRoleSource(category.getId(), SourceType.VISUAL_CATEGORY);
        return R.status(true);
    }

    /**
     * 修改 可视化分类表
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入visualCategory")
    public R update(@Valid @RequestBody VisualCategory visualCategory) {
        List<VisualCategory> isExit = visualCategoryService.list(new LambdaQueryWrapper<VisualCategory>()
                .eq(VisualCategory::getCategorySource, visualCategory.getCategorySource())
                .eq(VisualCategory::getCategoryKey, visualCategory.getCategoryKey()));
        if (!CollectionUtils.isEmpty(isExit)){
            return R.fail("分类名称已存在");
        }
        return R.status(visualCategoryService.updateCategory(visualCategory));
    }

    /**
     * 删除 可视化分类表
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        List<Long> idS = Func.toLongList(ids);
        //找到分类
        //替换为分类id
        //List<VisualCategory> visualCategories = visualCategoryService.listByIds(idS);
        //List<String> collect = visualCategories.stream().map(VisualCategory::getCategoryValue).collect(Collectors.toList());
        Set<VisualCategory> visualCategories = visualCategoryService.getAllCategoryValueByIds(idS);
        List<String> collect = visualCategories.stream().map(VisualCategory::getCategoryValue).collect(Collectors.toList());
        //查看分类是否绑定可视化id
        List<Visual> visuals = visualService.list(new QueryWrapper<Visual>().in("category", collect));
        //查看分类是否绑定组态id
        List<TopologyData> topologys = topologyDataService.list(new QueryWrapper<TopologyData>().in("category_value", collect));
        if (!CollectionUtils.isEmpty(visuals) || !CollectionUtils.isEmpty(topologys)) {
            return R.fail("当前分类下有大屏存在，删除失败");
        }
        return R.status(visualCategoryService.removeByIds(idS));
    }
}
