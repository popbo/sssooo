package com.stdc.topology2d.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.stdc.project.mapper.ProjectMapper;
import com.stdc.topology2d.constants.ModelConstants;
import com.stdc.topology2d.constants.TopologyConstants;
import com.stdc.topology2d.entity.dto.TopologyDataDto;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.entity.vo.EngineeringGlobalSynchronizationFrom;
import com.stdc.topology2d.entity.vo.TopologyDataQueryVo;
import com.stdc.topology2d.entity.vo.TopoloyDateJumpVo;
import com.stdc.topology2d.mapper.TopologyDataMapper;
import com.stdc.topology2d.service.ITopologyDataService;
import com.stdc.topology2d.util.SnowflakeIdGenerator;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.*;
import com.stdc.visual.entity.po.OssFile;
import com.stdc.visual.entity.po.VisualCategory;
import com.stdc.visual.mapper.OssFileMapper;
import com.stdc.visual.service.IVisualCategoryService;
import com.stdc.visual.service.IVisualReleaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.stdc.core.oss.utils.FileUtil.*;
import static com.stdc.visual.entity.po.OssFile.FILE_SOURCE_TOPOLOGY_2D;

@Slf4j
@Service
public class TopologyDataServiceImpl extends ServiceImpl<TopologyDataMapper, TopologyData> implements ITopologyDataService {


    /**
     * 完整版导出,导出文件缓存地址
     */
    private static final String EXPORT_FILE_CACHE_PATH = "./static/export";

    private static final String TOPOLOGY2D_JSON_NAME = "topo2d.json";
    private static final String TOPOLOGY2D_FILE_JSON_NAME = "file.json";
    private static final String TOPOLOGY2D_BACKGROUND_JSON_NAME = "background_file.json";
    private static final Map<String, TopoloyDateJumpVo> storedDataMap = new HashMap<>();

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Autowired
    private TopologyDataMapper topologyDataMapper;

    @Autowired
    private OssFileMapper ossFileMapper;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private CommonThreadPool commonThreadPool;

    @Autowired
    private RoleSourceService roleSourceService;

    @Autowired
    private IVisualReleaseService visualReleaseService;

    @Autowired
    private IVisualCategoryService visualCategoryService;

    @Autowired
    private ProjectMapper projectMapper;


    @Override
    public IPage<TopologyData> getAllList(TopologyDataQueryVo query, QueryWrapper<TopologyData> queryWrapper) throws Exception {
        //categoryValueList记录当前目录和所有子目录的categoryValue
        List<String> categoryValueList = new ArrayList<>();
        //查询当前目录是否有子目录
        if (StringUtil.isBlank(query.getCategoryValue())) {
            Page<TopologyData> page = baseMapper.selectPage(new Page<>(query.getCurrent(), query.getPageSize()), queryWrapper);
            return page;
        }
        VisualCategory visualCategory = visualCategoryService.getByCategoryValue(query.getCategoryValue());
        //当前目录如果有子目录 则递归查询所有子目录 并添加子目录的categoryValue到categoryValueList中
        getTopologyData(visualCategory, categoryValueList);
        //添加当前目录的categoryValue
        categoryValueList.add(query.getCategoryValue());
        //System.out.println(categoryValueList);
        //根据categoryValue查询所有大屏数据
        if (categoryValueList != null && categoryValueList.size() > 0) {
            queryWrapper.in(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, categoryValueList);
            Page<TopologyData> page = baseMapper.selectPage(new Page<>(query.getCurrent(), query.getPageSize()), queryWrapper);
            return page;
        }
        return new Page<>();
    }

    //递归获取当前查询目录的所有子目录下 记录其categoryValue
    private void getTopologyData(VisualCategory visualCategory, List<String> categoryValueList) {
        if (visualCategory != null) {
            LambdaQueryWrapper<VisualCategory> queryWrapper = new LambdaQueryWrapper<VisualCategory>();
            queryWrapper.eq(VisualCategory::getParentId, visualCategory.getId());
            List<VisualCategory> visualCategories = visualCategoryService.list(queryWrapper);
            if (visualCategories != null) {
                visualCategories.forEach(bean -> {
                    categoryValueList.add(bean.getCategoryValue());
                    getTopologyData(bean, categoryValueList);
                });
            }
        }
    }

    @Override
    public void share(TopologyDataDto topologyDataDto) throws Exception {
        TopologyData topologyData = baseMapper.selectById(topologyDataDto.getId());
        if (null == topologyData) {
            BaseException.throwException(ResultCode.get("数据为空"));
        }
        String data = topologyData.getData();
        //BeanUtils.copyBean(topologyData, topologyDataDto);
        JSONObject jsonObject = JsonUtil.parseObject(data);
        jsonObject.put("shared", topologyDataDto.getShared());
        jsonObject.put("sharedCustom", topologyDataDto.getSharedCustom());
        jsonObject.put("sharedEncryption", topologyDataDto.getSharedEncryption());
        jsonObject.put("sharedPassword", topologyDataDto.getSharedPassword());
        jsonObject.put("sharedUrl", topologyDataDto.getSharedUrl());
        jsonObject.put("path", topologyDataDto.getPath());
        jsonObject.put("version", topologyDataDto.getVersion());

        topologyData.setData(jsonObject.toJSONString());
        topologyData.setUpdateTime(new Date());
        topologyData.setPath(topologyDataDto.getPath());
        topologyData.setShared(topologyDataDto.getShared());
        topologyData.setSharedCustom(topologyDataDto.getSharedCustom());
        topologyData.setSharedEncryption(topologyDataDto.getSharedEncryption());
        topologyData.setSharedPassword(topologyDataDto.getSharedPassword());
        topologyData.setVersion(topologyDataDto.getVersion());
        topologyData.setSharedUrl(topologyDataDto.getSharedUrl());
        baseMapper.updateById(topologyData);

        //大屏发布信息
        VisualRelease visualRelease = visualReleaseService.getById(topologyDataDto.getId());
        // 未存在 即保存
        if (ObjectUtil.isEmpty(visualRelease)){
            //筛序地址是否重复
            VisualRelease isExit = visualReleaseService.getOne(new LambdaQueryWrapper<VisualRelease>()
                    .eq(VisualRelease::getPath, topologyDataDto.getPath())
                    .eq(VisualRelease::getSource, 1));
            if (ObjectUtil.isNotEmpty(isExit)){
                BaseException.throwException(ResultCode.get("this_release_path_is_exit"));
            }
        } else { //大屏发布信息已存在 即修改
            //筛序地址是否重复
            VisualRelease isExit = visualReleaseService.getOne(new LambdaQueryWrapper<VisualRelease>()
                    .eq(VisualRelease::getPath, topologyDataDto.getPath())
                    .eq(VisualRelease::getSource, 1)
                    .ne(VisualRelease::getId, topologyDataDto.getId()));
            if (ObjectUtil.isNotEmpty(isExit)){
                BaseException.throwException(ResultCode.get("this_release_path_is_exit"));
            }
        }
        if (visualRelease == null) {
            visualRelease = new VisualRelease();
        }
        visualRelease.setId(Long.valueOf(topologyDataDto.getId()));
        visualRelease.setSource(1);
        visualRelease.setVersion(topologyDataDto.getVersion());
        visualRelease.setPsw(topologyDataDto.getSharedPassword());
        visualRelease.setPath(topologyDataDto.getPath());
        visualRelease.setIsEnc(topologyDataDto.getSharedEncryption() ? 1 : 0);
        visualRelease.setIsRelease(topologyDataDto.getShared() ? 1 : 0);
        visualRelease.setIsCustom(topologyDataDto.getSharedCustom() ? 1 : 0);
        visualRelease.setUsername(AuthUtils.getUser().getUsername());
        visualRelease.setComponent(topologyData.getData());
        visualReleaseService.saveOrUpdate(visualRelease);
    }


