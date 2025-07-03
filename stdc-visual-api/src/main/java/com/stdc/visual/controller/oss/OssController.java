package com.stdc.visual.controller.oss;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.http.util.RestTemplateUtil;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.mp.utils.Condition;
import com.stdc.core.mp.utils.Query;
import com.stdc.core.oss.prop.OssFileType;
import com.stdc.core.redis.util.RedisUtils;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.CommonThreadPool;
import com.stdc.visual.common.utils.StdcVisualConstant;
import com.stdc.visual.entity.po.OssFile;
import com.stdc.visual.entity.request.OssFileRequest;
import com.stdc.visual.service.IVisualOssFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.stdc.visual.util.OssFileTask.initFrontCssByName;

/**
 * @author: wang_jie
 * @data: 2022/5/27--17:02
 * @describe:
 */
@RestController
@RequestMapping("/oss")
@Api(tags = "资源：资源管理")
@ApiSupport(author = "wangjie",order = 10)
public class OssController {

    @Autowired
    private IVisualOssFileService iVisualOssFileService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CommonThreadPool commonThreadPool;

    /**
     * 3D文件缓存 key
     */
    public static final String CACHE_3D_KEY = StdcVisualConstant.PROJECT_PREFIX + "CACHE_3D_KEY";

    /**
     * 分页 可视化分类表
     */
    @ApiOperation("查询文件:分页")
    @GetMapping("/file/page/{source}/{type}")
    public R<IPage<OssFile>> page(
            @ApiParam(value = "是否为系统文件: system 系统 custom自定义") @PathVariable("source") String source,
            @ApiParam(value = "文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)") @PathVariable("type") String type,
            Query query) {
        if (!OssFileType.BUCKETS.contains(source)){
            BaseException.throwException(ResultCode.get("source_type_is_error"));
        }
        Integer current = query.getCurrent();
        Integer size = query.getSize();
        //当前页
        current = ObjectUtil.isNotEmpty(current) ? current : 1;
        //查询页数
        size = ObjectUtil.isNotEmpty(size) ? size : 100;
        //数据主体
        List<OssFile> ossFiles = iVisualOssFileService.queryFileByType(source, type,current,size);
        //svg类型转换url为xml类型
        if (StringUtil.equalsIgnoreCase(OssFileType.SVG.getKey(),type)){
            ossFiles.forEach(f->{
                String link = null;
                try { link = RestTemplateUtil.doGet(f.getLink(), new HashMap<>()); } catch (Exception e) { }
                f.setLink(link);
            });
        }
        //数据总数
        Integer count = iVisualOssFileService.queryCountByType(source, type);
        //分页对象
        IPage<OssFile> pages = new Page();
        //设置数据主体
        pages.setRecords(ossFiles);
        //设置当前查询长度
        pages.setSize(size);
        //设置总数
        pages.setTotal(count);
        //设置当前页数
        pages.setCurrent(current);
        return R.data(pages);
    }

    @ApiOperation("查询文件")
    @GetMapping("/file/{source}/{type}")
    public R query(
            @ApiParam(value = "是否为系统文件: system 系统 custom自定义") @PathVariable("source") String source,
            @ApiParam(value = "文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)") @PathVariable("type") String type,
            @ApiParam(value = "大屏id") Long visualId,
            @ApiParam(value = "组件id") String componentId){
        if (!OssFileType.BUCKETS.contains(source)){
            BaseException.throwException(ResultCode.get("source_type_is_error"));
        }
        List<OssFile> query = null;
        if ( ObjectUtil.isNotEmpty(visualId) || StringUtil.hasText(componentId)){
            query = iVisualOssFileService.queryWithId(source, type,visualId,componentId);
        }else {
             query = iVisualOssFileService.query(source, type);
        }
        if (StringUtil.equalsIgnoreCase(OssFileType.SVG.getKey(),type) && CollectionUtils.isNotEmpty(query)){
            query.forEach(f->{
                String link = null;
                try { link = RestTemplateUtil.doGet(f.getLink(), new HashMap<>()); } catch (Exception e) { }
                f.setLink(link);
            });
        }
        return R.data(query);
    }

    @ApiOperation("查询当前大屏图片文件")
    @GetMapping("/file/custom/component/currentImg")
    public R queryCurrentConfigImg(
            @ApiParam(value = "大屏id") Long visualId,
            @ApiParam(value = "大屏id") Long configId){
        List<OssFile> query = iVisualOssFileService.queryCurrentConfigImg(visualId,configId);
        return R.data(query);
    }

