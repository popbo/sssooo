package com.stdc.topology2d.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.mp.utils.Condition;
import com.stdc.core.mp.utils.Query;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.topology2d.constants.ModelConstants;
import com.stdc.topology2d.constants.TopologyConstants;
import com.stdc.topology2d.entity.dto.TopologyDirDto;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.entity.po.TopologyDir;
import com.stdc.topology2d.entity.vo.TopologyDirEditorVo;
import com.stdc.topology2d.service.ITopologyDataService;
import com.stdc.topology2d.service.ITopologyDirService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.stdc.topology2d.constants.ModelConstants.*;


@RestController
@RequestMapping("/topology/dir")
@ApiSupport(author = "wangjie", order = 16)
@Api(tags = "组态:目录管理")
@Slf4j
public class TopologyDirController {

    @Autowired
    private ITopologyDirService topologyDirService;

    @Autowired
    private ITopologyDataService topologyDataService;


    @ApiOperation("获取目录列表")
    @PostMapping("/list")
    public R<IPage<TopologyDir>> list(@RequestBody TopologyDir topologyDir) throws Exception {
        Query query = new Query();
        query.setCurrent(0);
        query.setSize(1000);
        QueryWrapper<TopologyDir> queryWrapper = Condition.getQueryWrapper(topologyDir);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        //queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_USERNAME_PROPERTY, topologyDir.getUsername());
        //queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());
        //queryWrapper.like(ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY, topologyDir.getFullPath());
        IPage<TopologyDir> pages = topologyDirService.page(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }

    @ApiOperation("获取某目录下图纸列表")
    @PostMapping("/data/list")
    public R<IPage<TopologyData>> topo2dlist(@RequestBody TopologyDir topologyDir) throws Exception {
        Query query = new Query();
        query.setCurrent(0);
        query.setSize(1000);
        QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, topologyDir.getUsername());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_FOLDER_PROPERTY, topologyDir.getFullPath());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());
        IPage<TopologyData> pages = topologyDataService.page(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }

    @ApiOperation("添加目录")
    @PostMapping("/add")
    public R add(@RequestBody TopologyDir topologyDir) throws Exception {
        topologyDir.setType(TopologyConstants.TOPOLOGY_DATA_TYPE);
        topologyDirService.save(topologyDir);
        return R.data(topologyDir);
    }

    @ApiOperation("更新目录")
    @PostMapping("/update")
    public R update(@RequestBody TopologyDirEditorVo topologyDir) throws Exception {
        UpdateWrapper<TopologyDir> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_USERNAME_PROPERTY, topologyDir.getUsername());
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY, topologyDir.getOldFullpath());
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());

        updateWrapper.set(ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY, topologyDir.getNewFullpath());
        topologyDirService.update(updateWrapper);

        //修改文件后增加数据文件夹内图纸的更新
        UpdateWrapper<TopologyData> dataUpdateWrapper = new UpdateWrapper<>();
        dataUpdateWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        dataUpdateWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, topologyDir.getUsername());
        dataUpdateWrapper.eq(ModelConstants.TOPOLOGY_DATA_FOLDER_PROPERTY, topologyDir.getOldFullpath());
        dataUpdateWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());

        dataUpdateWrapper.set(ModelConstants.TOPOLOGY_DATA_FOLDER_PROPERTY, topologyDir.getNewFullpath());
        topologyDataService.update(dataUpdateWrapper);
        return R.success("更新成功");
    }

    @ApiOperation("删除目录")
    @PostMapping("/delete/{id}")
    public R delete(@PathVariable String id) throws Exception {
        topologyDirService.removeById(id);
        return R.success("删除成功");
    }

    @ApiOperation("根据主键id查询目录")
    @PostMapping("/get/{id}")
    public R<TopologyDir> get(@PathVariable String id) throws Exception {
        return R.data(topologyDirService.getById(id));
    }

    @ApiOperation("查询目录")
    @PostMapping("/folders/get")
    public R<List<String>> getFolders(@RequestBody TopologyDir topologyDir) throws Exception {
        QueryWrapper<TopologyDir> queryWrapper = Condition.getQueryWrapper(topologyDir);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        List<TopologyDir> topologyDirList = topologyDirService.list(queryWrapper);
        List<String> list = new ArrayList<>();
        if (ObjectUtil.isEmpty(topologyDirList)) {
            list.add("未分类");
        }
        topologyDirList.forEach(dir -> {
            list.add(dir.getFullPath());
        });
        return R.data(list);
    }

    @ApiOperation("根据文件夹名字返回少量字段数据")
    @PostMapping("/data/small/list")
    public R<IPage<TopologyData>> smallList(@RequestBody TopologyDirDto topologyDirDto) throws Exception {
        Query query = new Query();
        query.setCurrent(0);
        query.setSize(1000);
        QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, topologyDirDto.getUsername());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_FOLDER_PROPERTY, topologyDirDto.getFolder());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, topologyDirDto.getCategoryValue());
        if (!StringUtil.isBlank(topologyDirDto.getProjectId())) {
            queryWrapper.eq(TOPOLOGY_DIR_PROJECTID_PROPERTY, topologyDirDto.getProjectId());
        }

        List<String> queryFields = Arrays.asList(TOPOLOGY_DATA_ID_PROPERTY, TOPOLOGY_DATA_IMAGE_PROPERTY,
                TOPOLOGY_DATA_PASSWORD_PROPERTY, TOPOLOGY_DATA_NAME_PROPERTY);
        queryWrapper.select(String.join(",", queryFields));
        IPage<TopologyData> pages = topologyDataService.page(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }

    @ApiOperation("获取组件目录列表")
    @PostMapping("/components/list")
    public R<IPage<TopologyDir>> componentsList(@RequestBody TopologyDir topologyDir) {
        Query query = new Query();
        query.setCurrent(0);
        query.setSize(1000);
        QueryWrapper<TopologyDir> queryWrapper = Condition.getQueryWrapper(topologyDir);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY, TopologyConstants.TOPOLOGY_COMPONENTS_TYPE);
        IPage<TopologyDir> pages = topologyDirService.page(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }

    @ApiOperation("获取某组件目录下图纸列表")
    @PostMapping("/components/data/list")
    public R<IPage<TopologyData>> topo2dcomponentslist(@RequestBody TopologyData topologyData) {
        Query query = new Query();
        query.setCurrent(0);
        query.setSize(1000);
        QueryWrapper<TopologyData> queryWrapper = Condition.getQueryWrapper(topologyData);
        /*queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, topologyData.getCollection());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, topologyData.getUsername());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_FOLDER_PROPERTY, topologyData.getFolder());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, topologyData.getCategoryValue());*/
        IPage<TopologyData> pages = topologyDataService.page(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }

    @ApiOperation("添加组件目录")
    @PostMapping("/components/add")
    public R addComponents(@RequestBody TopologyDir topologyDir) {
        topologyDir.setType(TopologyConstants.TOPOLOGY_COMPONENTS_TYPE);
        topologyDirService.save(topologyDir);
        return R.data(topologyDir);
    }

    @ApiOperation("更新我的组件的目录")
    @PostMapping("/components/update")
    public R updateComponents(@RequestBody TopologyDirEditorVo topologyDir) throws Exception {
        UpdateWrapper<TopologyDir> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY, TopologyConstants.TOPOLOGY_COMPONENTS_TYPE);
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_USERNAME_PROPERTY, topologyDir.getUsername());
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY, topologyDir.getOldFullpath());
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());

        updateWrapper.set(ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY, topologyDir.getNewFullpath());
        topologyDirService.update(updateWrapper);
        return R.success("更新成功");
    }

    @ApiOperation("添加目录-可视化适配")
    @PostMapping("/topo2d/visual/dir/add")
    public R visualDirAdd(@RequestBody TopologyDir topologyDir) {
        topologyDir.setType(TopologyConstants.TOPOLOGY_DATA_TYPE);
        topologyDirService.save(topologyDir);
        return R.data(topologyDir);
    }


    @ApiOperation("新增模目录")
    @PostMapping("/template/add")
    public R addTemplate(@RequestBody TopologyDir topologyDir) throws Exception {
        topologyDir.setType(TopologyConstants.TOPOLOGY_TEMPLATE_TYPE);
        topologyDirService.save(topologyDir);
        return R.data(topologyDir);
    }

    @ApiOperation("更新模版目录")
    @PostMapping("/template/update")
    public R updateTemplate(@RequestBody TopologyDirEditorVo topologyDir) throws Exception {
        UpdateWrapper<TopologyDir> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY, TopologyConstants.TOPOLOGY_TEMPLATE_TYPE);
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_USERNAME_PROPERTY, topologyDir.getUsername());
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY, topologyDir.getOldFullpath());
        updateWrapper.eq(ModelConstants.TOPOLOGY_DIR_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());

        updateWrapper.set(ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY, topologyDir.getNewFullpath());
        topologyDirService.update(updateWrapper);
        return R.success("更新成功");
    }

    @ApiOperation("获取模板目录列表")
    @PostMapping("/template/list")
    public R<IPage<TopologyDir>> templatesList(@RequestBody TopologyDir topologyDir) {
        Query query = new Query();
        query.setCurrent(0);
        query.setSize(1000);
        QueryWrapper<TopologyDir> queryWrapper = Condition.getQueryWrapper(topologyDir);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY, TopologyConstants.TOPOLOGY_TEMPLATE_TYPE);
        //queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_USERNAME_PROPERTY, topologyDir.getUsername());
        //queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());
        //queryWrapper.like(ModelConstants.TOPOLOGY_DIR_FULLPATH_PROPERTY, topologyDir.getFullPath());
        IPage<TopologyDir> pages = topologyDirService.page(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }
}
