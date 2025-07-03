package com.stdc.visual.controller.visual;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.entity.po.VisualCategory;
import com.stdc.visual.service.IVisualCategoryService;
import com.stdc.visual.service.IVisualService;
import com.stdc.visual.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.stdc.visual.service.IVisualCategoryService.VISUAL_TEMPLATE_VALUE;

/**
 * @author: wang_jie
 * @data: 2024/8/7--16:27
 * @describe:
 */
@RestController
@AllArgsConstructor
@RequestMapping("/topology/3D")
@ApiSupport(author = "wangjie",order = 11)
@Api(tags = "提供给3D组态的接口:免权限")
public class TopologyController {

    private final IVisualService visualService;

    private final IVisualCategoryService visualCategoryService;

    private final RoleSourceService roleSourceService;




    /**
     * 分页 3D组态分类表
     */
    @GetMapping("/category/list")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "分页", notes = "3D组态分类")
    public R<List<VisualCategory>> topology3DCategory() {
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.VISUAL_CATEGORY);
        if (CollectionUtils.isEmpty(ids)){
            return R.data(null);
        }
        QueryWrapper<VisualCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("category_value",VISUAL_TEMPLATE_VALUE);
        queryWrapper.in("id",ids);
        //筛选3D组态分类
        queryWrapper.eq("category_source",2);
        List<VisualCategory> topologyCategoryS = visualCategoryService.list(queryWrapper);
        return R.data(topologyCategoryS);
    }




















}