    @Override
    public void batchShare(List<String> idList, String sharedUrlPrefix) throws Exception {
        for (String id : idList) {
            //组态大屏
            TopologyData topologyData = baseMapper.selectById(id);
            Boolean shared = topologyData.getShared();
            //未发布
            if (shared==null || !shared) {
                String data = topologyData.getData();
                JSONObject jsonObject = JsonUtil.parseObject(data);
                jsonObject.put("shared", true);
                jsonObject.put("sharedCustom", true);
                jsonObject.put("sharedEncryption", false);
                jsonObject.put("sharedPassword", "");
                jsonObject.put("sharedUrl", sharedUrlPrefix + "/" + id);
                jsonObject.put("path", id);
                jsonObject.put("version", "2.0.0");

                topologyData.setData(jsonObject.toJSONString());
                topologyData.setPath(id);
                topologyData.setShared(true);
                topologyData.setSharedCustom(true);
                topologyData.setSharedEncryption(false);
                topologyData.setSharedPassword("");
                topologyData.setVersion("2.0.0");
                topologyData.setSharedUrl(sharedUrlPrefix + "/" + id);
                topologyData.setUpdateTime(new Date());
                baseMapper.updateById(topologyData);
            }

            //大屏发布信息
            VisualRelease visualRelease = visualReleaseService.getById(id);
            // 未存在 即保存
            if (ObjectUtil.isEmpty(visualRelease)){
                //筛序地址是否重复
                VisualRelease isExit = visualReleaseService.getOne(new LambdaQueryWrapper<VisualRelease>()
                        .eq(VisualRelease::getPath, topologyData.getPath())
                        .eq(VisualRelease::getSource, 1));
                if (ObjectUtil.isNotEmpty(isExit)){
                    BaseException.throwException(ResultCode.get("this_release_path_is_exit"));
                }
                visualRelease = new VisualRelease();
                visualRelease.setId(Long.valueOf(id));
                visualRelease.setSource(1);
                visualRelease.setPsw("");
                visualRelease.setPath(id);
                visualRelease.setIsEnc(0);
                visualRelease.setIsCustom(1);
                visualRelease.setIsRelease(1);
                visualRelease.setUsername(AuthUtils.getUser().getUsername());
                visualRelease.setComponent(topologyData.getData());
                visualReleaseService.save(visualRelease);
            } else { //大屏发布信息已存在 即修改
                //筛序地址是否重复
                VisualRelease isExit = visualReleaseService.getOne(new LambdaQueryWrapper<VisualRelease>()
                        .eq(VisualRelease::getPath, topologyData.getPath())
                        .eq(VisualRelease::getSource, 1)
                        .ne(VisualRelease::getId, topologyData.getId()));
                if (ObjectUtil.isNotEmpty(isExit)){
                    BaseException.throwException(ResultCode.get("this_release_path_is_exit"));
                }
                if (!shared) {
                    visualRelease.setPath(id);
                }
                visualRelease.setIsRelease(1);
                visualRelease.setIsCustom(1);
                visualRelease.setComponent(topologyData.getData());
                visualRelease.setUsername(AuthUtils.getUser().getUsername());
                visualReleaseService.updateById(visualRelease);
            }
        }
    }

