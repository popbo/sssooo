package com.stdc.visual.controller.visual;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.mp.utils.Query;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.redis.util.RedisUtils;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.Func;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.annotation.ApiDecryptAes;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.StdcVisualConstant;
import com.stdc.visual.entity.dto.VisualDTO;
import com.stdc.visual.entity.dto.VisualVersionDTO;
import com.stdc.visual.entity.po.*;
import com.stdc.visual.entity.request.VisualRequest;
import com.stdc.visual.mapper.OssFileMapper;
import com.stdc.visual.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.stdc.core.oss.utils.FileUtil.getFileExtension;

/**
 * @author: wang_jie
 * @data: 2022/5/12--18:47
 * @describe: 可视化表 控制器
 */
@RestController
@RequestMapping("/visual")
@ApiSupport(author = "wangjie",order = 13)
@Api(tags = "大屏:大屏详情")
public class VisualController {

    @Autowired
    private IVisualService visualService;

    @Autowired
    private IVisualCategoryService categoryService;

    @Autowired
    private RoleSourceService roleSourceService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private IVisualReleaseService releaseService;

    @Autowired
    private OssFileMapper ossFileMapper;

    @Autowired
    private IVisualConfigService configService;

    @Autowired
    private GesturePasswordService gesturePasswordService;

    @Value("${stdc.preview-time}")
    private Long previewTime;


    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入visual")
    public R<VisualDTO> detail(@RequestParam Long id) {
        VisualDTO detail = visualService.detail(id);
        return R.data(detail);
    }

    /**
     * 导出功能
     */
    @GetMapping("/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "大屏信息:导出功能", notes = "传入visual")
    public R export(@RequestParam Long id) {
//        return R.data(visualService.exportZip(id));
        //由于大屏的图片文件版本问题,普通导出功能不再适合,先修改为完成导出功能
        return fullExport(id);
//        return R.success("导出成功");
    }

    /**
     * 完整导出功能
     */
    @GetMapping("/full/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "大屏信息:完整导出功能", notes = "传入visual")
    public R fullExport(@RequestParam Long id) {
        try {
            return R.data(visualService.fullExport(id));
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导出失败");
    }

    /**
     * 轻量化导出功能
     */
    @GetMapping("/light-weight/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "大屏信息:轻量化导出功能", notes = "传入visual")
    public R lightWeightExport(@RequestParam String ids) {
        return R.data(visualService.lightWeightExport(Func.toLongList(ids)));
    }


    /**
     * 轻量化导出功能
     */
    @GetMapping("/light-weight/topology/export")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "大屏信息:轻量化导出功能(组态)", notes = "传入visual")
    public R lightWeightExportForTopology(@RequestParam String ids) {
        return R.data(visualService.lightWeightExportForTopology(Func.toLongList(ids)));
    }

    /**
     * 批量导出功能
     */
    @GetMapping("/batch/export")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "大屏信息:批量导出功能", notes = "传入visual")
    public R batchExport(@RequestParam String ids) {
        String ret = null;
        try {
            ret = visualService.batchExport(Func.toLongList(ids));
        } catch (Exception e) {
            BaseException.throwException(e);
        }
        return R.data(ret);
    }

    /**
     * 导入功能
     */
    @PostMapping("/import")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "大屏信息:导入功能", notes = "传入visual")
    public R saveVisual(@Valid @RequestBody VisualDTO visual) {
        boolean temp = visualService.saveVisual(visual);
        if (temp) {
            Long id = visual.getVisual().getId();
            Map<String, String> idM = new HashMap<>();
            idM.put("id", String.valueOf(id));
            return R.data(idM);
        } else {
            return R.status(false);
        }
    }

    /**
     * 完整导入功能
     */
    @PostMapping("/full/import/{category}/{visualName}")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "大屏信息:完整导入功能", notes = "传入visual")
    public R fullImport(@ApiParam(value = "上传文件") @RequestParam MultipartFile file,
                        @ApiParam(value = "大屏分类") @PathVariable("category") String category,
                        @ApiParam(value = "大屏名称") @PathVariable("visualName") String visualName) throws Exception {
        if (!StringUtil.equalsIgnoreCase("zip",getFileExtension(file.getOriginalFilename()))){
            return R.fail("文件类型错误,请上传zip格式文件");
        }
        if (visualName.contains(".zip")){
            visualName = FileUtil.removeLastElement(FileUtil.getNameWithoutExtension(visualName), StringPool.UNDERSCORE);
        }
        visualService.fullImport(file,category,visualName);
        return R.success("导入完成");
    }