    @ApiOperation("查询当前自定义组件图片文件")
    @GetMapping("/file/custom/component/current-component/img")
    public R queryCurrentComponentImg(
            @ApiParam(value = "组件id") String componentId){
        List<OssFile> query = iVisualOssFileService.queryCurrentComponentImg(componentId);
        return R.data(query);
    }

    @ApiOperation("查询当前自定义组件除图片之外的文件")
    @GetMapping("/file/custom/component/current-component/file")
    public R queryCurrentComponentFileExcludeImg(
            @ApiParam(value = "组件id") String componentId){
        List<OssFile> query = iVisualOssFileService.queryCurrentComponentFileExcludeImg(componentId);
        return R.data(query);
    }

    @ApiOperation("上传vue文件获取vue文件内容")
    @PostMapping("/file/vue")
    public Object vue(
            @ApiParam(value = "上传文件") @RequestParam MultipartFile file){
        if (file.isEmpty()) {
            BaseException.throwException("No file uploaded");
        }

        if (!file.getOriginalFilename().endsWith(".vue")) {
            BaseException.throwException("Invalid file type, please upload a .vue file");
        }
        byte[] fileBytes = new byte[0];
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileContent = null;
        try {
            fileContent = new String(fileBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return R.data(fileContent);
    }

    @ApiOperation("删除当前大屏组件文件")
    @PostMapping("/file/custom/component/del/currentImg")
    public R delCurrentConfigImg(
            @ApiParam(value = "图片id") String id){
        boolean b = iVisualOssFileService.delCurrentConfigImg(id);
        if (b){
            return R.success("删除成功");
        }
        return R.fail("删除失败");
    }

    @ApiOperation("新增文件:有大屏id参数")
    @PostMapping("/file/{source}/{type}/{visualId}")
    public R saveWithVisualId(
            @ApiParam(value = "是否为系统文件: system 系统 custom自定义") @PathVariable("source") String source,
            @ApiParam(value = "文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)") @PathVariable("type") String type,
            @ApiParam(value = "上传文件") @RequestParam MultipartFile file,
            @ApiParam(value = "大屏id") @PathVariable("visualId") Long visualId,
            @ApiParam(value = "组件id") String componentId){
        if (!OssFileType.BUCKETS.contains(source)){
            BaseException.throwException(ResultCode.get("source_type_is_error"));
        }
        OssFile save = iVisualOssFileService.save(source, type, visualId, componentId, file);
        if (ObjectUtil.isNotEmpty(save)){
            return R.data(save);
        }
        return R.fail(ResultCode.get("save_data_error"));
    }

    @ApiOperation("psd上传功能:新增文件")
    @PostMapping("/psd/file/custom/component")
    public R saveWithUserAndVisualId(@ApiParam(value = "上传文件") @RequestParam MultipartFile file){
        String source = "custom";
        String type = "component";
        if (!OssFileType.BUCKETS.contains(source)){
            BaseException.throwException(ResultCode.get("source_type_is_error"));
        }
        OssFile save = iVisualOssFileService.save(source, type, null, null, file);
        if (ObjectUtil.isNotEmpty(save)){
            return R.data(save.getLink());
        }
        return R.fail(ResultCode.get("save_data_error"));
    }

    @ApiOperation("新增文件:无大屏id")
    @PostMapping("/file/{source}/{type}")
    public R save(
            @ApiParam(value = "是否为系统文件: system 系统 custom自定义") @PathVariable("source") String source,
            @ApiParam(value = "文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)") @PathVariable("type") String type,
            @ApiParam(value = "上传文件") @RequestParam MultipartFile file,
            @ApiParam(value = "大屏id") Long visualId,
            @ApiParam(value = "大屏页面id") Long configId,
            @ApiParam(value = "组件id") String componentId){
        if (!OssFileType.BUCKETS.contains(source)){
            BaseException.throwException(ResultCode.get("source_type_is_error"));
        }
        if (ObjectUtil.isEmpty(visualId) || ObjectUtil.isEmpty(configId)){
            BaseException.throwException("visualId为空或者configId为空");
        }
        //判断是否为字体文件，字体文件更新admin用户css文件
        if (StringUtil.equals(OssFileType.font.getKey(),type)){
            commonThreadPool.addTask(()->initFrontCssByName("admin"));
        }
        OssFile save = iVisualOssFileService.save(source, type, visualId,configId, componentId, file);
        if (ObjectUtil.isNotEmpty(save)){
            return R.data(save);
        }
        return R.fail(ResultCode.get("save_data_error"));
    }

    @ApiOperation("修改文件")
    @PutMapping("/file/{source}/{type}")
    public R update(
            @ApiParam(value = "是否为系统文件: system 系统 custom自定义") @PathVariable("source") String source,
            @ApiParam(value = "文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)") @PathVariable("type") String type,
            @ApiParam(value = "对象存储实体") @RequestBody OssFileRequest ossFileRequest){
        if (!OssFileType.BUCKETS.contains(source)){
            BaseException.throwException(ResultCode.get("source_type_is_error"));
        }
        boolean update = iVisualOssFileService.update(source, type, ossFileRequest);
        if (update){
            return R.data(ResultCode.SUCCESS);
        }
        return R.fail(ResultCode.get("update_data_error"));
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/file/{source}/{type}")
    public R del(
            @ApiParam(value = "是否为系统文件: system 系统 custom 自定义") @PathVariable("source") String source,
            @ApiParam(value = "文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)") @PathVariable("type") String type,
            @ApiParam(value = "对象存储实体") @RequestBody OssFileRequest ossFileRequest){
        if (!OssFileType.BUCKETS.contains(source)){
            BaseException.throwException(ResultCode.get("source_type_is_error"));
        }
        boolean update = iVisualOssFileService.del(ossFileRequest.getId());
        if (update){
            return R.data(ResultCode.SUCCESS);
        }
        return R.fail(ResultCode.get("del_data_error"));
    }


    @ApiOperation("保存3D文件")
    @PostMapping("/file/3D/custom/component")
    public R save3D(
            @ApiParam(value = "3D文件glb") @RequestParam MultipartFile glbFile,
            @ApiParam(value = "封面文件") @RequestParam MultipartFile imgFile){
        String source = OssFileType.CUSTOM_BUCKET;
        OssFile glbOss = iVisualOssFileService.save3D(source, OssFileType.COMPONENT.getKey(), null, null, glbFile);
        OssFile imgOSS = iVisualOssFileService.save3D(source, OssFileType.COMPONENT.getKey(), null, null, imgFile);
        if (ObjectUtil.isNotEmpty(glbOss) && ObjectUtil.isNotEmpty(imgOSS)){
            ArrayList ret = new ArrayList<OssFile>();
            ret.add(glbOss);
            ret.add(imgOSS);
            Object cacheObject = redisUtils.getCacheList(CACHE_3D_KEY);
            ArrayList cacheList = ObjectUtil.isNotEmpty(cacheObject) ? (ArrayList)cacheObject : new ArrayList();
            cacheList.add(ret);
            redisUtils.deleteObject(CACHE_3D_KEY);
            redisUtils.setCacheList(CACHE_3D_KEY,cacheList);
            return R.data(ret);
        }
        return R.fail(ResultCode.get("save_data_error"));
    }

    @ApiOperation("查询3D文件")
    @GetMapping("/file/3D/custom/component")
    public R query3D(){
        List<Map> ret = new ArrayList<>();
        //查询3D组件文件
        List<OssFile> query = iVisualOssFileService.query3D();
        if (CollectionUtils.isEmpty(query)) return null;
        //查询3D组件文件映射关系  glb-img
        String cacheKey = CACHE_3D_KEY;
        ArrayList cacheList = (ArrayList)redisUtils.getCacheList(cacheKey);
        for (Object cacheValue : cacheList) {
            //获取到映射分组
            List<OssFile> valueS = (ArrayList<OssFile>) cacheValue;
            //验证是否在数据库中
            List<String> idCollect = query.stream().map(OssFile::getId).collect(Collectors.toList());
            OssFile glb = null;
            OssFile img = null;
            valueS = valueS.stream().filter(v -> idCollect.contains(v.getId())).collect(Collectors.toList());
            for (OssFile value : valueS) {
                if (StringUtil.equalsIgnoreCase("glb",value.getNameSuffix())){
                    glb = value;
                }else {
                    img = value;
                }
            }
            if (ObjectUtil.isNotEmpty(glb) && ObjectUtil.isNotEmpty(img)){
                HashMap<String, String> valueMap = new HashMap<>();
                valueMap.put("glb",glb.getLink());
                valueMap.put("img",img.getLink());
                ret.add(valueMap);
            }
        }
        return R.data(ret);
    }

    @ApiOperation("请求接口获取font.css文件地址url")
    @GetMapping("/file/get/font-css")
    public R writeFontCss(){
        return R.data(contextPath + "/css/admin/font.css");
    }


    @ApiOperation("获取二级表头文件")
    @PostMapping("/level2/excel/export")
    public R getLevel2Excel(@RequestBody String body){
        return R.data(iVisualOssFileService.convertJSONToExcel(body));
    }

}
