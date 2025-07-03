package com.stdc.topology2d.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.mp.utils.Condition;
import com.stdc.core.mp.utils.Query;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.topology2d.constants.ModelConstants;
import com.stdc.topology2d.entity.dto.FolderInfo;
import com.stdc.topology2d.entity.dto.TopologyDataDto;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.entity.po.TopologyDir;
import com.stdc.topology2d.entity.vo.EngineeringGlobalSynchronizationFrom;
import com.stdc.topology2d.entity.vo.TopologyDataEditorVo;
import com.stdc.topology2d.entity.vo.TopologyDataQueryVo;
import com.stdc.topology2d.entity.vo.TopoloyDateJumpVo;
import com.stdc.topology2d.service.ITopologyDataService;
import com.stdc.topology2d.service.ITopologyDirService;
import com.stdc.visual.auth.annotation.AuthEnable;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.common.utils.CommonThreadPool;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.service.IVisualReleaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.stdc.core.oss.utils.FileUtil.getFileExtension;
import static com.stdc.topology2d.constants.ModelConstants.*;
import static com.stdc.topology2d.constants.TopologyConstants.*;

@RestController
@RequestMapping("/topology/data")
@ApiSupport(author = "wangjie", order = 16)
@Api(tags = "组态:组态详情")
@Slf4j
public class TopologyDataController {

    @Value("${oss.minio.enable}")
    private boolean minioEnable;
    @Value("${oss.minio.endpoint}")
    private String minioEndpoint;
    @Value("${oss.minio.stdc-topology-bucket}")
    private String minioTopologyBucket;

    @Autowired
    private ITopologyDataService topologyDataService;

    @Autowired
    private ITopologyDirService topologyDirService;

    @Autowired
    private IVisualReleaseService visualReleaseService;

    @Autowired
    private CommonThreadPool commonThreadPool;

    private static List<String> selectFields = Arrays.asList(TOPOLOGY_DATA_ID_PROPERTY, TOPOLOGY_DATA_NAME_PROPERTY,
            TOPOLOGY_DATA_SHARED_PROPERTY, TOPOLOGY_DATA_VERSION_PROPERTY, TOPOLOGY_DATA_SHAREDURL_PROPERTY, TOPOLOGY_DATA_PATH_PROPERTY,
            TOPOLOGY_DATA_SHAREDCUSTOM_PROPERTY, TOPOLOGY_DATA_SHAREDENCRYPTION_PROPERTY, TOPOLOGY_DATA_CATEGORYKEY_PROPERTY,
            TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, TOPOLOGY_DATA_MODULETYPE_PROPERTY, TOPOLOGY_DATA_FOLDER_PROPERTY,
            TOPOLOGY_DATA_PASSWORD_PROPERTY, TOPOLOGY_DATA_CREATETIME_PROPERTY, TOPOLOGY_DATA_UPDATETIME_PROPERTY,
            TOPOLOGY_DATA_SHAREDPASSWORD_PROPERTY, TOPOLOGY_DATA_SHAREDIMAGE_PROPERTY,
            TOPOLOGY_DATA_VISUALTYPE_PROPERTY, TOPOLOGY_DATA_IMAGE_PROPERTY, TOPOLOGY_DATA_PROJECTID_PROPERTY);

    @ApiOperation("组态大屏编辑")
    @RequestMapping(value = "/editor", method = {RequestMethod.POST, RequestMethod.GET})
    public R<TopologyData> editor(@RequestParam(value = "id") String id) {
        //id为空
        if (StringUtils.isEmpty(id)) {
            log.error("请求id为空");
            return R.fail("请求id为空");
        }
        QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(TOPOLOGY_DATA_ID_PROPERTY,
                ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY,
                ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY,
                ModelConstants.TOPOLOGY_DATA_NAME_PROPERTY);
        queryWrapper.eq(TOPOLOGY_DATA_ID_PROPERTY, id);
        TopologyData topologyData = topologyDataService.getOne(queryWrapper);
        if (topologyData == null) {
            return R.fail("获取信息失败");
        }
        TopologyDataEditorVo topologyDataEditorVo = new TopologyDataEditorVo();
        topologyDataEditorVo.setId(topologyData.getId());
        topologyDataEditorVo.setR(System.currentTimeMillis());
        topologyDataEditorVo.setComponent("topo2d-components".equals(topologyData.getCollection()) ? true : false);
        topologyDataEditorVo.setVisualizationEdit(true);
        topologyDataEditorVo.setUsername(topologyData.getUsername());
        topologyDataEditorVo.setHeadtitle(topologyData.getName());
        return R.data(topologyData);
    }

