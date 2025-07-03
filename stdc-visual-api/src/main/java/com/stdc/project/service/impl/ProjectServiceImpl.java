package com.stdc.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.oss.prop.OssFileType;
import com.stdc.core.oss.utils.OssTemplate;
import com.stdc.core.oss.vo.OssFileVO;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.DateUtil;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.project.entity.Project;
import com.stdc.project.entity.vo.ProjectShare;
import com.stdc.project.mapper.ProjectMapper;
import com.stdc.project.service.IProjectService;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.service.ITopologyDataService;
import com.stdc.topology2d.util.SnowflakeIdGenerator;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.auth.mapper.VisualReleaseMapper;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.CommonThreadPool;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.common.utils.StdcVisualConstant;
import com.stdc.visual.common.utils.ZipFileUtils;
import com.stdc.visual.entity.dto.VisualDTO;
import com.stdc.visual.entity.po.OssFile;
import com.stdc.visual.entity.po.Visual;
import com.stdc.visual.entity.po.VisualCategory;
import com.stdc.visual.entity.po.VisualConfig;
import com.stdc.visual.entity.vo.VisualCategoryTreeVO;
import com.stdc.visual.mapper.OssFileMapper;
import com.stdc.visual.service.IVisualCategoryService;
import com.stdc.visual.service.IVisualReleaseService;
import com.stdc.visual.service.IVisualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.stdc.core.oss.utils.FileUtil.*;