    /**
     * 获取相关版本
     */
    @GetMapping("/detail/{version}")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "获取相关版本详情", notes = "传入visual")
    public R<VisualDTO> detailVersion(@ApiParam(value = "大屏id", required = true) @RequestParam Long id,
                                      @ApiParam(value = "版本号", required = true) @PathVariable("version")String version) {
        VisualDTO detail = visualService.detailVersion(id,version);
        return R.data(detail);
    }


    /**
     * 详情（加密）
     */
    @ApiDecryptAes
    @PostMapping("/share/detail")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "详情", notes = "传入visual")
    public R<VisualDTO> shareDetail(@RequestBody String id) {
        VisualDTO detail = visualService.detail(Long.valueOf(id));
        return R.data(detail);
    }

//    /**
//     * 分页 AI驾驶舱可视化表
//     */
//    @GetMapping("/AI/list")
//    @ApiOperationSupport(order = 6)
//    @ApiOperation(value = "分页", notes = "传入visual")
//    public R<IPage<Visual>> aiList(VisualRequest request, Query query) {
//        //设置为AI驾驶舱分类
//        request.setCategory(AI_CATEGORY_VALUE);
//        return list(request,query);
//    }

    /**
     * 分页 可视化表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "分页", notes = "传入visual")
    public R<IPage<Visual>> list(VisualRequest request, Query query) {
        /*QueryWrapper<Visual> queryWrapper = Condition.getQueryWrapper(request);
        if (StringUtil.hasText(request.getTitle())){
            queryWrapper.like("title",request.getTitle());
        }
        request.setTitle(null);
        if (StringUtil.hasText(request.getOrder())){
            queryWrapper.orderByDesc(request.getOrder());
        }*/
        //IPage<Visual> pages = visualService.page(Condition.getPage(query), queryWrapper);
        IPage<Visual> pages = visualService.getAllList(request, query);
        pages.getRecords().forEach(v->{
            VisualRelease release = releaseService.queryOneById(String.valueOf(v.getId()));
            if (ObjectUtil.isNotEmpty(release)){
                release.setComponent(null);
                release.setDetail(null);
                v.setRelease(release);
            }else {
                v.setRelease(new VisualRelease());
            }
            //大屏封面为缩略图 更新为最新的缩略图的id
            if (StringUtil.equals(BackGroundType.THUMBNAIL.getValue(),v.getBackgroundType())){
                //找到更新时间最新的config
                VisualConfig lastConfig = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, v.getId()).orderByDesc(VisualConfig::getUpdateTime).last(" limit 1"));
                if (ObjectUtil.isNotEmpty(lastConfig) && StringUtil.hasText(lastConfig.getBackgroundId())){
                    v.setBackgroundId(lastConfig.getBackgroundId());
                    OssFile ossFile = ossFileMapper.selectById(lastConfig.getBackgroundId());
                    if (ObjectUtil.isNotEmpty(ossFile)){
                        v.setBackgroundUrl(ossFile.getLink());
                    }
                }else {
                    v.setBackgroundId(null);
                }
            }
            //大屏封面为自定义图
            if (StringUtil.equals(BackGroundType.CUSTOM.getValue(),v.getBackgroundType())){
                if (StringUtil.hasText(v.getBackgroundId())){
                    OssFile ossFile = ossFileMapper.selectById(v.getBackgroundId());
                    if (ObjectUtil.isNotEmpty(ossFile)){
                        v.setBackgroundUrl(ossFile.getLink());
                    }
                }else {
                    v.setBackgroundUrl(null);
                }
            }
        });
        return R.data(pages);
    }

    /**
     * 新增 可视化表
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "新增", notes = "传入visual")
    public R save(@Valid @RequestBody VisualDTO visual) {
        boolean temp = visualService.saveVisual(visual);
        if (temp) {
            Long id = visual.getVisual().getId();
            Map<String, String> idM = new HashMap<>();
            idM.put("id", String.valueOf(id));
            return R.data(idM);
        } else {
            return R.status(false);
        }
    }

    @PostMapping("/psd/save")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "通过导入psd文件的方式新增大屏")
    public R pageParamsList(@ApiParam(value = "上传文件") @RequestParam MultipartFile file,
                            @ApiParam(value = "大屏id") String category){
        Boolean isSave = null;
        try {
            isSave = visualService.psdSave(file,category);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
        return R.data(isSave);
    }



    /**
     * 新增为新版本 可视化表
     * visual 进行更新  config进行插入
     */
    @PostMapping("/save/version")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "新增为新版本", notes = "传入visual")
    public R saveNewVersion(@Valid @RequestBody VisualDTO visual) {
        boolean temp = visualService.saveVisualNewVersion(visual);
        if (temp) {
            Long id = visual.getVisual().getId();
            Map<String, String> idM = new HashMap<>();
            idM.put("id", String.valueOf(id));
            return R.data(idM);
        } else {
            return R.status(false);
        }
    }

    /**
     * 获取大屏版本详情
     * @param visualId 大屏id
     * @return 版本详情
     */
    @PostMapping("/query-version/{visualId}")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "获取大屏版本详情")
    public R queryVisualVersion(@ApiParam(value = "大屏id", required = true) @PathVariable("visualId") Long visualId) {
        return R.data(visualService.queryVisualVersion(visualId));
    }

    /**
     * 修改 可视化表
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "修改", notes = "传入visual")
    public R update(@Valid @RequestBody VisualDTO visual) {
        return R.status(visualService.updateVisual(visual));
    }

    /**
     * 修改 可视化版本 信息
     */
    @PostMapping("/update/version")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "修改版本信息", notes = "传入版本信息对象")
    public R updateVersion(@Valid @RequestBody @ApiParam(value = "版本信息", required = true) VisualVersionDTO versionDTO) {
        boolean b = visualService.updateVersion(versionDTO);
        if (b){
            return R.success("修改成功");
        }
        return R.fail("修改失败");
    }

    /**
     * 删除 可视化表
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(visualService.removeVisual(Func.toLongList(ids)));
    }

    /**
     * 删除 可视化表 版本
     */
    @PostMapping("/remove/version")
    @ApiOperationSupport(order = 13)
    @ApiOperation(value = "删除大屏版本", notes = "传入ids")
    public R removeVersion(@ApiParam(value = "版本Id", required = true) @RequestParam Long versionId) {
        return R.status(visualService.removeVersion(versionId));
    }

    /**
     * 复制 可视化表
     */
    @PostMapping("/copy")
    @ApiOperationSupport(order = 14)
    @ApiOperation(value = "复制", notes = "传入id")
    public R copy(@ApiParam(value = "主键集合", required = true) @RequestParam Long id) {
        return R.data(String.valueOf(visualService.copyVisual(id)));
    }

    /**
     * 获取分类
     */
    @GetMapping("category")
    @ApiOperationSupport(order = 15)
    @ApiOperation(value = "获取类型")
    public R category() {
        //查询当前资源类型(大屏)，当前用户角色拥有的 资源id 集合
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.VISUAL_CATEGORY);
        List<VisualCategory> list = categoryService.list(new LambdaQueryWrapper<VisualCategory>().in(VisualCategory::getId,ids));
        return R.data(list);
    }

    /**
     * 设定大屏超时时间
     */
    @GetMapping("/view-init/{id}")
    @ApiOperationSupport(order = 16)
    @ApiOperation(value = "预览设定时间")
    public R viewInit(@ApiParam(value = "大屏主键", required = true) @PathVariable("id") String id) {
        //设定预览时间
        redisUtils.deleteObject(StdcVisualConstant.PROJECT_PREFIX + id);
        redisUtils.setCacheObject(StdcVisualConstant.PROJECT_PREFIX + id,StdcVisualConstant.PROJECT_PREFIX + id,previewTime, TimeUnit.MILLISECONDS);
        return R.success("");
    }

    /**
     * 获取大屏超时时间
     */
    @GetMapping("/view-get/{id}")
    @ApiOperationSupport(order = 17)
    @ApiOperation(value = "获取超时时间")
    public R viewGet(@ApiParam(value = "大屏主键", required = true) @PathVariable("id") String id) {
        //设定预览时间
        Object cacheObject = redisUtils.getCacheObject(StdcVisualConstant.PROJECT_PREFIX + id);
        if (ObjectUtil.isEmpty(cacheObject)){
            return R.fail("大屏预览已超时");
        }else {
            return R.success("大屏预览未超时");
        }
    }

    /**
     * 保存手势密码
     */
    @PostMapping("/gtpsw/save")
    @ApiOperationSupport(order = 18)
    @ApiOperation(value = "【手势密码】保存手势密码")
    public R saveGtPsw(@ApiParam(value = "大屏主键", required = true) @RequestBody GesturePassword gesturePassword) {
        return R.status(gesturePasswordService.save(gesturePassword));
    }

    /**
     * 验证手势密码
     */
    @PostMapping("/gtpsw/validate")
    @ApiOperationSupport(order = 19)
    @ApiOperation(value = "【手势密码】验证手势密码")
    public R validateGtPsw(@ApiParam(value = "大屏主键", required = true) @RequestBody GesturePassword gesturePassword) {
        return R.status(gesturePasswordService.validate(gesturePassword));
    }

    /**
     * 通过旧手势密码，修改为新手势密码
     * [query] id 手势密码id
     * [query] oldGtPsw 旧手势密码
     * [query] newGtPsw 新手势密码
     */
    @PostMapping("/gtpsw/updateGtPsw")
    @ApiOperationSupport(order = 20,includeParameters = {"id","oldGtPsw","newGtPsw"})
    @ApiOperation(value = "【手势密码】通过旧手势密码，修改为新手势密码")
    public R updateGtPsw(@ApiParam(value = "【id】手势密码id,【oldGtPsw】旧手势密码,【newGtPsw】新手势密码", required = true) @RequestBody Map<String,Object> body) {
        Long id = (Long) body.get("id");
        String oldGtPsw = (String) body.get("oldGtPsw");
        String newGtPsw = (String) body.get("newGtPsw");
        return R.status(gesturePasswordService.updateGtPsw(id,oldGtPsw,newGtPsw));
    }

    /**
     * 通过旧验证密码，修改为新验证密码
     * [query] id 手势密码id
     * [query] oldVPsw 旧验证密码
     * [query] newVPsw 新验证密码
     */
    @PostMapping("/gtpsw/updateVPsw")
    @ApiOperationSupport(order = 21,includeParameters = {"id","oldVPsw","newVPsw"})
    @ApiOperation(value = "【手势密码】通过旧验证密码，修改为新验证密码")
    public R updateVPsw(@ApiParam(value = "【id】手势密码id,【oldVPsw】旧验证密码,【newVPsw】新验证密码", required = true) @RequestBody Map<String,Object> body) {
        Long id = (Long) body.get("id");
        String oldVPsw = (String) body.get("oldVPsw");
        String newVPsw = (String) body.get("newVPsw");
        return R.status(gesturePasswordService.updateVPsw(id,oldVPsw,newVPsw));
    }

    /**
     * 通过验证密码，修改手势密码 【找回密码】
     * [query] id 手势密码id
     * [query] vPsw 验证密码
     * [query] gtPsw 手势密码
     */
    @PostMapping("/gtpsw/updateGtPswByVPsw")
    @ApiOperationSupport(order = 22,includeParameters = {"id","vPsw","gtPsw"})
    @ApiOperation(value = "【手势密码】通过验证密码，修改手势密码 【找回密码】")
    public R updateGtPswByVPsw(@ApiParam(value = "【id】手势密码id,【vPsw】验证密码,【gtPsw】手势密码", required = true) @RequestBody Map<String,Object> body) {
        Long id = (Long) body.get("id");
        String vPsw = (String) body.get("vPsw");
        String gtPsw = (String) body.get("gtPsw");
        return R.status(gesturePasswordService.updateGtPswByVPsw(id,vPsw,gtPsw));
    }

    /**
     * 删除手势密码
     */
    @DeleteMapping("/gtpsw/del/{id}")
    @ApiOperationSupport(order = 23)
    @ApiOperation(value = "【手势密码】删除手势密码")
    public R deleteGtPswByVPsw(@ApiParam(value = "手势密码id", required = true) @PathVariable("id") Long id) {
        return R.status(gesturePasswordService.delete(id));
    }

    /**
     * 通过某一个大屏id获取和这个大屏相同分类的所有的大屏id
     */
    @GetMapping("/same-category-ids/{id}")
    @ApiOperationSupport(order = 24)
    @ApiOperation(value = "通过某一个大屏id获取和这个大屏相同分类的所有的大屏id")
    public R sameCateGory(@ApiParam(value = "手势密码id", required = true) @PathVariable("id") Long id) {
        return R.data(visualService.getSameCateGory(id));
    }


    @GetMapping("/pageParamsList/{id}")
    @ApiOperationSupport(order = 25)
    @ApiOperation(value = "返回分类下其他页面的id，title和页面配置的参数")
    public R pageParamsList(@ApiParam(value = "分类id", required = true) @PathVariable("id") Long id){
        List<Map<String,Object>> ret = new ArrayList<>();
        List<Long> ids = visualService.getSameCateGory(id);
        //不返回自己的信息
        ids.remove(id);
        ids.forEach(visualId->{
            Map<String,Object> map = new HashMap<>(4);
            VisualDTO detail = visualService.detail(visualId);
            map.put("id", String.valueOf(visualId));
            map.put("title",detail.getVisual().getTitle());
            JSONObject detailInfo = JSON.parseObject(detail.getConfig().getDetail());
            map.put("pageParamsList", detailInfo.get("pageParamsList"));
            VisualRelease release = releaseService.queryOneByVisualId(visualId);
            if(ObjectUtil.isEmpty(release)){
                map.put("path","");
            }else {
                map.put("path",release.getPath());
            }
            ret.add(map);
        });
        return R.data(ret);
    }


    @GetMapping("/initAITemplate")
    @ApiOperationSupport(order = 26)
    @ApiOperation(value = "初始化ai驾驶舱模板")
    public void initAITemplate(){
        String path = "E:/Desktop/theNewAiTemplate";

        LogUtil.info("-----------------------------AI-金色-PC模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-PC模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-PC模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-PC模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-PC模板-----------------------------------------------------------------");


        VisualCategory kingPC = categoryService.getOne(new LambdaQueryWrapper<VisualCategory>().eq(VisualCategory::getCategoryKey, "AI-金色-PC模板"));
        Map<String,JSONArray> kingPCComponentMap = new HashMap<>();
        List<Visual> kingPCS = visualService.list(new LambdaQueryWrapper<Visual>().eq(Visual::getCategory, kingPC.getCategoryValue()));
        kingPCS.forEach(kingVisual->{
            VisualConfig one = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, kingVisual.getId()));
            kingPCComponentMap.put(kingVisual.getTitle(),JSONArray.parseArray(one.getComponent()));
        });

        String kingPCPath = path + "/king/component-template/pc-screen/";
        for (String filename : kingPCComponentMap.keySet()) {
            writeToJsonFile(kingPCPath + filename + ".json",kingPCComponentMap.get(filename));
        }


        LogUtil.info("-----------------------------AI-金色-大屏模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-大屏模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-大屏模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-大屏模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-大屏模板-----------------------------------------------------------------");



        VisualCategory kingLarge = categoryService.getOne(new LambdaQueryWrapper<VisualCategory>().eq(VisualCategory::getCategoryKey, "AI-金色-大屏模板"));
        Map<String,JSONArray> kingLargeComponentMap = new HashMap<>();
        List<Visual> kingLargeS = visualService.list(new LambdaQueryWrapper<Visual>().eq(Visual::getCategory, kingLarge.getCategoryValue()));
        kingLargeS.forEach(kingVisual->{
            VisualConfig one = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, kingVisual.getId()));
            kingLargeComponentMap.put(kingVisual.getTitle(),JSONArray.parseArray(one.getComponent()));
        });

        String kingLargePath = path + "/king/component-template/large-screen/";
        for (String filename : kingLargeComponentMap.keySet()) {
            writeToJsonFile(kingLargePath + filename + ".json",kingLargeComponentMap.get(filename));
        }


        LogUtil.info("-----------------------------AI-蓝色-PC模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-PC模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-PC模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-PC模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-PC模板-----------------------------------------------------------------");



        VisualCategory bluePC = categoryService.getOne(new LambdaQueryWrapper<VisualCategory>().eq(VisualCategory::getCategoryKey, "AI-蓝色-PC模板"));
        Map<String,JSONArray> bluePCComponentMap = new HashMap<>();
        List<Visual> bluePCS = visualService.list(new LambdaQueryWrapper<Visual>().eq(Visual::getCategory, bluePC.getCategoryValue()));
        bluePCS.forEach(kingVisual->{
            VisualConfig one = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, kingVisual.getId()));
            bluePCComponentMap.put(kingVisual.getTitle(),JSONArray.parseArray(one.getComponent()));
        });

        String bluePCPath = path + "/blue/component-template/pc-screen/";
        for (String filename : bluePCComponentMap.keySet()) {
            writeToJsonFile(bluePCPath + filename + ".json",bluePCComponentMap.get(filename));
        }



        LogUtil.info("-----------------------------AI-蓝色-大屏模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-大屏模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-大屏模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-大屏模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-大屏模板-----------------------------------------------------------------");



        VisualCategory blueLarge = categoryService.getOne(new LambdaQueryWrapper<VisualCategory>().eq(VisualCategory::getCategoryKey, "AI-蓝色-大屏模板"));
        Map<String,JSONArray> blueLargeComponentMap = new HashMap<>();
        List<Visual> blueLargeS = visualService.list(new LambdaQueryWrapper<Visual>().eq(Visual::getCategory, blueLarge.getCategoryValue()));
        blueLargeS.forEach(kingVisual->{
            VisualConfig one = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, kingVisual.getId()));
            blueLargeComponentMap.put(kingVisual.getTitle(),JSONArray.parseArray(one.getComponent()));
        });

        String blueLargePath = path + "/blue/component-template/large-screen/";
        for (String filename : blueLargeComponentMap.keySet()) {
            writeToJsonFile(blueLargePath + filename + ".json",blueLargeComponentMap.get(filename));
        }


        LogUtil.info("-----------------------------AI-金色-手机模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-手机模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-手机模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-手机模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-金色-手机模板-----------------------------------------------------------------");



        VisualCategory kingMobile = categoryService.getOne(new LambdaQueryWrapper<VisualCategory>().eq(VisualCategory::getCategoryKey, "AI-金色-手机模板"));
        Map<String,JSONArray> kingMobileComponentMap = new HashMap<>();
        List<Visual> kingMobileS = visualService.list(new LambdaQueryWrapper<Visual>().eq(Visual::getCategory, kingMobile.getCategoryValue()));
        kingMobileS.forEach(kingMobileV->{
            VisualConfig one = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, kingMobileV.getId()));
            kingMobileComponentMap.put(kingMobileV.getTitle(),JSONArray.parseArray(one.getComponent()));
        });

        String kingMobilePath = path + "/king/component-template/mobile-screen/";
        for (String filename : kingMobileComponentMap.keySet()) {
            writeToJsonFile(kingMobilePath + filename + ".json",kingMobileComponentMap.get(filename));
        }


        LogUtil.info("-----------------------------AI-蓝色-手机模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-手机模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-手机模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-手机模板-----------------------------------------------------------------");
        LogUtil.info("-----------------------------AI-蓝色-手机模板-----------------------------------------------------------------");



        VisualCategory blueMobile = categoryService.getOne(new LambdaQueryWrapper<VisualCategory>().eq(VisualCategory::getCategoryKey, "AI-蓝色-手机模板"));
        Map<String,JSONArray> blueMobileComponentMap = new HashMap<>();
        List<Visual> blueMobileS = visualService.list(new LambdaQueryWrapper<Visual>().eq(Visual::getCategory, blueMobile.getCategoryValue()));
        blueMobileS.forEach(blueMobileV->{
            VisualConfig one = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, blueMobileV.getId()));
            blueMobileComponentMap.put(blueMobileV.getTitle(),JSONArray.parseArray(one.getComponent()));
        });

        String blueMobilePath = path + "/blue/component-template/mobile-screen/";
        for (String filename : blueMobileComponentMap.keySet()) {
            writeToJsonFile(blueMobilePath + filename + ".json",blueMobileComponentMap.get(filename));
        }

    }

    public static void writeToJsonFile(String filePath, JSONArray jsonArray) {
        try {
            // 创建文件对象
            File file = new File(filePath);

            // 获取文件所在目录
            File parentDir = file.getParentFile();

            // 如果目录不存在，创建目录
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 创建文件写入器
            FileWriter fileWriter = new FileWriter(file);

            // 将 JSONArray 写入文件
            fileWriter.write(jsonArray.toJSONString());

            // 关闭文件写入器
            fileWriter.close();

            LogUtil.info("JSON 文件保存成功:"+filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