    @ApiOperation("组态大屏详情获取")
    @RequestMapping(value = "/{collection}/get", method = {RequestMethod.POST})
    public R get(@RequestBody TopologyDataDto topologyDataDto) {
        //id为空
        if (topologyDataDto == null || topologyDataDto.getId() == null || StringUtils.isEmpty(topologyDataDto.getId().toString())) {
            log.error("请求id为空");
            return R.fail("请求id为空");
        }
        TopologyData topologyData = topologyDataService.getById(topologyDataDto.getId());
        if (topologyData == null) {
            return R.fail("获取信息失败");
        }
        return R.data(JsonUtil.parseObject(topologyData.getData()));
    }

    @ApiOperation("组态大屏列表获取")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<IPage<TopologyData>> visualList(@RequestBody TopologyDataDto topologyDataDto, Query query) throws Exception {
        QueryWrapper<TopologyData> queryWrapper = Condition.getQueryWrapper(topologyDataDto);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TOPOLOGY_DATA_TYPE);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, AuthUtils.getUser().getUsername());
        if (!StringUtils.isEmpty(topologyDataDto.getCategoryValue())) {
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, topologyDataDto.getCategoryValue());
        }
        if (!StringUtils.isEmpty(topologyDataDto.getName())) {
            queryWrapper.like(ModelConstants.TOPOLOGY_DATA_NAME_PROPERTY, topologyDataDto.getName());
        }
        queryWrapper.select(String.join(",", selectFields));
        IPage<TopologyData> pages = topologyDataService.page(Condition.getPage(query), queryWrapper);
        if (pages != null && pages.getRecords() != null && !pages.getRecords().isEmpty() && minioEnable) {
            String minioUrl = (minioEndpoint.endsWith("/") ? minioEndpoint : minioEndpoint + "/") + minioTopologyBucket + "/thumbnail/";
            pages.getRecords().forEach(item -> {
                item.setImage(minioUrl + item.getImage().substring(item.getImage().lastIndexOf("/") + 1));
            });
        }
        return R.data(pages);
    }

    @ApiOperation("新增大屏/自定义组件")
    @RequestMapping(value = "/{collection}/add", method = RequestMethod.POST)
    public R<Map> add(@PathVariable("collection") String collection, @RequestBody Map data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }
        // 保存数据(大屏/自定义组件)
        try {
            data.put("collection", collection);
            Map map = topologyDataService.saveByData(data);
            return R.data(map);
        } catch (Exception e) {
            log.error("保存大屏数据失败:{}", e);
            return R.fail("保存大屏数据失败:" + e.getMessage());
        }
    }

    @ApiOperation("编辑大屏信息")
    @RequestMapping(value = "/{collection}/visual/update", method = RequestMethod.POST)
    public R visualUpdate(@PathVariable("collection") String collection, @RequestBody Map<String, Object> data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }
        // 更新数据(大屏/自定义组件)
        try {
            data.put("collection", collection);
            Map<String, Object> map = topologyDataService.visualUpdate(data);
            return R.data(map);
        } catch (Exception e) {
            log.error("编辑大屏信息失败:{}", e);
            return R.fail("编辑大屏信息失败:" + e.getMessage());
        }
    }
    @ApiOperation("更新大屏/自定义组件")
    @RequestMapping(value = "/{collection}/update", method = RequestMethod.POST)
    public R<Map> update(@PathVariable("collection") String collection, @RequestBody Map<String, Object> data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }
        // 更新数据(大屏/自定义组件)
        try {
            data.put("collection", collection);
            Map<String, Object> map = topologyDataService.saveByData(data);

            return R.data(map);
        } catch (Exception e) {
            log.error("更新大屏数据失败:{}", e);
            return R.fail("更新大屏数据失败:" + e.getMessage());
        }
    }

    @ApiOperation("获取大屏/自定义组件列表")
    @RequestMapping(value = "/{collection}/list", method = RequestMethod.POST)
    public R list(@PathVariable("collection") String collection, @RequestBody TopologyDataQueryVo data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }

        try {
            Query query = new Query();
            query.setCurrent(data.getCurrent());
            query.setSize(data.getPageSize());
            QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
            if (!StringUtil.isBlank(data.getSortFlag())) {
                if ("1".equals(data.getSortFlag())) {
                    queryWrapper.orderByDesc(ModelConstants.TOPOLOGY_DATA_CREATETIME_PROPERTY);
                } else if ("2".equals(data.getSortFlag())) {
                    queryWrapper.orderByDesc(ModelConstants.TOPOLOGY_DATA_UPDATETIME_PROPERTY);
                }
            } else {
                queryWrapper.orderByDesc(ModelConstants.TOPOLOGY_DATA_CREATETIME_PROPERTY, ModelConstants.TOPOLOGY_DATA_UPDATETIME_PROPERTY);
            }
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, collection);
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_PROJECTID_PROPERTY, data.getProjectId());
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, data.getUsername());
            if (!StringUtils.isEmpty(data.getName())) {
                queryWrapper.like(ModelConstants.TOPOLOGY_DATA_NAME_PROPERTY, data.getName());
            }
            //topo2d
            if (TOPOLOGY_DATA_TYPE.equals(collection) || TOPOLOGY_TEMPLATE_TYPE.equals(collection)) {
                /*if (!StringUtils.isEmpty(data.getName())) {
                    queryWrapper.like(ModelConstants.TOPOLOGY_DATA_NAME_PROPERTY, data.getName());
                }
                queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, data.getCategoryValue());
                IPage<TopologyData> pages = topologyDataService.page(Condition.getPage(query), queryWrapper);*/
                queryWrapper.select(String.join(",", selectFields));
                //查询当前目录和子目录下所有组态大屏
                IPage<TopologyData> pages = topologyDataService.getAllList(data, queryWrapper);
                return R.data(pages);
            } else if (TOPOLOGY_COMPONENTS_TYPE.equals(collection)) {
                //topo2d-components
                List<String> queryFields = Arrays.asList(TOPOLOGY_DATA_DATA_PROPERTY);
                if (!StringUtil.isBlank(data.getCategoryValue())) {
                    queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, data.getCategoryValue());
                }

                queryWrapper.select(String.join(",", queryFields));
                IPage<TopologyData> pages = topologyDataService.page(Condition.getPage(query), queryWrapper);
                List<JSONObject> list = new ArrayList<>();
                pages.getRecords().stream().forEach(item -> {
                    JSONObject jsonObject = JsonUtil.parseObject(item.getData());
                    list.add(jsonObject);
                });
                return R.data(list);
            }
            return R.data(null);
        } catch (Exception e) {
            log.error("获取大屏/组件列表失败:{}", e);
            return R.fail("获取大屏/组件数据失败:" + e.getMessage());
        }
    }

    @ApiOperation("获取大屏不分页列表")
    @RequestMapping(value = "/nopage/list", method = RequestMethod.POST)
    public R noPageList(@RequestBody TopologyDataQueryVo data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }
        try {
            QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
            if (!StringUtil.isBlank(data.getSortFlag())) {
                if ("1".equals(data.getSortFlag())) {
                    queryWrapper.orderByDesc(ModelConstants.TOPOLOGY_DATA_CREATETIME_PROPERTY);
                } else if ("2".equals(data.getSortFlag())) {
                    queryWrapper.orderByDesc(ModelConstants.TOPOLOGY_DATA_UPDATETIME_PROPERTY);
                }
            } else {
                queryWrapper.orderByDesc(ModelConstants.TOPOLOGY_DATA_CREATETIME_PROPERTY, ModelConstants.TOPOLOGY_DATA_UPDATETIME_PROPERTY);
            }
            if (!StringUtils.isEmpty(data.getCategoryValue())) {
                queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, data.getCategoryValue());
            }
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TOPOLOGY_DATA_TYPE);
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, data.getUsername());
            if (!StringUtils.isEmpty(data.getName())) {
                queryWrapper.like(ModelConstants.TOPOLOGY_DATA_NAME_PROPERTY, data.getName());
            }
            queryWrapper.select(String.join(",", selectFields));
            List<TopologyData> list = topologyDataService.list(queryWrapper);
            return R.data(list);
        } catch (Exception e) {
            log.error("获取大屏不分页列表失败:{}", e);
            return R.fail("获取大屏不分页列表失败:" + e.getMessage());
        }
    }

    @ApiOperation("获取图纸列表(预览专用)")
    @RequestMapping(value = "/preview/folders/list", method = RequestMethod.POST)
    public R previewFoldersList(@RequestBody TopologyDir topologyDir) throws Exception {
        if (null == topologyDir) {
            return R.fail("数据为空");
        }
        try {
            List<FolderInfo> resList = new ArrayList<>();
            QueryWrapper<TopologyDir> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_TYPE_PROPERTY, topologyDir.getType());
            queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_USERNAME_PROPERTY, topologyDir.getUsername());
            queryWrapper.eq(ModelConstants.TOPOLOGY_DIR_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());
            List<TopologyDir> dirList = topologyDirService.list(queryWrapper);
            for (TopologyDir dir : dirList) {
                QueryWrapper<TopologyData> dataQueryWrapper = new QueryWrapper<>();
                dataQueryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, topologyDir.getType());
                dataQueryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, topologyDir.getUsername());
                dataQueryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, topologyDir.getCategoryValue());
                dataQueryWrapper.eq(ModelConstants.TOPOLOGY_DATA_FOLDER_PROPERTY, dir.getFullPath());
                List<TopologyData> dataList = topologyDataService.list(dataQueryWrapper);
                List<Map<String, Object>> list = new ArrayList<>();
                for (TopologyData data : dataList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", data.getId());
                    map.put("name", data.getName());
                    map.put("image", data.getImage());
                    list.add(map);
                }
                FolderInfo folderInfo = new FolderInfo();
                folderInfo.setId(dir.getId());
                folderInfo.setType(dir.getType());
                folderInfo.setName(dir.getFullPath());
                folderInfo.setList(list);
                resList.add(folderInfo);
            }

            return R.data(resList);
        } catch (Exception e) {
            log.error("获取图纸列表(预览专用)失败:{}", e);
            return R.fail("获取图纸列表(预览专用)失败:" + e.getMessage());
        }
    }

    @ApiOperation("批量删除大屏/自定义组件列表")
    @RequestMapping(value = "/{collection}/delete", method = RequestMethod.POST)
    public R<Boolean> delete(@PathVariable("collection") String collection, @RequestBody TopologyDataDto data) throws Exception {
        String[] idList = data.getIdList();
        if (null == idList) {
            return R.fail("数据为空");
        }
        // 更新数据(大屏/自定义组件)
        try {
            //data.setCollection(collection);
            Integer successCount = 0; //删除成功数
            for (String id : idList) {
                if (collection.equals(TOPOLOGY_DATA_TYPE)) {
                    TopologyData base = topologyDataService.getById(id);
                    if (base == null) {
                        continue;
                    }
                    if (base.getShared() == null || !base.getShared()) { //发布状态下不支持删除
                        topologyDataService.removeById(id);
                        successCount++;
                    }
                } else if (collection.equals(TOPOLOGY_COMPONENTS_TYPE)) {
                    topologyDataService.removeById(id);
                    successCount++;
                }
            }
            return R.success("总计"+idList.length +"条数据,"+"成功删除"+successCount+"条数据");
        } catch (Exception e) {
            log.error("删除大屏数据失败:{}", e);
            return R.fail("删除大屏数据失败:" + e.getMessage());
        }
    }

    @ApiOperation("根据id删除大屏/自定义组件")
    @RequestMapping(value = "/{collection}/delete/{id}", method = RequestMethod.POST)
    public R<Boolean> deleteById(@PathVariable("collection") String collection, @PathVariable("id") String id) throws Exception {
        try {
            TopologyData topologyData = topologyDataService.getById(id);
            if (topologyData.getShared() != null && topologyData.getShared()) { //发布状态下不支持删除
                return R.fail("已发布不能删除");
            }
            topologyDataService.removeById(id);
            return R.success("删除成功");
        } catch (Exception e) {
            log.error("删除大屏数据失败:{}", e);
            return R.fail("删除大屏数据失败:" + e.getMessage());
        }
    }

    @ApiOperation("组态大屏发布")
    @PostMapping(value = "/{collection}/share")
    public R share(@RequestBody TopologyDataDto topologyDataDto) throws Exception {
        //id为空
        if (topologyDataDto == null || topologyDataDto.getId() == null || StringUtils.isEmpty(topologyDataDto.getId().toString())) {
            log.error("请求id为空");
            return R.fail("请求id为空");
        }
        topologyDataService.share(topologyDataDto);
        return R.success("发布成功");
    }

    @ApiOperation("组态大屏批量发布")
    @PostMapping(value = "/{collection}/batch/share")
    public R batchShare(@RequestBody TopologyDataDto topologyDataDto) throws Exception {
        String[] idList = topologyDataDto.getIdList();
        if (null == idList) {
            return R.fail("数据为空");
        }
        try {
            topologyDataService.batchShare(Arrays.asList(idList), topologyDataDto.getSharedUrlPrefix());
        } catch (Exception e) {
            log.error("组态大屏批量发布失败:{}", e);
            return R.fail("组态大屏批量发布失败:" + e.getMessage());
        }
        return R.success("发布成功");
    }

    @ApiOperation("通过path获取大屏信息")
    @PostMapping(value = "/{collection}/getByPath")
    @AuthEnable
    public R getByPath(@PathVariable("collection") String collection, @RequestBody TopologyDataDto topologyDataDto) throws Exception {
        //id为空
        if (topologyDataDto == null || topologyDataDto.getPath() == null || StringUtils.isEmpty(topologyDataDto.getPath().toString())) {
            log.error("请求参数path为空");
            return R.fail("请求参数path为空");
        }
        /*QueryWrapper<TopologyData> dataQueryWrapper = new QueryWrapper<>();
        dataQueryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, collection);
        dataQueryWrapper.eq(ModelConstants.TOPOLOGY_DATA_PATH_PROPERTY, topologyDataDto.getPath());
        TopologyData topologyData = topologyDataService.getOne(dataQueryWrapper);
        if (topologyData == null) {
            return R.fail("获取信息失败");
        }
        return R.data(JsonUtil.parseObject(topologyData.getData()));*/

        LambdaQueryWrapper<VisualRelease> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VisualRelease::getPath, topologyDataDto.getPath());
        queryWrapper.eq(VisualRelease::getSource, 1);
        VisualRelease visualRelease = visualReleaseService.getOne(queryWrapper);
        if (visualRelease == null) {
            return R.fail(ResultCode.get("this_release_path_is_not_exit"));
        }
        if (visualRelease.getIsRelease() == 0) {
            return R.fail(ResultCode.get("this_release_path_is_not_release"));
        }
        return R.data(JsonUtil.parseObject(visualRelease.getComponent()));
    }

    @ApiOperation("复制图纸/自定义组件")
    @PostMapping(value = "/{collection}/copyfile")
    public R copyfile(@PathVariable("collection") String collection, @RequestBody TopologyData query) throws Exception {
        TopologyData topologyData = topologyDataService.getById(query.getId());
        if (null == topologyData) {
            return R.fail("数据为空");
        }
        topologyData.setCollection(collection);

        topologyDataService.copyfile(topologyData);
        return R.success("复制成功");
    }

    @ApiOperation("组态导出")
    @PostMapping(value = "/export/{id}")
    public R export(@PathVariable String id) throws Exception {
        try {
            //return R.data(topologyDataService.export(Long.valueOf(id)));
            return R.data(this.fullExport(id));
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导出失败");
    }

    @ApiOperation("组态完整导出")
    @PostMapping(value = "/full/export/{id}")
    public R fullExport(@PathVariable String id) throws Exception {
        try {
            return R.data(topologyDataService.fullExport(id));
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导出失败");
    }

    @ApiOperation("导出菜单下所有组态大屏")
    @PostMapping(value = "/dir/export")
    public R dirExport(@RequestBody TopologyData query) throws Exception {
        try {
            String zipPath = topologyDataService.dirExportAll(query.getCategoryValue());
            //String zipPath = topologyDataService.dirExport(query.getCategoryValue());
            return R.data(zipPath);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导出失败");
    }

    @ApiOperation("组态导入")
    @PostMapping(value = "/import/{category}/{name}")
    public R topo2dImport(@ApiParam(value = "上传文件") @RequestParam MultipartFile file,
                          @ApiParam(value = "大屏分类") @PathVariable("category") String category,
                          @ApiParam(value = "大屏名称") @PathVariable("name") String name) throws Exception {
        if (!StringUtil.equalsIgnoreCase("zip", getFileExtension(file.getOriginalFilename()))) {
            return R.fail("文件类型错误,请上传zip格式文件");
        }
        if (name.contains(".zip")) {
            name = FileUtil.removeLastElement(FileUtil.getNameWithoutExtension(name), StringPool.UNDERSCORE);
        }
        topologyDataService.topo2dImport(file, category, name);
        return R.success("导入成功");
    }


    @ApiOperation("组态批量导入")
    @PostMapping(value = "/batch/import")
    public R batchImport(@RequestBody TopologyData topologyData) throws Exception {

        return R.success("导入成功");
    }

    @ApiOperation("组态可视化跳转")
    @PostMapping(value = "/jump/visual")
    public R jumpFromVisual(@RequestBody TopoloyDateJumpVo topoloyDateJumpVo) throws Exception {

        //数据为空
        if (StringUtils.isEmpty(topoloyDateJumpVo.toString())) {
            return R.fail("参数异常，请检查");
        }
        topologyDataService.jumpFromVisual(topoloyDateJumpVo);

        return R.success("sucessful");
    }

    @ApiOperation("组态可视化跳转适配")
    @PostMapping(value = "/jump/topo2d")
    public R jumpToTopo2d(@RequestBody TopoloyDateJumpVo topoloyDateJumpVo) throws Exception {

        TopoloyDateJumpVo res = topologyDataService.jumpToTopo2d(topoloyDateJumpVo.getId());
        if (res == null) {
            return R.fail("图纸不存在或者查询异常，请检查");
        }

        return R.data(res);
    }

    @ApiOperation("组件工程化全局同步")
    @PostMapping(value = "/engineering/global/synchronization")
    public R engineeringGlobalSynchronization(@RequestBody List<EngineeringGlobalSynchronizationFrom> list) throws Exception {
        topologyDataService.engineeringGlobalSynchronization(list);
        TopologyData topologyData = topologyDataService.getById(list.get(0).getId());
        return R.data(JsonUtil.parseObject(topologyData.getData()));
    }

    @ApiOperation("根据外部id查询图纸id")
    @GetMapping(value = "/queryIdByExternalId")
    public R<Map> queryIdByExternalId(@RequestParam String externalId) throws Exception {
        Map<Object, String> id = topologyDataService.queryIdByExternalId(externalId);
        if (ObjectUtils.isEmpty(id)) {
            return R.fail("根据外部id查询图纸id 失败");
        }
        return R.data(id);
    }

}