/**
 * <p>
 * 工程管理表 服务实现类
 * </p>
 *
 * @author dongbobo
 * @since 2025-06-23
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    private static final String EXPORT_FILE_CACHE_PATH = "./static/export";

    private static final String TOPOLOGY2D_JSON_NAME = "topo2d.json";

    private static final String VISUAL_JSON_NAME = "visual.json";

    private static final String CATEGORY_JSON_NAME = "category.json";

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ITopologyDataService topologyDataService;

    @Autowired
    private OssFileMapper ossFileMapper;

    @Autowired
    private IVisualService visualService;

    @Autowired
    private IVisualReleaseService visualReleaseService;

    @Autowired
    private IVisualCategoryService visualCategoryService;

    @Autowired
    private RoleSourceService roleSourceService;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private CommonThreadPool commonThreadPool;

    @Autowired
    private VisualReleaseMapper releaseMapper;

    @Autowired
    private SnowflakeIdGenerator idGenerator;


    @Override
    public Map<String, Object> getStatistics() {
        return projectMapper.getStatistics();
    }

    @Override
    public String export(String id) throws Exception {
        String ret = "";
        //获取工程信息
        Project project = projectMapper.selectById(id);
        String downFilePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + project.getName();
        //下载压缩包
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + project.getName() + ".zip";
        String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;

        JSONObject categoryJson = new JSONObject();

        QueryWrapper<VisualCategory> topoQueryWrapper = new QueryWrapper<>();
        topoQueryWrapper.eq("project_id", id);
        topoQueryWrapper.eq("category_source", 1);
        List<VisualCategory> topoCategoryList = visualCategoryService.list(topoQueryWrapper);
        List<VisualCategoryTreeVO> topologyCategory = visualCategoryService.getVisualCategoryTreeVO(topoCategoryList);
        categoryJson.put("topologyCategory", topoCategoryList);
        QueryWrapper<VisualCategory> visualQueryWrapper = new QueryWrapper<>();
        visualQueryWrapper.eq("project_id", id);
        visualQueryWrapper.eq("category_source", 0);
        List<VisualCategory> visualCategoryList = visualCategoryService.list(visualQueryWrapper);
        List<VisualCategoryTreeVO> visualCategory = visualCategoryService.getVisualCategoryTreeVO(visualCategoryList);
        categoryJson.put("visualCategory", visualCategoryList);
        /*QueryWrapper<VisualCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", id);
        queryWrapper.eq("parent_id", 0);
        List<VisualCategory> categoryList = visualCategoryService.list(queryWrapper);
        for (VisualCategory visualCategory : categoryList) {
            if ("1".equals(visualCategory.getCategorySource())) {
                //导出组态大屏 按菜单
                topologyDataService.exportTopologyByProjectIdAndCategory(visualCategory.getCategoryValue(), project);
            } else if ("0".equals(visualCategory.getCategorySource())) {
                //导出可视化大屏 按菜单
                visualService.exportVisualByProjectIdAndCategory(visualCategory.getCategoryValue(), project);
            }
        }*/
        //导出组态大屏
        topologyDataService.exportTopologyByProjectId(id, downFilePath);
        //导出可视化大屏
        visualService.exportVisualByProjectId(id, downFilePath);

        JsonUtil.writeJsonToFile(categoryJson.toJSONString(),downFilePath + StringPool.SLASH + CATEGORY_JSON_NAME);

        FileInputStream inputStream =null;
        try {
            createFile(zipSavePath);
            ZipFileUtils.writeFolderToZip(downFilePath,zipSavePath);

            File file = new File(zipSavePath);
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", zipName, inputStream);
            ret = save.getLink();
        } catch (Exception e) {
            log.error("压缩本地文件失败...");
            e.printStackTrace();
        } finally {
            if (inputStream!=null) inputStream.close();
            //删除本地缓存文件
            delFile(downFilePath);
            commonThreadPool.addTask(()->{
                try {
                    Thread.sleep(20000);
                    delFile(zipSavePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return ret;
    }

    @Override
    public String batchExport(List<String> idList) throws Exception {
        String ret = "";
        String batch = "批量工程导出";
        String batchDownFilePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + batch;
        String batchZipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + batch + ".zip";
        String batchZipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + batchZipName;
        for (String id : idList) {
            //获取工程信息
            Project project = projectMapper.selectById(id);
            String downFilePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + batch + StringPool.SLASH + project.getName();
            //导出组态大屏
            topologyDataService.exportTopologyByProjectId(id, downFilePath);
            //导出可视化大屏
            visualService.exportVisualByProjectId(id, downFilePath);
        }
        FileInputStream inputStream =null;
        try {
            createFile(batchZipSavePath);
            ZipFileUtils.writeFolderToZip(batchDownFilePath,batchZipSavePath);

            File file = new File(batchZipSavePath);
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", batchZipName, inputStream);
            ret = save.getLink();
        } catch (Exception e) {
            log.error("压缩本地文件失败...");
            e.printStackTrace();
        } finally {
            if (inputStream!=null) inputStream.close();
            //删除本地缓存文件
            delFile(batchDownFilePath);
            commonThreadPool.addTask(()->{
                try {
                    Thread.sleep(20000);
                    delFile(batchZipSavePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return ret;
    }

    @Override
    public void importProject(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        Project project = new Project();
        project.setName(fileName.substring(0,fileName.lastIndexOf(".")));
        project.setCreateBy(AuthUtils.getUser().getUsername());
        project.setCreateTime(new Date());
        projectMapper.insert(project);

        //获取zip文件包对象
        String zipPath= new File("").getAbsolutePath() + StringPool.SLASH + "static" + StringPool.SLASH + StringUtil.randomUUID() + StringPool.UNDERSCORE +file.getOriginalFilename();
        File convFile = new File(zipPath);
        file.transferTo(convFile);
        ZipFile zipFile = new ZipFile(convFile, Charset.forName("GBK"));

        //处理category.json文件 创建分类
        Map<String, HashMap<String, VisualCategory>> map = dealCategoryData(zipFile, project.getId());

        //组态大屏导入
        topologyDataService.topo2dImportFromProject(zipFile, project.getId(), map.get("topoCategoryMaps"));
        //可视化大屏导入
        visualService.fullImportFromProject(zipFile, project.getId(), map.get("visualCategoryMaps"));

        //关闭
        zipFile.close();
        //删除缓存文件
        delFile(convFile.getPath());
    }

    //分类信息处理 通过category.json文件 创建新的分类
    private Map<String, HashMap<String, VisualCategory>> dealCategoryData(ZipFile zipFile, String projectId) throws IOException {
        List<? extends ZipEntry> categoryJson = zipFile.stream().filter(
                entry -> entry.getName().contains(CATEGORY_JSON_NAME)).collect(Collectors.toList());
        ZipEntry categoryJsonZipEntry = null;
        if (!CollectionUtils.isEmpty(categoryJson)) {
            categoryJsonZipEntry = categoryJson.stream().findFirst().get();
        }
        //读取 category.json 内容
        String categoryStr = null;
        if (ObjectUtil.isNotEmpty(categoryJsonZipEntry)){
            categoryStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(categoryJsonZipEntry))).readLine();
        }
        JSONObject categoryJsonObject = JsonUtil.parseObject(categoryStr);

        //可视化大屏分类信息
        JSONArray visualCategoryArray = categoryJsonObject.getJSONArray("visualCategory");
        //key=导入分类的categoryValue value=生成的新分类
        HashMap<String, VisualCategory> visualCategoryMaps = new HashMap<>();
        //key=导入分类的id value=生成的新分类id 用于替换子分类的parentId字段
        HashMap<String, String> visualIdMaps= new HashMap<>();
        for (Object object : visualCategoryArray) {
            VisualCategory category = JsonUtil.parseObject(object.toString(), VisualCategory.class);
            String oldId = category.getId().toString();
            String oldCategoryValue = category.getCategoryValue();
            category.setId(idGenerator.nextId());
            category.setProjectId(projectId);
            category.setCategoryValue(StringUtil.randomUUID());
            category.setCreateTime(System.currentTimeMillis());
            visualCategoryService.save(category);
            //添加资源
            VisualCategory categorySource = visualCategoryService.getOne(new LambdaQueryWrapper<VisualCategory>()
                    .eq(VisualCategory::getCategoryKey, category.getCategoryKey())
                    .eq(VisualCategory::getCategoryValue, category.getCategoryValue()));
            roleSourceService.saveRoleSource(categorySource.getId(), SourceType.VISUAL_CATEGORY);

            visualCategoryMaps.put(oldCategoryValue, category);
            visualIdMaps.put(oldId, category.getId().toString());
        }
        //替换子分类的parentId字段
        visualCategoryMaps.forEach((oldCategoryValue, category) -> {
            if (category.getParentId() != null && !"0".equals(category.getParentId())) {
                category.setParentId(visualIdMaps.get(category.getParentId()));
                visualCategoryService.updateById(category);
            }
        });

        //组态大屏分类信息
        JSONArray topologyCategoryArray = categoryJsonObject.getJSONArray("topologyCategory");
        //key=导入分类的categoryValue value=生成的新分类
        HashMap<String, VisualCategory> topoCategoryMaps = new HashMap<>();
        //key=导入分类的id value=生成的新分类id 用于替换子分类的parentId字段
        HashMap<String, String> topoIdMaps= new HashMap<>();
        for (Object object : topologyCategoryArray) {
            VisualCategory category = JsonUtil.parseObject(object.toString(), VisualCategory.class);
            String oldId = category.getId().toString();
            String oldCategoryValue = category.getCategoryValue();
            category.setId(idGenerator.nextId());
            category.setProjectId(projectId);
            category.setCategoryValue(StringUtil.randomUUID());
            category.setCreateTime(System.currentTimeMillis());
            visualCategoryService.save(category);
            //添加资源
            VisualCategory categorySource = visualCategoryService.getOne(new LambdaQueryWrapper<VisualCategory>()
                    .eq(VisualCategory::getCategoryKey, category.getCategoryKey())
                    .eq(VisualCategory::getCategoryValue, category.getCategoryValue()));
            roleSourceService.saveRoleSource(categorySource.getId(), SourceType.VISUAL_CATEGORY);

            topoCategoryMaps.put(oldCategoryValue, category);
            topoIdMaps.put(oldId, category.getId().toString());
        }
        //替换子分类的parentId字段
        topoCategoryMaps.forEach((oldCategoryValue, category) -> {
            if (category.getParentId() != null && !"0".equals(category.getParentId())) {
                category.setParentId(topoIdMaps.get(category.getParentId()));
                visualCategoryService.updateById(category);
            }
        });

        HashMap<String, HashMap<String, VisualCategory>> result = new HashMap<>();
        result.put("visualCategoryMaps", visualCategoryMaps);
        result.put("topoCategoryMaps", topoCategoryMaps);
        return result;
    }

    @Override
    public void share(ProjectShare projectShare) throws Exception{
        Project project = projectMapper.selectById(projectShare.getId());
        //更新发布状态
        project.setShared(project.getShared());
        project.setSharedBy(AuthUtils.getUser().getUsername());
        project.setShareTime(new Date());
        projectMapper.updateById(project);

        //更新组态大屏发布状态
        QueryWrapper<TopologyData> topologyDataQueryWrapper = new QueryWrapper<>();
        topologyDataQueryWrapper.eq("project_id", project.getId());
        List<TopologyData> topologyDataList = topologyDataService.list(topologyDataQueryWrapper);
        List<String> idList = topologyDataList.stream().map(TopologyData::getId).collect(Collectors.toList());
        topologyDataService.batchShare(idList, projectShare.getSharedUrlPrefix());

        //更新可视化大屏发布状态
        QueryWrapper<Visual> visualQueryWrapper = new QueryWrapper<>();
        visualQueryWrapper.eq("project_id", project.getId());
        List<Visual> visualList = visualService.list(visualQueryWrapper);
        for (Visual visual : visualList) {
            VisualRelease visualRelease = releaseMapper.selectById(visual.getId());
            if (visualRelease == null) {
                visualRelease = new VisualRelease();
                visualRelease.setId(visual.getId());
                visualRelease.setPath(visual.getId().toString());
                visualRelease.setIsCustom(1);
            }
            visualRelease.setIsRelease(1);
            visualRelease.setVersion("v1");
            visualReleaseService.releaseVisual(visualRelease);
        }
    }

    @Override
    public void batchShare(ProjectShare projectShare) throws Exception {
        String[] idList = projectShare.getIdList();
        ProjectShare newProjectShare = new ProjectShare();
        for (String id : idList) {
            newProjectShare.setId(id);
            newProjectShare.setShare(projectShare.getShare());
            newProjectShare.setSharedUrlPrefix(projectShare.getSharedUrlPrefix());
            this.share(newProjectShare);
        }
    }

    @Override
    public void deleteById(String id) throws Exception {
        QueryWrapper<TopologyData> topologyQueryWrapper = new QueryWrapper<>();
        topologyQueryWrapper.eq("project_id", id);
        List<TopologyData> topologyDataList = topologyDataService.list(topologyQueryWrapper);
        if (ObjectUtil.isNotEmpty(topologyDataList)) {
            BaseException.throwException("工程存在组态大屏不能删除");
        }
        QueryWrapper<Visual> visualQueryWrapper = new QueryWrapper<>();
        visualQueryWrapper.eq("project_id", id);
        List<Visual> visualList = visualService.list(visualQueryWrapper);
        if (ObjectUtil.isNotEmpty(visualList)) {
            BaseException.throwException("工程存在可视化大屏不能删除");
        }
        projectMapper.deleteById(id);
    }

    @Override
    public void copy(String id) throws Exception {
        //复制工程
        Project project = projectMapper.selectById(id);
        project.setId(null);
        project.setName(project.getName() + " - 副本");
        project.setCreateBy(AuthUtils.getUser().getUsername());
        project.setCreateTime(new Date());
        project.setUpdateBy(null);
        project.setUpdateTime(null);
        project.setSharedBy(null);
        project.setShareTime(null);
        projectMapper.insert(project);

        //复制分类
        copyCategory(id, project.getId());
        //复制组态
        //copyTopologyData(id, project.getId());
        //复制可视化
        //copyVisual(id, project.getId());
    }

    //复制分类
    void copyCategory(String projectId, String newProjectId) throws Exception {
        QueryWrapper<VisualCategory> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.eq("project_id", projectId);
        categoryQueryWrapper.eq("parent_id", 0);
        List<VisualCategory> visualCategoryList = visualCategoryService.list(categoryQueryWrapper);
        //遍历parentId为0的父级分类
        for (VisualCategory visualCategory : visualCategoryList) {
            String oldCategoryValue = visualCategory.getCategoryValue();

            //复制前visualCategory.id
            String oldCategoryId = visualCategory.getId().toString();
            Long snowId = idGenerator.nextId();
            visualCategory.setId(snowId);
            visualCategory.setParentId("0");
            visualCategory.setProjectId(newProjectId);//复制后的工程id
            visualCategory.setCreateTime(System.currentTimeMillis());
            visualCategoryService.saveCategory(visualCategory);

            //复制子分类
            copyChildCategory(visualCategory, oldCategoryId, projectId, newProjectId);

            //添加权限
            VisualCategory getOne = visualCategoryService.getOne(new LambdaQueryWrapper<VisualCategory>()
                    .eq(VisualCategory::getCategoryKey, visualCategory.getCategoryKey())
                    .eq(VisualCategory::getCategoryValue, visualCategory.getCategoryValue()));
            roleSourceService.saveRoleSource(getOne.getId(), SourceType.VISUAL_CATEGORY);

            //复制组态
            copyTopologyData(oldCategoryValue, visualCategory.getCategoryValue(), projectId, newProjectId);
            //复制可视化
            copyVisual(oldCategoryValue, visualCategory.getCategoryValue(), projectId, newProjectId);
        }
    }

    //复制子分类
    void copyChildCategory(VisualCategory visualCategory, String oldCategoryId, String projectId, String newProjectId) throws Exception {
        QueryWrapper<VisualCategory> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.eq("project_id", projectId);
        categoryQueryWrapper.eq("parent_id", visualCategory.getId());
        List<VisualCategory> visualCategoryList = visualCategoryService.list(categoryQueryWrapper);
        for (VisualCategory category : visualCategoryList) {
            String oldCategoryValue = category.getCategoryValue();

            Long snowId = idGenerator.nextId();
            category.setId(snowId);
            category.setParentId(oldCategoryId);
            category.setProjectId(newProjectId);//复制后的工程id
            category.setCreateTime(System.currentTimeMillis());
            visualCategoryService.saveCategory(category);

            //复制子分类
            copyChildCategory(category, oldCategoryId, projectId, newProjectId);

            //添加权限
            VisualCategory getOne = visualCategoryService.getOne(new LambdaQueryWrapper<VisualCategory>()
                    .eq(VisualCategory::getCategoryKey, category.getCategoryKey())
                    .eq(VisualCategory::getCategoryValue, category.getCategoryValue()));
            roleSourceService.saveRoleSource(getOne.getId(), SourceType.VISUAL_CATEGORY);

            //复制组态
            copyTopologyData(oldCategoryValue, category.getCategoryValue(), projectId, newProjectId);
            //复制可视化
            copyVisual(oldCategoryValue, category.getCategoryValue(), projectId, newProjectId);
        }
    }

    void copyTopologyData(String oldCategoryValue, String newCategoryValue, String projectId, String newProjectId) throws Exception {
        QueryWrapper<TopologyData> topologyQueryWrapper = new QueryWrapper<>();
        topologyQueryWrapper.eq("project_id", projectId);
        topologyQueryWrapper.eq("category_value", oldCategoryValue);
        List<TopologyData> topologyDataList = topologyDataService.list(topologyQueryWrapper);
        for (TopologyData topologyData : topologyDataList) {
            Long snowId = idGenerator.nextId();
            JSONObject jsonObject = JsonUtil.parseObject(topologyData.getData());
            jsonObject.put("id", snowId.toString());
            jsonObject.put("name", topologyData.getName() + " - 副本");
            jsonObject.put("shared", false);
            jsonObject.put("sharedCustom", false);
            jsonObject.put("sharedEncryption", false);
            jsonObject.put("sharedPassword", "");
            jsonObject.put("sharedUrl", "");
            jsonObject.put("path", "");
            jsonObject.put("categoryValue", newCategoryValue);

            topologyData.setData(jsonObject.toJSONString());
            topologyData.setId(snowId.toString());
            topologyData.setProjectId(newProjectId);
            topologyData.setName(topologyData.getName() + " - 副本");
            topologyData.setCreateBy(AuthUtils.getUser().getUsername());
            topologyData.setCreateTime(new Date());
            topologyData.setUpdateBy(null);
            topologyData.setUpdateTime(null);
            topologyData.setPath(null);
            topologyData.setShared(false);
            topologyData.setSharedCustom(false);
            topologyData.setSharedEncryption(false);
            topologyData.setSharedPassword(null);
            topologyData.setSharedUrl(null);
            topologyData.setCategoryValue(newCategoryValue);
            topologyDataService.save(topologyData);
        }
    }

    void copyVisual(String oldCategoryValue, String newCategoryValue, String projectId, String newProjectId) throws Exception {
        QueryWrapper<Visual> visualQueryWrapper = new QueryWrapper<>();
        visualQueryWrapper.eq("project_id", projectId);
        visualQueryWrapper.eq("category", oldCategoryValue);
        List<Visual> visualList = visualService.list(visualQueryWrapper);
        for (Visual visualData : visualList) {
            Long snowId = idGenerator.nextId();
            VisualDTO visualDTO = visualService.detail(visualData.getId());
            Visual visual = visualDTO.getVisual();
            visual.setId(snowId);
            visual.setTitle(visualData.getTitle() + " - 副本");
            visual.setProjectId(newProjectId);
            visual.setCategory(newCategoryValue);
            visual.setUpdateTime(null);
            visualDTO.setVisual(visual);

            VisualConfig config = visualDTO.getConfig();
            JSONObject detailJsonObject = JsonUtil.parseObject(config.getDetail());
            detailJsonObject.put("name", visualData.getTitle() + " - 副本");
            config.setId(idGenerator.nextId());
            config.setVisualId(snowId);
            visualDTO.setConfig(config);

            visualService.saveVisual(visualDTO);
        }
    }

    /**
     * 上传文件
     * @param source
     * @param type/import
     * @param fileName
     * @param inputStream
     * @return
     */
    public OssFile saveOssFileWithFileName(String source, String type, String fileName, InputStream inputStream) {
        OssFileVO ossFileVO = ossTemplate.putFile(StdcVisualConstant.OSS_TOPOLOGY_PREFIX_BUCKET, source + StringPool.SLASH +fileNameHandler(fileName, type), inputStream);
        if (ObjectUtil.isEmpty(ossFileVO)){
            BaseException.throwException(ResultCode.get("upload_file_error"));
        }
        OssFile ossFile = new OssFile();
        String id = StringUtil.randomUUID();
        long timeMillis = System.currentTimeMillis();
        String namePrefix = null;
        ossFile.setId(id)
                .setNamePrefix(StringUtil.hasText(namePrefix) ? namePrefix : getNameWithoutExtension(fileName))
                .setNameSuffix(getFileExtension(fileName))
                .setLink(ossFileVO.getLink())
                .setDoMain(ossFileVO.getDoMain())
                .setType(type)
                .setSystemType(source)
                .setIsDeleted(0)
                .setCreateTime(timeMillis)
                .setUpdateTime(timeMillis);
        return ossFile;
    }

    /**
     * 文件分类处理-处理名称
     * @param type
     * @return
     */
    private String fileNameHandler(String fileName,String type){

        OssFileType fileType = OssFileType.get(type);
        //校验文件格式
        String fileNameSuffix = getFileExtension(fileName);
        if (OssFileType.isNotInValues(fileType,fileNameSuffix)){
            BaseException.throwException(ResultCode.get("file_format_error"));
        }
        return fileType.getKey() + StringPool.SLASH + fileName;
    }


}
