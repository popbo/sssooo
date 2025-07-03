package com.stdc.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.mp.utils.Condition;
import com.stdc.core.mp.utils.Query;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.project.entity.Project;
import com.stdc.project.entity.vo.*;
import com.stdc.project.service.IProjectService;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.common.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.stdc.core.oss.utils.FileUtil.getFileExtension;

/**
 * <p>
 * 工程管理
 * </p>
 *
 * @author dongbobo
 * @since 2025-06-23
 */
@RestController
@RequestMapping("/project")
@ApiSupport(author = "dongbobo")
@Api(tags = "工程管理")
@Slf4j
public class ProjectController {

    @Autowired
    private IProjectService projectService;

    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    @ApiOperation(value = "分页列表", notes = "分页列表")
    public R<IPage<Project>> list(@RequestBody ProjectQuery projectQuery, Query query) {
        //QueryWrapper<Project> queryWrapper = Condition.getQueryWrapper(projectQuery);
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", projectQuery.getName());
        IPage<Project> page = projectService.page(Condition.getPage(query), queryWrapper);
        return R.data(page);
    }

    @RequestMapping(value = "/get/{id}", method = {RequestMethod.GET})
    @ApiOperation(value = "获取详情", notes = "获取详情")
    public R<Project> get(@PathVariable String id) {
        Project project = projectService.getById(id);
        return R.data(project);
    }

    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ApiOperation(value = "新增", notes = "新增")
    public R add(@RequestBody ProjectAdd projectAdd) {
        Project project = new Project();
        BeanUtils.copyBean(project, projectAdd);
        project.setCreateBy(AuthUtils.getUser().getUsername());
        project.setCreateTime(new Date());
        projectService.save(project);
        return R.success("新增成功");
    }

    @RequestMapping(value = "/copy/{id}", method = {RequestMethod.POST})
    @ApiOperation(value = "复制", notes = "复制")
    public R copy(@PathVariable String id) throws Exception{
        projectService.copy(id);
        return R.success("复制成功");
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ApiOperation(value = "修改", notes = "修改")
    public R update(@RequestBody ProjectUpdate projectUpdate) {
        Project project = new Project();
        BeanUtils.copyBean(project, projectUpdate);
        project.setUpdateBy(AuthUtils.getUser().getUsername());
        project.setUpdateTime(new Date());
        projectService.updateById(project);
        return R.success("修改成功");
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE})
    @ApiOperation(value = "删除", notes = "删除")
    public R delete(@PathVariable String id) throws Exception{
        if (ObjectUtil.isEmpty(id)) {
            return R.fail("请求参数异常");
        }
        Project project = projectService.getById(id);
        if (project == null) {
            return R.fail("数据不存在");
        }
        if (project.getShared()) {
            return R.fail("已工程发布不允许删除");
        }
        projectService.deleteById(id);
        return R.success("删除成功");
    }

    @RequestMapping(value = "/batch/delete", method = {RequestMethod.DELETE})
    @ApiOperation(value = "批量删除", notes = "批量删除")
    public R batchDelete(@RequestBody ProjectDelete projectDelete) throws Exception{
        if (ObjectUtil.isEmpty(projectDelete.getIdList())) {
            return R.fail("请求参数异常");
        }
        Arrays.stream(projectDelete.getIdList()).forEach( id ->{
            try {
                this.delete(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return R.success("批量删除成功");
    }

    @RequestMapping(value = "/share", method = {RequestMethod.POST})
    @ApiOperation(value = "发布", notes = "发布")
    public R share(@RequestBody ProjectShare projectShare) throws Exception{
        projectService.share(projectShare);
        return R.success("发布成功");
    }

    @RequestMapping(value = "/batch/share", method = {RequestMethod.POST})
    @ApiOperation(value = "批量发布", notes = "批量发布")
    public R batchShare(@RequestBody ProjectShare projectShare) throws Exception{
        if (ObjectUtil.isEmpty(projectShare.getIdList())) {
            return R.fail("请求参数异常");
        }
        projectService.batchShare(projectShare);
        return R.success("批量发布成功");
    }

    @RequestMapping(value = "/statistics", method = {RequestMethod.GET})
    @ApiOperation(value = "工程统计", notes = "工程统计")
    public R statistics() {
        Map<String, Object> statistics = projectService.getStatistics();
        return R.data(statistics);
    }


    @ApiOperation("根据工程id导出")
    @PostMapping(value = "/export/{id}")
    public R export(@PathVariable String id) throws Exception {
        try {
            String zipPath = projectService.export(id);
            return R.data(zipPath);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导出失败");
    }

    @ApiOperation("批量导出")
    @PostMapping(value = "/batch/export")
    public R batchExport(@RequestBody ProjectDelete projectDelete) throws Exception {
        if (ObjectUtil.isEmpty(projectDelete.getIdList())) {
            return R.fail("请求参数异常");
        }
        try {
            List<String> idList = Arrays.asList(projectDelete.getIdList());
            String zipPaths = projectService.batchExport(idList);
            return R.data(zipPaths);
            /*List<String> zipPaths = new ArrayList<>();
            for (String id : projectDelete.getIdList()) {
                String zipPath = projectService.export(id);
                zipPaths.add(zipPath);
            }
            return R.data(String.join(";", zipPaths));*/
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导出失败");
    }

    @ApiOperation("工程导入")
    @PostMapping(value = "/import")
    public R importProject(@ApiParam(value = "上传文件") @RequestParam MultipartFile file) throws Exception {
        if (!StringUtil.equalsIgnoreCase("zip", getFileExtension(file.getOriginalFilename()))) {
            return R.fail("文件类型错误,请上传zip格式文件");
        }
        try {
            projectService.importProject(file);
            return R.success("导入成功");
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导入失败");
    }

}