    @Override
    @Transactional
    public Map<String, Object> saveByData(Map<String, Object> dataMap) {
        TopologyData topologyData = new TopologyData();
        //新增的时候保存用户和创建时间
        SysUserDTO user = AuthUtils.getUser();
        if (topologyData.getId() == null) {
            if (user != null) {
                topologyData.setUsername(user.getUsername());
                topologyData.setCreateBy(user.getUsername());
            }
            topologyData.setCreateTime(new Date());
        } else {
            if (user != null) {
                topologyData.setUpdateBy(user.getUsername());
            }
            topologyData.setUpdateTime(new Date());
        }
        //处理数据
        if (!ObjectUtils.isEmpty(dataMap)) {
            if (dataMap.containsKey("id")) {
                topologyData.setId(dataMap.get("id").toString());
            } else {
                // 新增时预获取雪花算法生成的id
                Long snowId = idGenerator.nextId();
                dataMap.put("id", snowId.toString());
                topologyData.setId(snowId.toString());
            }
            topologyData.setData(JSONObject.toJSONString(dataMap));
            //文件名
            if (dataMap.containsKey("name")) {
                topologyData.setName(String.valueOf(dataMap.get("name")));
            }
            //文件夹
            if (dataMap.containsKey("collection")) {
                topologyData.setCollection(String.valueOf(dataMap.get("collection")));
            }
            //外部id(比如:物联网id)
            if (dataMap.containsKey("externalId")) {
                topologyData.setExternalId(String.valueOf(dataMap.get("externalId")));
            }
            //图纸是否分享（分享true，未分享false）
            if (dataMap.containsKey("shared")) {
                topologyData.setShared((Boolean) dataMap.get("shared"));
            }
            //版本号
            if (dataMap.containsKey("version")) {
                topologyData.setVersion(String.valueOf(dataMap.get("version")));
            }
            //分享链接
            if (dataMap.containsKey("sharedUrl")) {
                topologyData.setSharedUrl(String.valueOf(dataMap.get("sharedUrl")));
            }
            //分享是否自定义（自定义为true）
            if (dataMap.containsKey("sharedCustom")) {
                topologyData.setSharedCustom((Boolean) dataMap.get("sharedCustom"));
            }
            //分享加密
            if (dataMap.containsKey("sharedEncryption")) {
                topologyData.setSharedEncryption((Boolean) dataMap.get("sharedEncryption"));
            }
            //分享密码
            if (dataMap.containsKey("sharedPassword")) {
                if (!ObjectUtil.isEmpty(dataMap.get("sharedPassword"))) {
                    topologyData.setSharedPassword(String.valueOf(dataMap.get("sharedPassword")));
                }
            }
            //自定义封面图片
            if (dataMap.containsKey("sharedImage")) {
                topologyData.setSharedImage(String.valueOf(dataMap.get("sharedImage")));
            }
            //封面类型，1默认，2缩略图，3自定义封面
            if (dataMap.containsKey("visualType")) {
                topologyData.setVisualType(String.valueOf(dataMap.get("visualType")));
            }
            //缩略图图片地址
            if (dataMap.containsKey("image")) {
                topologyData.setImage(String.valueOf(dataMap.get("image")));
            }
            //可视化分类
            if (dataMap.containsKey("categoryValue")) {
                topologyData.setCategoryValue(String.valueOf(dataMap.get("categoryValue")));
            } else {
                topologyData.setCategoryValue("topo2dnocategorykey9999999999999");
            }
            //可视化分类值
            if (dataMap.containsKey("categoryKey")) {
                topologyData.setCategoryKey(String.valueOf(dataMap.get("categoryKey")));
            } else {
                topologyData.setCategoryKey("未分类");
            }
            //分类模版类型
            if (dataMap.containsKey("moduleType")) {
                topologyData.setModuleType(String.valueOf(dataMap.get("moduleType")));
            }
            //图纸编辑密码
            if (dataMap.containsKey("password")) {
                topologyData.setPassword(String.valueOf(dataMap.get("password")));
            }
            //图纸文件夹名
            if (dataMap.containsKey("folder")) {
                topologyData.setFolder(String.valueOf(dataMap.get("folder")));
            } else {
                topologyData.setFolder("未分类");
            }
            if (dataMap.containsKey("projectId")) {
                topologyData.setProjectId(String.valueOf(dataMap.get("projectId")));
            }
        }

        this.saveOrUpdate(topologyData);
        dataMap.put("id", topologyData.getId());

        return dataMap;
    }

    @Override
    public Map<String, Object> visualUpdate(Map<String, Object> dataMap) {
        TopologyData topologyData = new TopologyData();
        //新增的时候保存用户和创建时间
        SysUserDTO user = AuthUtils.getUser();
        topologyData.setUpdateBy(user.getUsername());
        topologyData.setUpdateTime(new Date());
        //处理数据
        TopologyData base = baseMapper.selectById(String.valueOf(dataMap.get("id")));
        JSONObject jsonObject = JsonUtil.parseObject(base.getData());
        jsonObject.put("id", String.valueOf(dataMap.get("id")));
        topologyData.setId(String.valueOf(dataMap.get("id")));
        if (dataMap.containsKey("name")) {
            topologyData.setName(String.valueOf(dataMap.get("name")));
            jsonObject.put("name", String.valueOf(dataMap.get("name")));
        }
        if (dataMap.containsKey("collection")) {
            topologyData.setCollection(String.valueOf(dataMap.get("collection")));
            jsonObject.put("collection", String.valueOf(dataMap.get("collection")));
        }
        if (dataMap.containsKey("sharedImage")) {
            topologyData.setSharedImage(String.valueOf(dataMap.get("sharedImage")));
            jsonObject.put("sharedImage", String.valueOf(dataMap.get("sharedImage")));
        }
        if (dataMap.containsKey("password") && dataMap.get("password") != null) {
            topologyData.setPassword(String.valueOf(dataMap.get("password")));
            jsonObject.put("password", String.valueOf(dataMap.get("password")));
        }
        if (dataMap.containsKey("folder")) {
            topologyData.setFolder(String.valueOf(dataMap.get("folder")));
            jsonObject.put("folder", String.valueOf(dataMap.get("folder")));
        }
        if (dataMap.containsKey("visualType")) {
            topologyData.setVisualType(String.valueOf(dataMap.get("visualType")));
            jsonObject.put("visualType", String.valueOf(dataMap.get("visualType")));
        }
        //可视化分类
        if (dataMap.containsKey("categoryValue")) {
            topologyData.setCategoryValue(String.valueOf(dataMap.get("categoryValue")));
            jsonObject.put("categoryValue", String.valueOf(dataMap.get("categoryValue")));
        } else {
            topologyData.setCategoryValue("topo2dnocategorykey9999999999999");
            jsonObject.put("categoryValue", "topo2dnocategorykey9999999999999");
        }
        //可视化分类值
        if (dataMap.containsKey("categoryKey")) {
            topologyData.setCategoryKey(String.valueOf(dataMap.get("categoryKey")));
        } else {
            topologyData.setCategoryKey("未分类");
        }
        topologyData.setData(jsonObject.toJSONString());
        this.updateById(topologyData);
        return dataMap;
    }

    @Override
    public void copyfile(TopologyData topologyData) throws Exception {
        topologyData.setId(null);
        topologyData.setCreateTime(new Date());
        topologyData.setUpdateTime(new Date());
        topologyData.setShared(false);
        topologyData.setSharedUrl("");
        //String data = buildUserJsonStrForSreen(topologyData);
        //topologyData.setData(data);
        baseMapper.insert(topologyData);
    }



    @Override
    public String fullExport(String id) throws Exception {
        String ret = null;
        TopologyData topologyData = baseMapper.selectById(id);

        //OssFile ossFile = ossFileMapper.selectById(id);
        LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OssFile::getVisualId, id);
        queryWrapper.eq(OssFile::getSource, "TOPOLOGY_2D");
        //大屏文件数据
        List<OssFile> ossFileList = ossFileMapper.selectList(queryWrapper);
        //封面图
        OssFile backgroundFile = ossFileMapper.queryBackGroundWithVisualId(Long.valueOf(id));
        //保存大屏配置信息和大屏文件
        //缓存地址
        String cachePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + StringUtil.randomUUID();
        //大屏配置信息
        JsonUtil.writeJsonToFile(topologyData.getData(),cachePath + StringPool.SLASH + TOPOLOGY2D_JSON_NAME);
        //保存数据文件信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(ossFileList),cachePath + StringPool.SLASH + TOPOLOGY2D_FILE_JSON_NAME);
        //保存封面图信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(backgroundFile),cachePath + StringPool.SLASH + TOPOLOGY2D_BACKGROUND_JSON_NAME);

        //下载图片文件到本地
        long currentTimeMillis = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(ossFileList.size());
        ossFileList.stream().forEach(ossFile -> {
            commonThreadPool.addTask(()->{
                try {
                    String saveDir = cachePath + StringPool.SLASH + ossFile.getSystemType() + StringPool.SLASH + ossFile.getType();
                    downloadFileFromNetWithOriginalName(ossFile.getLink(),saveDir,5000,10000);
                } catch (IOException e) {
                    log.error("下载文件失败-->"+ossFile.getLink());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        });
        latch.await();
        log.info("-------->下载图片地址完成,耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");

        //将本地目录打成zip文件并且写入响应体
        //压缩包文件名称
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + topologyData.getName() + ".zip";
        String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;
        FileInputStream inputStream =null;
        try {
            //将文件夹压缩为zip包并且导入到本地
            createFile(zipSavePath);
            ZipFileUtils.writeFolderToZip(cachePath,zipSavePath);
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
            delFile(cachePath);
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
    public String dirExportAll(String categoryValue) throws Exception {
        String ret = "";

        //未分类
        if ("topo2dnocategorykey9999999999999".equals(categoryValue)) {
            ret = exportNoDir(categoryValue);
            return ret;
        }

        //categoryValueList记录当前目录和所有子目录的categoryValue
        List<String> categoryValueList = new ArrayList<>();
        //查询当前目录是否有子目录
        VisualCategory visualCategory = visualCategoryService.getByCategoryValue(categoryValue);
        //当前目录如果有子目录 则递归查询所有子目录 并添加子目录的categoryValue到categoryValueList中
        getTopologyData(visualCategory, categoryValueList);
        //添加当前目录的categoryValue
        categoryValueList.add(categoryValue);
        //根据categoryValue查询所有大屏数据
        if (categoryValueList != null && categoryValueList.size() > 0) {
            QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, AuthUtils.getUser().getUsername());
            queryWrapper.in(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, categoryValueList);
            List<TopologyData> list = baseMapper.selectList(queryWrapper);

            String categoryKey = visualCategory.getCategoryKey(); //菜单名称
            String downFilePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + categoryKey; //本地缓存
            for (TopologyData topologyData : list) {
                LambdaQueryWrapper<OssFile> ossQueryWrapper = new LambdaQueryWrapper<>();
                ossQueryWrapper.eq(OssFile::getVisualId, topologyData.getId());
                ossQueryWrapper.eq(OssFile::getSource, "TOPOLOGY_2D");
                //大屏文件数据
                List<OssFile> ossFileList = ossFileMapper.selectList(ossQueryWrapper);
                //封面图
                OssFile backgroundFile = ossFileMapper.queryBackGroundWithVisualId(Long.valueOf(topologyData.getId()));
                //保存大屏配置信息和大屏文件
                String fileName = categoryKey + StringPool.SLASH + topologyData.getName();
                String fileSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + fileName;
                //大屏配置信息
                JsonUtil.writeJsonToFile(topologyData.getData(),fileSavePath + StringPool.SLASH + TOPOLOGY2D_JSON_NAME);
                //保存数据文件信息
                JsonUtil.writeJsonToFile(JSON.toJSONString(ossFileList),fileSavePath + StringPool.SLASH + TOPOLOGY2D_FILE_JSON_NAME);
                //保存封面图信息
                JsonUtil.writeJsonToFile(JSON.toJSONString(backgroundFile),fileSavePath + StringPool.SLASH + TOPOLOGY2D_BACKGROUND_JSON_NAME);

                //下载图片文件到本地
                long currentTimeMillis = System.currentTimeMillis();
                CountDownLatch latch = new CountDownLatch(ossFileList.size());
                ossFileList.stream().forEach(ossFile -> {
                    commonThreadPool.addTask(()->{
                        try {
                            String saveDir = fileSavePath + StringPool.SLASH + ossFile.getSystemType() + StringPool.SLASH + ossFile.getType();
                            downloadFileFromNetWithOriginalName(ossFile.getLink(),saveDir,5000,10000);
                        } catch (IOException e) {
                            log.error("下载文件失败-->"+ossFile.getLink());
                            e.printStackTrace();
                        } finally {
                            latch.countDown();
                        }
                    });
                });
                latch.await();
                log.info("-------->下载图片地址完成,耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");
            }

            //下载压缩包
            String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + categoryKey + ".zip";
            String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;
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
        }

        return ret;
    }

    @Override
    public String dirExport(String categoryValue) throws Exception {
        String ret = "";

        //未分类
        if ("topo2dnocategorykey9999999999999".equals(categoryValue)) {
            ret = exportNoDir(categoryValue);
            return ret;
        }

        VisualCategory visualCategory = visualCategoryService.getByCategoryValue(categoryValue);
        String categoryKey = visualCategory.getCategoryKey(); //菜单名称
        String downFilePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + categoryKey; //本地缓存

        //递归下载当前菜单下大屏数据
        export(null, categoryValue, null, null);

        //下载压缩包
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + categoryKey + ".zip";
        String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;
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

    //下载当前菜单下大屏数据
    private void export(String parentCategoryKey, String categoryValue, String projectId, String projectName) throws Exception {
        VisualCategory visualCategory = visualCategoryService.getByCategoryValue(categoryValue);
        QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
        SysUserDTO user = AuthUtils.getUser();
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, user.getUsername());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, categoryValue);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_PROJECTID_PROPERTY, projectId);
        List<TopologyData> list = baseMapper.selectList(queryWrapper);

        String categoryKey = null;
        if (StringUtil.isBlank(parentCategoryKey)) {
            categoryKey = visualCategory.getCategoryKey();
            if (!StringUtil.isBlank(projectName)) {
                categoryKey =  projectName + StringPool.SLASH + "组态" + StringPool.SLASH + categoryKey;
            }
        } else {
            categoryKey = parentCategoryKey + StringPool.SLASH + visualCategory.getCategoryKey();
        }
        for (TopologyData topologyData : list) {
            try {
                downloadDirData(categoryKey, topologyData.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //递归下载子菜单大屏
        if (visualCategory != null) {
            LambdaQueryWrapper<VisualCategory> dirQueryWrapper = new LambdaQueryWrapper<VisualCategory>();
            dirQueryWrapper.eq(VisualCategory::getParentId, visualCategory.getId());
            dirQueryWrapper.eq(VisualCategory::getProjectId, projectId);
            List<VisualCategory> visualCategories = visualCategoryService.list(dirQueryWrapper);
            if (visualCategories != null && visualCategories.size() > 0) {
                String finalCategoryKey = categoryKey;
                visualCategories.forEach(bean -> {
                    try {
                        export(finalCategoryKey, bean.getCategoryValue(), projectId, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    //下载大屏数据
    private String downloadDirData(String categoryKey, String id) throws Exception {
        TopologyData topologyData = baseMapper.selectById(id);

        LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OssFile::getVisualId, id);
        queryWrapper.eq(OssFile::getSource, "TOPOLOGY_2D");
        //大屏文件数据
        List<OssFile> ossFileList = ossFileMapper.selectList(queryWrapper);
        //封面图
        OssFile backgroundFile = ossFileMapper.queryBackGroundWithVisualId(Long.valueOf(id));
        //保存大屏配置信息和大屏文件
        String fileName = categoryKey + StringPool.SLASH + topologyData.getName();
        String fileSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + fileName;
        //大屏配置信息
        JsonUtil.writeJsonToFile(topologyData.getData(),fileSavePath + StringPool.SLASH + TOPOLOGY2D_JSON_NAME);
        //保存数据文件信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(ossFileList),fileSavePath + StringPool.SLASH + TOPOLOGY2D_FILE_JSON_NAME);
        //保存封面图信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(backgroundFile),fileSavePath + StringPool.SLASH + TOPOLOGY2D_BACKGROUND_JSON_NAME);

        //下载图片文件到本地
        long currentTimeMillis = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(ossFileList.size());
        ossFileList.stream().forEach(ossFile -> {
            commonThreadPool.addTask(()->{
                try {
                    String saveDir = fileSavePath + StringPool.SLASH + ossFile.getSystemType() + StringPool.SLASH + ossFile.getType();
                    downloadFileFromNetWithOriginalName(ossFile.getLink(),saveDir,5000,10000);
                } catch (IOException e) {
                    log.error("下载文件失败-->"+ossFile.getLink());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        });
        latch.await();
        log.info("-------->下载图片地址完成,耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");

        return fileSavePath;
    }

    private String exportNoDir(String categoryKey) throws Exception{
        String ret = null;
        QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, AuthUtils.getUser().getUsername());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_CATEGORYVALUE_PROPERTY, categoryKey);
        List<TopologyData> list = baseMapper.selectList(queryWrapper);
        String downFilePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + categoryKey; //本地缓存
        for (TopologyData topologyData : list) {
            LambdaQueryWrapper<OssFile> ossQueryWrapper = new LambdaQueryWrapper<>();
            ossQueryWrapper.eq(OssFile::getVisualId, topologyData.getId());
            ossQueryWrapper.eq(OssFile::getSource, "TOPOLOGY_2D");
            //大屏文件数据
            List<OssFile> ossFileList = ossFileMapper.selectList(ossQueryWrapper);
            //封面图
            OssFile backgroundFile = ossFileMapper.queryBackGroundWithVisualId(Long.valueOf(topologyData.getId()));
            //保存大屏配置信息和大屏文件
            String fileName = categoryKey + StringPool.SLASH + topologyData.getName();
            String fileSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + fileName;
            //大屏配置信息
            JsonUtil.writeJsonToFile(topologyData.getData(),fileSavePath + StringPool.SLASH + TOPOLOGY2D_JSON_NAME);
            //保存数据文件信息
            JsonUtil.writeJsonToFile(JSON.toJSONString(ossFileList),fileSavePath + StringPool.SLASH + TOPOLOGY2D_FILE_JSON_NAME);
            //保存封面图信息
            JsonUtil.writeJsonToFile(JSON.toJSONString(backgroundFile),fileSavePath + StringPool.SLASH + TOPOLOGY2D_BACKGROUND_JSON_NAME);

            //下载图片文件到本地
            long currentTimeMillis = System.currentTimeMillis();
            CountDownLatch latch = new CountDownLatch(ossFileList.size());
            ossFileList.stream().forEach(ossFile -> {
                commonThreadPool.addTask(()->{
                    try {
                        String saveDir = fileSavePath + StringPool.SLASH + ossFile.getSystemType() + StringPool.SLASH + ossFile.getType();
                        downloadFileFromNetWithOriginalName(ossFile.getLink(),saveDir,5000,10000);
                    } catch (IOException e) {
                        log.error("下载文件失败-->"+ossFile.getLink());
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            });
            latch.await();
            log.info("-------->下载图片地址完成,耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");
        }

        //下载压缩包
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + "未分类" + ".zip";
        String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;
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
    public void topo2dImport(MultipartFile file, String category, String name) throws Exception {
        //获取zip文件包对象
        String zipPath= new File("").getAbsolutePath() + StringPool.SLASH + "static" + StringPool.SLASH + StringUtil.randomUUID() + StringPool.UNDERSCORE +file.getOriginalFilename();
        File convFile = new File(zipPath);
        file.transferTo(convFile);
        ZipFile zipFile = new ZipFile(convFile, Charset.forName("GBK"));


        //获取 topo2d.json 对象
        List<? extends ZipEntry> topo2dJson = zipFile.stream().filter(
                entry -> entry.getName().contains(TOPOLOGY2D_JSON_NAME)).collect(Collectors.toList());
        ZipEntry topo2dJsonZipEntry = null;
        if (!CollectionUtils.isEmpty(topo2dJson)) {
            topo2dJsonZipEntry = topo2dJson.stream().findFirst().get();
        }
        //读取 topo2d.json 内容
        String topo2dStr = null;
        if (ObjectUtil.isNotEmpty(topo2dJsonZipEntry)){
            topo2dStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(topo2dJsonZipEntry))).readLine();
        }
        //topo2d.json转为java对象
        TopologyData topologyData = JsonUtil.toJavaObj(topo2dStr, TopologyData.class);
        //JSONObject jsonObject = JSONObject.parseObject(topo2dStr);
        //TopologyData topologyData = JSONObject.parseObject(topo2dStr, TopologyData.class);

        //获取 background_file.json 对象
        List<? extends ZipEntry> backgroundJson = zipFile.stream().filter(
                entry -> entry.getName().contains(TOPOLOGY2D_BACKGROUND_JSON_NAME)).collect(Collectors.toList());
        ZipEntry backgroundZipEntry = null;
        if (!CollectionUtils.isEmpty(backgroundJson)){
            backgroundZipEntry = backgroundJson.stream().findFirst().get();
        }
        //读取 background_file.json 内容
        String backgroundFileStr = null;
        if (ObjectUtil.isNotEmpty(backgroundJson)){
            backgroundFileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(backgroundZipEntry))).readLine();
        }
        //背景图信息
        OssFile ossBackGroundFile = JsonUtil.toJavaObj(backgroundFileStr, OssFile.class);

        //获取 file.json 对象
        List<? extends ZipEntry> fileJson = zipFile.stream().filter(
                entry -> entry.getName().contains(TOPOLOGY2D_FILE_JSON_NAME) && !entry.getName().contains(TOPOLOGY2D_BACKGROUND_JSON_NAME)).collect(Collectors.toList());
        ZipEntry fileJsonZipEntry = null;
        if (!CollectionUtils.isEmpty(fileJson)){
            fileJsonZipEntry = fileJson.stream().findFirst().get();
        }
        //读取 file.json 内容
        String fileStr = null;
        if (ObjectUtil.isNotEmpty(fileJson)){
            fileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(fileJsonZipEntry))).readLine();
        }
        //获取文件对象jsonArray
        JSONArray fileArray = JSONArray.parseArray(fileStr);
        //文件不为空
        if (!CollectionUtils.isEmpty(fileArray)){
            //通过文件信息数组,将zip包中的文件传到新的服务器上,并且获得新服务器文件的地址
            Map<String,String> linkMap = new HashMap<>(fileArray.size());
            //大屏封面映射地址
            AtomicReference<String> backGroundUrl = new AtomicReference();
            List<OssFile> newFiles = new ArrayList<>(fileArray.size());
            fileArray.forEach(fileJsonObject->{
                //获取文件信息
                OssFile ossFile = JsonUtil.toJavaObj(JSON.toJSONString(fileJsonObject), OssFile.class);
                //比对文件名称,获取对应的文件
                List<? extends ZipEntry> collect = zipFile.stream().filter(entry ->{
                    if (entry.getName().equals(StringPool.SLASH)){
                        return false;
                    }
                    String[] zipNameS = entry.getName().split(StringPool.SLASH);
                    String zipName = zipNameS[zipNameS.length - 1];
                    String[] ossNameS = ossFile.getDoMain().split(StringPool.SLASH);
                    String ossName = ossNameS[ossNameS.length - 1];
                    return StringUtil.equals(zipName, ossName) || zipName.contains(ossName);
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)){
                    try {
                        ZipEntry entity = collect.stream().findFirst().get();
                        //获取上传完成之后的新文件地址
                        String[] nameS = entity.getName().split(StringPool.SLASH);
                        String fileName = StringUtil.randomUUID() + StringPool.DOT + com.stdc.core.oss.utils.FileUtil.getFileExtension(nameS[nameS.length - 1]);
                        OssFile newFile = saveOssFileWithFileName(ossFile.getSystemType(), ossFile.getType(),fileName, zipFile.getInputStream(entity));
                        if (ObjectUtil.isNotEmpty(ossBackGroundFile) && StringUtil.hasText(ossBackGroundFile.getId()) && StringUtil.equals(ossFile.getId(),ossBackGroundFile.getId())){
                            backGroundUrl.set(newFile.getId());
                        }
                        //保存新旧地址的映射关系
                        linkMap.put(ossFile.getLink(),newFile.getLink());
                        linkMap.put(ossFile.getDoMain(),newFile.getDoMain());
                        //存储新文件信息
                        newFiles.add(newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //替换topo2dStr中的文件地址
            for (String oldLink : linkMap.keySet()) {
                String newLink = linkMap.get(oldLink);
                topo2dStr = topo2dStr.replaceAll(oldLink, newLink);
            }
            TopologyData data = JsonUtil.toJavaObj(topo2dStr, TopologyData.class);
            //设置大屏信息
            data.setId(null);
            data.setData(topo2dStr);
            data.setCollection("topo2d");
            data.setCreateTime(new Date());
            SysUserDTO user = AuthUtils.getUser();
            data.setCreateBy(user.getUsername());
            data.setUsername(user.getUsername());
            //保存大屏信息
            this.saveOrUpdate(data);

            if (ObjectUtil.isNotEmpty(data)){
                final String visualId = data.getId();
                System.out.println("id=========" + data.getId());
                newFiles.forEach(newFile->{
                    //将新上传的文件信息记录,插入到数据库
                    newFile.setVisualId(String.valueOf(visualId));
                    newFile.setSource(FILE_SOURCE_TOPOLOGY_2D);
                    int i = ossFileMapper.insert(newFile);
                    if (i > 0) roleSourceService.saveRoleSource(newFile.getId(), SourceType.OSS_FILE);
                });
            }
        } else { //文件数据为空直接新增大屏
            TopologyData data = JsonUtil.toJavaObj(topo2dStr, TopologyData.class);
            //设置大屏信息
            data.setId(null);
            data.setData(topo2dStr);
            data.setCollection("topo2d");
            data.setCreateTime(new Date());
            SysUserDTO user = AuthUtils.getUser();
            data.setCreateBy(user.getUsername());
            data.setUsername(user.getUsername());
            //保存大屏信息
            this.saveOrUpdate(data);
        }

        //关闭
        zipFile.close();
        //删除缓存文件
        delFile(convFile.getPath());
    }

    @Override
    public void topo2dImportFromProject(ZipFile zipFile, String projectId, HashMap<String, VisualCategory> topoCategoryMaps) throws Exception {
        //获取 background_file.json 对象
        List<? extends ZipEntry> backgroundJson = zipFile.stream().filter(
                entry -> entry.getName().contains(TOPOLOGY2D_BACKGROUND_JSON_NAME)).collect(Collectors.toList());
        ZipEntry backgroundZipEntry = null;
        if (!CollectionUtils.isEmpty(backgroundJson)){
            backgroundZipEntry = backgroundJson.stream().findFirst().get();
        }
        //读取 background_file.json 内容
        String backgroundFileStr = null;
        if (ObjectUtil.isNotEmpty(backgroundJson)){
            backgroundFileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(backgroundZipEntry))).readLine();
        }
        //背景图信息
        OssFile ossBackGroundFile = JsonUtil.toJavaObj(backgroundFileStr, OssFile.class);

        /**
         * 由于组态file.json文件里没有内容 无需读取file.json文件
         * */
        //读取 file.json 内容
        String fileStr = null;
        //获取 file.json 对象
        List<? extends ZipEntry> fileJson = zipFile.stream().filter(
                entry -> entry.getName().contains(TOPOLOGY2D_FILE_JSON_NAME) && !entry.getName().contains(TOPOLOGY2D_BACKGROUND_JSON_NAME)).collect(Collectors.toList());
        fileJson.stream().forEach(entry->{
            if (entry.getName().contains("组态")) {
            }
        });

        //获取文件对象jsonArray
        JSONArray fileArray = JSONArray.parseArray(fileStr);
        /**
         * 由于组态file.json文件里没有内容 故 if (!CollectionUtils.isEmpty(fileArray))并不会执行, 执行的是else里的内容
         * */
        //文件不为空
        if (!CollectionUtils.isEmpty(fileArray)){
            //通过文件信息数组,将zip包中的文件传到新的服务器上,并且获得新服务器文件的地址
            Map<String,String> linkMap = new HashMap<>(fileArray.size());
            //大屏封面映射地址
            AtomicReference<String> backGroundUrl = new AtomicReference();
            List<OssFile> newFiles = new ArrayList<>(fileArray.size());
            fileArray.forEach(fileJsonObject->{
                //获取文件信息
                OssFile ossFile = JsonUtil.toJavaObj(JSON.toJSONString(fileJsonObject), OssFile.class);
                //比对文件名称,获取对应的文件
                List<? extends ZipEntry> collect = zipFile.stream().filter(entry ->{
                    if (entry.getName().equals(StringPool.SLASH)){
                        return false;
                    }
                    String[] zipNameS = entry.getName().split(StringPool.SLASH);
                    String zipName = zipNameS[zipNameS.length - 1];
                    String[] ossNameS = ossFile.getDoMain().split(StringPool.SLASH);
                    String ossName = ossNameS[ossNameS.length - 1];
                    return StringUtil.equals(zipName, ossName) || zipName.contains(ossName);
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)){
                    try {
                        ZipEntry entity = collect.stream().findFirst().get();
                        //获取上传完成之后的新文件地址
                        String[] nameS = entity.getName().split(StringPool.SLASH);
                        String fileName = StringUtil.randomUUID() + StringPool.DOT + com.stdc.core.oss.utils.FileUtil.getFileExtension(nameS[nameS.length - 1]);
                        OssFile newFile = saveOssFileWithFileName(ossFile.getSystemType(), ossFile.getType(),fileName, zipFile.getInputStream(entity));
                        if (ObjectUtil.isNotEmpty(ossBackGroundFile) && StringUtil.hasText(ossBackGroundFile.getId()) && StringUtil.equals(ossFile.getId(),ossBackGroundFile.getId())){
                            backGroundUrl.set(newFile.getId());
                        }
                        //保存新旧地址的映射关系
                        linkMap.put(ossFile.getLink(),newFile.getLink());
                        linkMap.put(ossFile.getDoMain(),newFile.getDoMain());
                        //存储新文件信息
                        newFiles.add(newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            //获取 topo2d.json 对象
            List<? extends ZipEntry> topo2dJson = zipFile.stream().filter(
                    entry -> entry.getName().contains(TOPOLOGY2D_JSON_NAME)).collect(Collectors.toList());
            topo2dJson.stream().forEach(entry -> {
                try {
                    String topo2dStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry))).readLine();
                    //替换topo2dStr中的文件地址
                    for (String oldLink : linkMap.keySet()) {
                        String newLink = linkMap.get(oldLink);
                        topo2dStr = topo2dStr.replaceAll(oldLink, newLink);
                    }
                    TopologyData data = JsonUtil.toJavaObj(topo2dStr, TopologyData.class);
                    //设置大屏信息
                    String id = idGenerator.nextId().toString();
                    data.setId(id);
                    data.setCollection("topo2d");
                    data.setCreateTime(new Date());
                    //String categoryValue = data.getCategoryValue();
                    //data.setCategoryValue("");
                    //data.setCategoryKey("");
                    topoCategoryMaps.forEach((oldCategoryValue, category) -> {
                        if (StringUtil.equals(oldCategoryValue, data.getCategoryValue())) {
                            data.setCategoryValue(category.getCategoryValue());
                            data.setCategoryKey(category.getCategoryKey());
                        }
                    });
                    JSONObject jsonObject = JsonUtil.parseObject(topo2dStr);
                    jsonObject.put("id", id);
                    jsonObject.put("categoryValue", data.getCategoryValue());
                    jsonObject.put("categoryKey", data.getCategoryKey());
                    data.setData(jsonObject.toJSONString());
                    data.setProjectId(projectId);
                    SysUserDTO user = AuthUtils.getUser();
                    data.setCreateBy(user.getUsername());
                    data.setUsername(user.getUsername());
                    //保存大屏信息
                    this.saveOrUpdate(data);

                    if (ObjectUtil.isNotEmpty(data)){
                        final String visualId = data.getId();
                        System.out.println("id=========" + data.getId());
                        newFiles.forEach(newFile->{
                            //将新上传的文件信息记录,插入到数据库
                            newFile.setVisualId(String.valueOf(visualId));
                            newFile.setSource(FILE_SOURCE_TOPOLOGY_2D);
                            int i = ossFileMapper.insert(newFile);
                            if (i > 0) roleSourceService.saveRoleSource(newFile.getId(), SourceType.OSS_FILE);
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } else {
            //文件数据为空直接新增大屏
            //获取 topo2d.json 对象
            List<? extends ZipEntry> topo2dJson = zipFile.stream().filter(
                    entry -> entry.getName().contains(TOPOLOGY2D_JSON_NAME)).collect(Collectors.toList());
            topo2dJson.stream().forEach(entry -> {
                try {
                    String topo2dStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry))).readLine();
                    TopologyData data = JsonUtil.toJavaObj(topo2dStr, TopologyData.class);
                    //设置大屏信息
                    String id = idGenerator.nextId().toString();
                    data.setId(id);
                    data.setCollection("topo2d");
                    data.setCreateTime(new Date());
                    topoCategoryMaps.forEach((oldCategoryValue, category) -> {
                        if (StringUtil.equals(oldCategoryValue, data.getCategoryValue())) {
                            data.setCategoryValue(category.getCategoryValue());
                            data.setCategoryKey(category.getCategoryKey());
                        }
                    });
                    JSONObject jsonObject = JsonUtil.parseObject(topo2dStr);
                    jsonObject.put("id", id);
                    jsonObject.put("categoryValue", data.getCategoryValue());
                    jsonObject.put("categoryKey", data.getCategoryKey());
                    data.setData(jsonObject.toJSONString());
                    data.setProjectId(projectId);
                    SysUserDTO user = AuthUtils.getUser();
                    data.setCreateBy(user.getUsername());
                    data.setUsername(user.getUsername());
                    //保存大屏信息
                    this.save(data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //关闭
        //zipFile.close();
        //删除缓存文件
        //delFile(convFile.getPath());
    }

    @Override
    public void engineeringGlobalSynchronization(List<EngineeringGlobalSynchronizationFrom> list) throws Exception {
        list.stream().forEach(from->{
            TopologyData topologyData = baseMapper.selectById(from.getId());
            JSONObject data = JsonUtil.parseObject(topologyData.getData());

            JSONArray pens = data.getJSONArray("pens");
            JSONObject pen = null;
            String text = "";
            //System.out.println("data前:"+data);
            for (int i=0; i<pens.size(); i++) {
                pen = pens.getJSONObject(i);
                text = pen.get("text").toString();
                if (text.contains(from.getText())) {
                    pen.put("text", text.replaceAll(from.getText(), from.getNewtext()));
                }
            }
            data.put("pens", pens);
            //System.out.println("data后:"+data);
            topologyData.setData(data.toJSONString());
            baseMapper.updateById(topologyData);
        });
    }

    // 临时保存图片文件的方法
    private void saveImageFile(InputStream inputStream, String fileName) throws IOException {
        // 定义保存图片的路径
        String imagePath = new File("").getAbsolutePath() + StringPool.SLASH + "static" + StringPool.SLASH + fileName;
        File imageFile = new File(imagePath);
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }


    //owner、editor赋值
    private String buildUserJsonStrForSreen(TopologyData topologyData) throws Exception {
        JSONObject jsonObject = JsonUtil.parseObject(topologyData.getData());
        JSONObject ownerJson = jsonObject.getJSONObject("owner");
        String onwerid = ownerJson.getString("id");
        String onwername = ownerJson.getString("name");
        JSONObject editorJson = jsonObject.getJSONObject("editor");
        String editorid = editorJson.getString("id");
        String editorname = editorJson.getString("name");
        ownerJson.put("id", editorid);
        ownerJson.put("name", editorname);
        jsonObject.put("owner", ownerJson);
        editorJson.put("id", onwerid);
        editorJson.put("name", onwername);
        jsonObject.put("editor", editorJson);
        return jsonObject.toJSONString();
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

    @Override
    public TopoloyDateJumpVo jumpToTopo2d(String id) {
        //存在从本地map拿数据，不存在从数据库中拿数据
        TopoloyDateJumpVo resStoredData = storedDataMap.get(id);
        if (org.apache.commons.lang3.ObjectUtils.allNotNull(resStoredData)) {
            return resStoredData;
        } else {
            TopologyData data = this.getById(id);
            if (data ==null){
                return null;
            }
            TopoloyDateJumpVo storedData = new TopoloyDateJumpVo();
            storedData.setId(id);
            storedData.setHeadtitle(data.getName());
            if (TopologyConstants.TOPOLOGY_COMPONENTS_TYPE.equals(data.getCollection())) {
                storedData.setComponent(true);
            } else {
                storedData.setComponent(false);
            }
            storedData.setUsername(data.getUsername());
            storedData.setVisualizationEdit(true);
            storedData.setR(System.currentTimeMillis());
            return storedData;
        }
    }

    @Override
    public void jumpFromVisual(TopoloyDateJumpVo topoloyDateJumpVo) {
        // 更新或插入 map
        storedDataMap.put(topoloyDateJumpVo.getId(), topoloyDateJumpVo);
        System.out.println("-----------" + storedDataMap);
    }

    @Override
    public Map<Object, String> queryIdByExternalId(String externalId) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_EXTERNALID_PROPERTY, externalId);

        TopologyData topologyData = baseMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(topologyData)) {
            return null;
        }
        Map<Object, String> ret = new HashMap<>();
        ret.put("id", topologyData.getId());
        return ret;
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

    //根据工程id导出组态大屏
    @Override
    public String exportTopologyByProjectId(String projectId, String downFilePath) throws Exception {
        String ret = null;
        Project project = projectMapper.selectById(projectId);
        QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
        SysUserDTO user = AuthUtils.getUser();
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, user.getUsername());
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_PROJECTID_PROPERTY, projectId);
        List<TopologyData> list = baseMapper.selectList(queryWrapper);

        //String downFilePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + project.getName(); //本地缓存
        for (TopologyData topologyData : list) {
            LambdaQueryWrapper<OssFile> ossQueryWrapper = new LambdaQueryWrapper<>();
            ossQueryWrapper.eq(OssFile::getVisualId, topologyData.getId());
            ossQueryWrapper.eq(OssFile::getSource, "TOPOLOGY_2D");
            //大屏文件数据
            List<OssFile> ossFileList = ossFileMapper.selectList(ossQueryWrapper);
            //封面图
            OssFile backgroundFile = ossFileMapper.queryBackGroundWithVisualId(Long.valueOf(topologyData.getId()));
            //保存大屏配置信息和大屏文件
            String fileName = "组态" + StringPool.SLASH + topologyData.getName();
            String fileSavePath = downFilePath + StringPool.SLASH + fileName;
            //大屏配置信息
            JsonUtil.writeJsonToFile(topologyData.getData(),fileSavePath + StringPool.SLASH + TOPOLOGY2D_JSON_NAME);
            //保存数据文件信息
            JsonUtil.writeJsonToFile(JSON.toJSONString(ossFileList),fileSavePath + StringPool.SLASH + TOPOLOGY2D_FILE_JSON_NAME);
            //保存封面图信息
            JsonUtil.writeJsonToFile(JSON.toJSONString(backgroundFile),fileSavePath + StringPool.SLASH + TOPOLOGY2D_BACKGROUND_JSON_NAME);

            //下载图片文件到本地
            long currentTimeMillis = System.currentTimeMillis();
            CountDownLatch latch = new CountDownLatch(ossFileList.size());
            ossFileList.stream().forEach(ossFile -> {
                commonThreadPool.addTask(()->{
                    try {
                        String saveDir = fileSavePath + StringPool.SLASH + ossFile.getSystemType() + StringPool.SLASH + ossFile.getType();
                        downloadFileFromNetWithOriginalName(ossFile.getLink(),saveDir,5000,10000);
                    } catch (IOException e) {
                        log.error("下载文件失败-->"+ossFile.getLink());
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            });
            latch.await();
            log.info("-------->下载图片地址完成,耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");
        }
        return ret;
    }

    @Override
    public String exportTopologyByProjectIdAndCategory(String categoryValue,  Project project) throws Exception {
        String ret = "";
        //未分类
        /*if ("topo2dnocategorykey9999999999999".equals(categoryValue)) {
            ret = exportNoDir(categoryValue);
            return ret;
        }*/

        //递归下载当前菜单下大屏数据
        export(null, categoryValue, project.getId(), project.getName() );

        return ret;
    }


}
