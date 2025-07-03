package com.stdc.visual.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.mp.utils.Condition;
import com.stdc.core.mp.utils.Query;
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
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.mapper.TopologyDataMapper;
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
import com.stdc.visual.entity.dto.VisualVersionDTO;
import com.stdc.visual.entity.dto.VisualVersionVO;
import com.stdc.visual.entity.po.*;
import com.stdc.visual.entity.request.VisualRequest;
import com.stdc.visual.mapper.OssFileMapper;
import com.stdc.visual.mapper.VisualMapper;
import com.stdc.visual.service.IVisualCategoryService;
import com.stdc.visual.service.IVisualConfigService;
import com.stdc.visual.service.IVisualService;
import com.stdc.visual.util.OssFileTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.stdc.core.oss.utils.FileUtil.*;
import static com.stdc.visual.entity.po.OssFile.FILE_SOURCE_VISUAL;
import static com.stdc.visual.service.IVisualCategoryService.VISUAL_TEMPLATE_VALUE;

/**
 * 可视化表 服务实现类
 *
 * @author wangjie
 */
@Slf4j
@Service
public class VisualServiceImpl extends BaseServiceImpl<VisualMapper, Visual> implements IVisualService {

    /**
     * 完整版导出,导出文件缓存地址
     */
    private static final String EXPORT_FILE_CACHE_PATH = "./static/export";

    /**
     * 轻量版版导出,导出文件缓存地址
     */
    private static final String EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH = "./static/unittec-light-weight";

    /**
     * psd上传功能接口
     */
    private static final String PSD_SAVE_HTTP_URL = "/stdc-visual/oss/psd/file/custom/component";

    /**
     * 大屏配置信息
     */
    private static final String VISUAL_JSON_NAME = "visual.json";

    /**
     * 大屏数据文件信息
     */
    private static final String FILE_JSON_NAME = "file.json";

    /**
     * 封面图
     */
    private static final String BACKGROUND_FILE_JSON_NAME = "background_file.json";

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private IVisualConfigService configService;

    @Autowired
    private RoleSourceService roleSourceService;

    @Autowired
    private IVisualCategoryService visualCategoryService;

    @Autowired
    private OssFileMapper ossFileMapper;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private VisualReleaseMapper releaseMapper;

    @Autowired
    private TopologyDataMapper topologyDataMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private CommonThreadPool commonThreadPool;

    @Autowired
    private OssFileTask ossFileTask;

    @Value("${python.exe-path}")
    private String pythonExePath;

    @Value("${python.python-file-path}")
    private String pythonFilePath;

    @Value("${python.cache-psd-path}")
    private String cachePsdPath;

    @Value("${python.api}")
    private String api;

    public String getBackGroundUrl(String backgroundId) {
        if (!StringUtil.hasText(backgroundId)){
            return null;
        }
        OssFile ossFile = ossFileMapper.selectById(backgroundId);
        if (ObjectUtil.isNotEmpty(ossFile)){
            return ossFile.getLink();
        }
        return null;
    }

    @Override
    public VisualDTO detail(Long id) {
        Visual visual = this.getById(id);
        Optional<VisualConfig> first = configService.list(Wrappers.<VisualConfig>query().lambda().eq(VisualConfig::getVisualId, id).orderByDesc(VisualConfig::getCreateTime)).stream().findFirst();
        if (!first.isPresent()){
            return null;
        }
        VisualConfig visualConfig = first.get();
        if (StringUtil.hasText(visual.getBackgroundId())){
            OssFile ossFile = ossFileMapper.selectById(visual.getBackgroundId());
            if (ObjectUtil.isNotEmpty(ossFile)){
                visual.setBackgroundUrl(ossFile.getLink());
            }
        }
        VisualDTO dto = new VisualDTO();
        dto.setVisual(visual);
        dto.setConfig(visualConfig);
        return dto;
    }

    @Override
    public VisualDTO export(Long id) {
        Visual visual = this.getById(id);
        List<VisualConfig> configs = configService.list(Wrappers.<VisualConfig>query().lambda().eq(VisualConfig::getVisualId, id).orderByDesc(VisualConfig::getCreateTime));
        VisualConfig config  = configs.get(0);
        if (StringUtil.hasText(visual.getBackgroundId())){
            OssFile ossFile = ossFileMapper.selectById(visual.getBackgroundId());
            if (ObjectUtil.isNotEmpty(ossFile)){
                visual.setBackgroundUrl(ossFile.getLink());
            }
        }

        VisualDTO dto = new VisualDTO();
        //visual.setId(null);
        dto.setVisual(visual);
        config.setId(null);
        config.setVisualId(null);
        dto.setConfig(config);
        return dto;
    }

    @Override
    public VisualDTO exportAndRelease(Long id) {
        //获取大屏信息
        VisualRelease release = releaseMapper.selectById(id);
        //没有地址
        if (ObjectUtil.isEmpty(release)){
            BaseException.throwException(ResultCode.get("this_release_path_is_not_release"));
        }
        //大屏未发布
        if (release.getIsRelease() == 0){
            BaseException.throwException(ResultCode.get("this_release_path_is_not_release"));
        }
        Visual visual = this.getById(id);
        visual.setRelease(release);

        VisualConfig visualConfig = configService.getById(release.getConfigId());
        visualConfig.setDetail(release.getDetail());
        visualConfig.setComponent(release.getComponent());

        VisualDTO visualDTO = new VisualDTO();
        visualDTO.setVisual(visual);
        visualDTO.setConfig(visualConfig);
        return visualDTO;
    }


    @Override
    public String exportZip(Long id) {
        String ret = null;
        VisualDTO dto = export(id);
        //缓存地址
        String cachePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + StringUtil.randomUUID();
        //保存大屏配置信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(dto),cachePath + StringPool.SLASH + VISUAL_JSON_NAME);
        //将本地目录打成zip文件并且写入响应体
        //压缩包文件名称
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + dto.getVisual().getTitle() + ".zip";
        String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;
        FileInputStream inputStream =null;
        try {
            //将文件夹压缩为zip包并且导入到本地
            createFile(zipSavePath);
            ZipFileUtils.writeFolderToZip(cachePath,zipSavePath);
            File file = new File(zipSavePath);
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", zipName,inputStream);
            ret = save.getLink();
            //ZipFileUtils.writeZipToResp(HttpServletUtil.getCurrentResp(), cachePath,zipName);
        } catch (Exception e) {
            log.error("压缩本地文件失败...");
            e.printStackTrace();
        }finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
    public String fullExport(Long id) throws Exception{
        //导出大屏前同步文件数据
//        ossFileTask.initOssFileByVisualId(id);
        String ret = null;
        //大屏配置信息
        VisualDTO visualDTO = export(id);
        //大屏文件数据【不包括封面图】
        List<OssFile> ossFileList = queryFileWithVisualId(id);
        //封面图
        OssFile backgroundFile = ossFileMapper.queryBackGroundWithVisualId(id);
        if (ObjectUtil.isNotEmpty(backgroundFile)){
            ossFileList.add(backgroundFile);
        }
        //保存大屏配置信息和大屏文件
        //缓存地址
        String cachePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + StringUtil.randomUUID();
        //保存大屏配置信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(visualDTO),cachePath + StringPool.SLASH + VISUAL_JSON_NAME);
        //保存数据文件信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(ossFileList),cachePath + StringPool.SLASH + FILE_JSON_NAME);
        //保存封面图信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(backgroundFile),cachePath + StringPool.SLASH + BACKGROUND_FILE_JSON_NAME);
        //下载文件到本地
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
                }finally {
                    latch.countDown();
                }
            });
        });
        latch.await();
        log.info("-------->下载图片地址完成,"+ossFileList.size()+"个文件耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");
        //将本地目录打成zip文件并且写入响应体
        //压缩包文件名称
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + com.stdc.core.oss.utils.FileUtil.removeSpaces(visualDTO.getVisual().getTitle()) + ".zip";
        String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;
        FileInputStream inputStream =null;
        try {
            //将文件夹压缩为zip包并且导入到本地
            createFile(zipSavePath);
            ZipFileUtils.writeFolderToZip(cachePath,zipSavePath);
            File file = new File(zipSavePath);
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", zipName,inputStream);
            ret = save.getLink();
//            ZipFileUtils.writeZipToResp(HttpServletUtil.getCurrentResp(), cachePath,zipName);
        } catch (Exception e) {
            log.error("压缩本地文件失败...");
            e.printStackTrace();
        }finally {
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
    public synchronized String lightWeightExport(List<Long> ids){
        String projectPath = "visual";
        String ret = null;
        Map<String,VisualDTO> lightWeightMap = new LinkedHashMap();
        for (Long id : ids) {
            //大屏配置信息
            VisualDTO visualDTO = exportAndRelease(id);
            lightWeightMap.put(visualDTO.getVisual().getRelease().getPath(),visualDTO);
        }


        //缓存地址
        //保存大屏配置信息
        String visualFilePath = EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH + StringPool.SLASH + projectPath + StringPool.SLASH  + "resource/data/unittec-light-weight.json";
        delFile(visualFilePath);
        JsonUtil.writeJsonToFile(JSON.toJSONString(lightWeightMap),visualFilePath);

        //保存大屏发布地址配置信息

        StringBuilder releasePathInfo = new StringBuilder();
        releasePathInfo.append("===========================================\n");
        for (String releasePath : lightWeightMap.keySet()) {
            VisualDTO visualDTO = lightWeightMap.get(releasePath);
            Visual visual = visualDTO.getVisual();
            releasePathInfo.append("可视化平台轻量化部署 【" + visual.getTitle() + "】 大屏发布地址部署为: http://your_ip:your_port/share/" + releasePath + "\n");
            releasePathInfo.append("--------------------------------\n");
        }
        releasePathInfo.append("===========================================\n");
        String releasePathInfoFilePath = EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH + StringPool.SLASH + projectPath + StringPool.SLASH  + "resource/data/release_path_info.text";
        delFile(releasePathInfoFilePath);
        JsonUtil.writeJsonToFile(releasePathInfo.toString(),releasePathInfoFilePath);

        //将本地目录打成zip文件并且写入响应体
        //压缩包文件名称
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + "unittec-light-weight-visual.zip";
        String zipSavePath = EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH  + StringPool.SLASH + zipName;
        FileInputStream inputStream =null;
        try {
            //将文件夹压缩为zip包并且导入到本地
            createFile(zipSavePath);
            ZipFileUtils.writeFolderToZip(EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH + StringPool.SLASH + projectPath,zipSavePath);
            File file = new File(zipSavePath);
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", zipName,inputStream);
            ret = save.getLink();
        } catch (Exception e) {
            log.error("压缩本地文件失败...");
            e.printStackTrace();
        }finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //删除本地缓存文件
            delFile(zipSavePath);
            delFile(releasePathInfoFilePath);
            delFile(visualFilePath);
        }
        return ret;
    }

    @Override
    public synchronized String  lightWeightExportForTopology(List<Long> ids) {
        JSONObject dataJsonObj = new JSONObject();
        for (Long id : ids) {
            TopologyData topologyData = topologyDataMapper.selectById(id);
            if (topologyData != null && (topologyData.getShared() == null || !topologyData.getShared())) {
                BaseException.throwException(ResultCode.get("this_release_path_is_not_release"));
            }
            dataJsonObj.put(topologyData.getPath(), JsonUtil.parseObject(topologyData.getData()));
        }
        String ret = null;
        String projectPath = "topology";
        //缓存地址
        //保存大屏配置信息
        String visualFilePath = EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH + StringPool.SLASH + projectPath + StringPool.SLASH  + "/resource/topo/webs/2d/data/unittec-light-weight.json";
        delFile(visualFilePath);
        JsonUtil.writeJsonToFile(JSON.toJSONString(dataJsonObj), visualFilePath);

        //保存大屏发布地址配置信息
        //JSONObject dataJsonObj = JSONObject.parseObject(data);
        StringBuilder releasePathInfo = new StringBuilder();
        releasePathInfo.append("===========================================\n");
        for (String releasePath : dataJsonObj.keySet()) {
            JSONObject itemObj = (JSONObject)dataJsonObj.get(releasePath);
            releasePathInfo.append("组态平台轻量化部署 【" + itemObj.getString("name") + "】 大屏发布地址为: http://your_ip:your_port/2d/release?id=" + releasePath + "\n");
            releasePathInfo.append("--------------------------------\n");
        }
        releasePathInfo.append("===========================================\n");
        String releasePathInfoFilePath = EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH + StringPool.SLASH + projectPath + StringPool.SLASH  + "/resource/topo/webs/2d/data/release_path_info.text";
        delFile(releasePathInfoFilePath);
        JsonUtil.writeJsonToFile(releasePathInfo.toString(),releasePathInfoFilePath);


        //将本地目录打成zip文件并且写入响应体
        //压缩包文件名称
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + "unittec-light-weight-topology.zip";
        String zipSavePath = EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH  + StringPool.SLASH + zipName;
        FileInputStream inputStream =null;
        try {
            //将文件夹压缩为zip包并且导入到本地
            createFile(zipSavePath);
            ZipFileUtils.writeFolderToZip(EXPORT_LIGHT_WEIGHT_FILE_CACHE_PATH + StringPool.SLASH + projectPath,zipSavePath);
            File file = new File(zipSavePath);
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", zipName,inputStream);
            ret = save.getLink();
        } catch (Exception e) {
            log.error("压缩本地文件失败...");
            e.printStackTrace();
        }finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //删除本地缓存文件
            delFile(zipSavePath);
            delFile(releasePathInfoFilePath);
            delFile(visualFilePath);
        }
        return ret;
    }


    @Override
    public String batchExport(List<Long> ids) throws Exception {
        String ret = null;
        //获取大屏压缩包地址
        List<String> linkList = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(ids.size());
        ids.forEach(id->commonThreadPool.addTask(()->{
            try {
                linkList.add(fullExport(id));
            } catch (Exception e) {
                BaseException.throwException(e);
            }finally {
                latch.countDown();
            }
        }));
        latch.await();
        //将多个大屏打包并下载到本地目录
        String saveDir = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + StringUtil.randomUUID();
        linkList.forEach(link-> {
            try {
                downloadFileFromNetWithSuffix(link,saveDir);
            } catch (IOException e) {
                BaseException.throwException(e);
            }
        });
        //将本地目录打成zip文件并且写入响应体
        //压缩包文件名称
        String zipName = "批量导出"+ StringPool.UNDERSCORE + DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + ".zip";
        String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;
        FileInputStream inputStream =null;
        try {
            //将文件夹压缩为zip包并且导入到本地
            createFile(zipSavePath);
            ZipFileUtils.writeFolderToZip(saveDir,zipSavePath);
            File file = new File(zipSavePath);
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", zipName,inputStream);
            ret = save.getLink();
        } catch (Exception e) {
            log.error("压缩本地文件失败...");
            e.printStackTrace();
        }finally {
            if (inputStream!=null) inputStream.close();
            //删除本地缓存文件
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
    public void fullImport(MultipartFile file, String category,String visualName) throws Exception {
        //获取zip文件包对象
        String zipPath= new File("").getAbsolutePath() + StringPool.SLASH + "static" + StringPool.SLASH + StringUtil.randomUUID() + StringPool.UNDERSCORE +file.getOriginalFilename();
        File convFile = new File(zipPath);
        file.transferTo(convFile);
        ZipFile zipFile = new ZipFile(convFile, Charset.forName("GBK"));

        //获取 visual.json 对象
        Optional<? extends ZipEntry> first = zipFile.stream().filter(entry -> entry.getName().contains(VISUAL_JSON_NAME)).findFirst();
        if (!first.isPresent()){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        ZipEntry visualJson = first.get();
        //读取 visual.json 内容
        String visualStr = null;
        if (ObjectUtil.isNotEmpty(visualJson)){
            visualStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(visualJson))).readLine();
        }
        if (!StringUtil.hasText(visualStr)) BaseException.throwException("解析压缩包失败");

        //获取 file.json 对象
        List<? extends ZipEntry> fileJsonCollection = zipFile.stream().filter(entry -> entry.getName().contains(FILE_JSON_NAME) && !entry.getName().contains(BACKGROUND_FILE_JSON_NAME)).collect(Collectors.toList());
        ZipEntry fileJson = null;
        if (!CollectionUtils.isEmpty(fileJsonCollection)) fileJson = fileJsonCollection.stream().findFirst().get();
        //读取 file.json 内容
        String fileStr = null;
        if (ObjectUtil.isNotEmpty(fileJson)){
            fileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(fileJson))).readLine();
        }

        //获取 background_file.json 对象
        List<? extends ZipEntry> backgroundFileJsonCollection = zipFile.stream().filter(entry -> entry.getName().contains(BACKGROUND_FILE_JSON_NAME)).collect(Collectors.toList());
        ZipEntry backgroundFile = null;
        if (!CollectionUtils.isEmpty(backgroundFileJsonCollection)) backgroundFile = backgroundFileJsonCollection.stream().findFirst().get();
        //读取 background_file.json 内容
        String backgroundFileStr = null;
        if (ObjectUtil.isNotEmpty(backgroundFile)){
            backgroundFileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(backgroundFile))).readLine();
        }
        //背景图信息
        OssFile ossBackGroundFile = JsonUtil.toJavaObj(backgroundFileStr, OssFile.class);

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
                        //存储新文件信息
                        newFiles.add(newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //替换visualStr中的文件地址
            for (String oldLink : linkMap.keySet()) {
                String newLink = linkMap.get(oldLink);
                visualStr = visualStr.replaceAll(oldLink, newLink);
            }
            //设置大屏信息
            VisualDTO dto = JsonUtil.toJavaObj(visualStr, VisualDTO.class);
            //大屏分类
            dto.getVisual().setCategory(category);
            //大屏标题
            dto.getVisual().setTitle(visualName);
            //大屏封面
            dto.getConfig().setBackgroundId(backGroundUrl.get() != null ? backGroundUrl.get() : null);

            //保存大屏信息
            dto = saveVisualRet(dto);
            if (ObjectUtil.isNotEmpty(dto)){
                final Long visualId = dto.getVisual().getId();
                final Long configId = dto.getConfig().getId();
                newFiles.forEach(newFile->{
                    //将新上传的文件信息记录,插入到数据库
                    newFile.setVisualId(String.valueOf(visualId));
                    newFile.setConfigId(String.valueOf(configId));
                    newFile.setSource(FILE_SOURCE_VISUAL);
                    int i = ossFileMapper.insert(newFile);
                    if (i > 0) roleSourceService.saveRoleSource(newFile.getId(), SourceType.OSS_FILE);
                });
            }
        }
        //文件数据为空直接新增大屏
        else {
            //设置大屏分类
            VisualDTO dto = JsonUtil.toJavaObj(visualStr, VisualDTO.class);
            dto.getVisual().setCategory(category);
            dto.getVisual().setTitle(visualName);
            //保存大屏信息
            saveVisualRet(dto);
        }
        //关闭
        zipFile.close();
        //删除缓存文件
        delFile(convFile.getPath());
    }

    @Override
    public void fullImportFromProject(ZipFile zipFile, String projectId, HashMap<String, VisualCategory> visualCategoryMaps) throws Exception {

        //获取 background_file.json 对象
        List<? extends ZipEntry> backgroundFileJsonCollection = zipFile.stream().filter(entry -> entry.getName().contains(BACKGROUND_FILE_JSON_NAME)).collect(Collectors.toList());
        ZipEntry backgroundFile = null;
        if (!CollectionUtils.isEmpty(backgroundFileJsonCollection)) backgroundFile = backgroundFileJsonCollection.stream().findFirst().get();
        //读取 background_file.json 内容
        String backgroundFileStr = null;
        if (ObjectUtil.isNotEmpty(backgroundFile)){
            backgroundFileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(backgroundFile))).readLine();
        }
        //背景图信息
        OssFile ossBackGroundFile = JsonUtil.toJavaObj(backgroundFileStr, OssFile.class);

        List<String> fileStrList = new ArrayList<>();
        //获取 file.json 对象
        List<? extends ZipEntry> fileJsonCollection = zipFile.stream().filter(
                entry -> entry.getName().contains(FILE_JSON_NAME) && !entry.getName().contains(BACKGROUND_FILE_JSON_NAME)).collect(Collectors.toList());
        fileJsonCollection.stream().forEach(entry -> {
            try {
                String fileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry))).readLine();
                fileStrList.add(fileStr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        //获取 visual.json 对象
        List<? extends ZipEntry> visualJson = zipFile.stream().filter(
                entry -> entry.getName().contains(VISUAL_JSON_NAME)).collect(Collectors.toList());
        visualJson.stream().forEach(entry -> {
            try {
                String visualStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry))).readLine();
                //VisualDTO dto = JsonUtil.toJavaObj(visualStr, VisualDTO.class);

                for (String fileStr : fileStrList) {
                    //获取文件对象jsonArray
                    JSONArray fileArray = JSONArray.parseArray(fileStr);
                    //文件不为空
                    if (!CollectionUtils.isEmpty(fileArray)){
                        //通过文件信息数组,将zip包中的文件传到新的服务器上,并且获得新服务器文件的地址
                        Map<String,String> linkMap = new HashMap<>(fileArray.size());
                        //大屏封面映射地址
                        AtomicReference<String> backGroundUrl = new AtomicReference();
                        List<OssFile> newFiles = new ArrayList<>(fileArray.size());

                        for (Object fileJsonObject : fileArray) {
                            VisualDTO dto = JsonUtil.toJavaObj(visualStr, VisualDTO.class);
                            //file.json和visual.json不匹配 跳过
                            String fileVisualId = JsonUtil.parseObject(fileJsonObject.toString()).getString("visualId");
                            String dtoVisualId = dto.getVisual().getId().toString();
                            if (fileVisualId.equals(dtoVisualId)) {
                                System.out.println("======================true");
                                //获取文件信息
                                OssFile ossFile = JsonUtil.toJavaObj(JSON.toJSONString(fileJsonObject), OssFile.class);
                                //比对文件名称,获取对应的文件
                                List<? extends ZipEntry> collect = zipFile.stream().filter(entry1 ->{
                                    if (entry1.getName().equals(StringPool.SLASH)){
                                        return false;
                                    }
                                    String[] zipNameS = entry1.getName().split(StringPool.SLASH);
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
                                        //存储新文件信息
                                        newFiles.add(newFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //替换visualStr中的文件地址
                                for (String oldLink : linkMap.keySet()) {
                                    String newLink = linkMap.get(oldLink);
                                    visualStr = visualStr.replaceAll(oldLink, newLink);
                                }
                                //设置大屏信息
                                //VisualDTO dto = JsonUtil.toJavaObj(visualStr, VisualDTO.class);
                                VisualDTO finalDto = dto;
                                visualCategoryMaps.forEach((oldCategoryValue, category) -> {
                                    if (StringUtil.equals(oldCategoryValue, finalDto.getVisual().getCategory())) {
                                        finalDto.getVisual().setCategory(category.getCategoryValue());
                                    }
                                });
                                //大屏封面
                                dto.getConfig().setBackgroundId(backGroundUrl.get() != null ? backGroundUrl.get() : null);

                                //保存大屏信息
                                dto.getVisual().setProjectId(projectId);
                                dto = saveVisualRet(dto);
                                if (ObjectUtil.isNotEmpty(dto)){
                                    final Long visualId = dto.getVisual().getId();
                                    final Long configId = dto.getConfig().getId();
                                    newFiles.forEach(newFile->{
                                        //将新上传的文件信息记录,插入到数据库
                                        newFile.setVisualId(String.valueOf(visualId));
                                        newFile.setConfigId(String.valueOf(configId));
                                        newFile.setSource(FILE_SOURCE_VISUAL);
                                        int i = ossFileMapper.insert(newFile);
                                        if (i > 0) roleSourceService.saveRoleSource(newFile.getId(), SourceType.OSS_FILE);
                                    });
                                }

                            }
                        }

                    }
                    //文件数据为空直接新增大屏
                    /*else {
                        //设置大屏分类
                        VisualDTO dto = JsonUtil.toJavaObj(visualStr, VisualDTO.class);

                        visualCategoryMaps.forEach((oldCategoryValue, category) -> {
                            if (StringUtil.equals(oldCategoryValue, dto.getVisual().getCategory())) {
                                dto.getVisual().setCategory(category.getCategoryValue());
                            }
                        });
                        //保存大屏信息
                        dto.getVisual().setProjectId(projectId);
                        saveVisualRet(dto);
                    }*/
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public VisualDTO detailVersion(Long id, String version) {
        Visual visual = this.getById(id);
        VisualConfig visualConfig = configService.getOne(Wrappers.<VisualConfig>query().lambda().eq(VisualConfig::getVisualId, id).eq(VisualConfig::getVersion,version).orderByDesc(VisualConfig::getUpdateTime));
        if (StringUtil.hasText(visual.getBackgroundId())){
            OssFile ossFile = ossFileMapper.selectById(visual.getBackgroundId());
            if (ObjectUtil.isNotEmpty(ossFile)){
                visual.setBackgroundUrl(ossFile.getLink());
            }
        }
        VisualDTO dto = new VisualDTO();
        dto.setVisual(visual);
        dto.setConfig(visualConfig);
        return dto;
    }

    @Override
    public VisualDTO detailVersion(Long id, Long configId) {
        Visual visual = this.getById(id);
        /*VisualConfig visualConfig = configService.getOne(Wrappers.<VisualConfig>query().lambda().eq(VisualConfig::getId,configId).eq(VisualConfig::getVisualId, id).orderByDesc(VisualConfig::getUpdateTime));
        if (ObjectUtil.isEmpty(configId) && ObjectUtil.isEmpty(visualConfig)){
            BaseException.throwException("大屏不存在");
        }*/
        if (StringUtil.hasText(visual.getBackgroundId())){
            OssFile ossFile = ossFileMapper.selectById(visual.getBackgroundId());
            if (ObjectUtil.isNotEmpty(ossFile)){
                visual.setBackgroundUrl(ossFile.getLink());
            }
        }
        VisualDTO dto = new VisualDTO();
        dto.setVisual(visual);
        //dto.setConfig(visualConfig);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveVisual(List<VisualDTO> dtoS) {
        Visual visual = null;
        boolean tempV = false;
        if (dtoS.size() > 0 ){
            visual = dtoS.get(0).getVisual();
            visual.setStatus(1);
            visual.setIsDeleted(0);
            visual.setBackgroundType(BackGroundType.CUSTOM.getValue());
            visual.setCreateTime(DateUtil.formatDateTime(new Date()));
            tempV = this.save(visual);
        }else {
            return false;
        }
        Collections.reverse(dtoS);
        for (VisualDTO dto : dtoS) {
            VisualConfig visualConfig = dto.getConfig();
            if (StringUtil.isBlank(visualConfig.getVersion())){
                BaseException.throwException(ResultCode.get("this_version_is_not_exit"));
            }
            visualConfig.setVisualId(visual.getId());
            long timeMillis = System.currentTimeMillis();
            visualConfig.setCreateTime(timeMillis);
            visualConfig.setUpdateTime(timeMillis);
            boolean tempVc = configService.save(visualConfig);
            boolean tempRs = roleSourceService.saveRoleSource(visual.getId(), SourceType.VISUAL);
            if (!(tempV && tempVc && tempRs)){
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long copyVisual(List<VisualDTO> dtoS) {
        Visual visual = null;
        boolean tempV = false;
        if (dtoS.size() > 0 ){
            visual = dtoS.get(0).getVisual();
            visual.setStatus(1);
            visual.setIsDeleted(0);
            visual.setCreateTime(DateUtil.formatDateTime(new Date()));
            visual.setUpdateTime(DateUtil.formatDateTime(new Date()));
            visual.setBackgroundType(BackGroundType.DEFAULT.getValue());
            tempV = this.save(visual);
        }else {
            return 0L;
        }
        Collections.reverse(dtoS);
        for (VisualDTO dto : dtoS) {
            VisualConfig visualConfig = dto.getConfig();
            if (StringUtil.isBlank(visualConfig.getVersion())){
                BaseException.throwException(ResultCode.get("this_version_is_not_exit"));
            }
            visualConfig.setVisualId(visual.getId());
            long timeMillis = System.currentTimeMillis();
            visualConfig.setCreateTime(timeMillis);
            visualConfig.setUpdateTime(timeMillis);
            boolean tempVc = configService.save(visualConfig);
            boolean tempRs = roleSourceService.saveRoleSource(visual.getId(), SourceType.VISUAL);
            if (!(tempV && tempVc && tempRs)){
                return 0L;
            }
        }
        return visual.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveVisual(VisualDTO dto) {
        Visual visual = dto.getVisual();

        visual.setStatus(1);
        visual.setIsDeleted(0);
        //当保存为大屏模板时，大屏封面为页面截图
        if (StringUtil.equals(visual.getCategory(),String.valueOf(VISUAL_TEMPLATE_VALUE))){
            visual.setBackgroundType(BackGroundType.THUMBNAIL.getValue());
        }else {
            visual.setBackgroundType(BackGroundType.DEFAULT.getValue());
        }
        String dateFormat = DateUtil.formatDateTime(new Date());
        visual.setCreateTime(dateFormat);
        visual.setUpdateTime(dateFormat);
        boolean tempV = this.save(visual);
        VisualConfig visualConfig = dto.getConfig();
        if (StringUtil.isBlank(visualConfig.getVersion())){
            visualConfig.setVersion("v1");
        }
        visualConfig.setVisualId(visual.getId());
        long timeMillis = System.currentTimeMillis();
        visualConfig.setCreateTime(timeMillis);
        visualConfig.setUpdateTime(timeMillis);
        boolean tempVc = configService.save(visualConfig);
        boolean tempRs = roleSourceService.saveRoleSource(visual.getId(), SourceType.VISUAL);
        return tempV && tempVc && tempRs;
    }

    @Override
    public VisualDTO saveVisualRet(VisualDTO dto) {
        Visual visual = dto.getVisual();
        visual.setId(null);
        visual.setStatus(1);
        visual.setIsDeleted(0);
        //当保存为大屏模板时，大屏封面为页面截图
        if (StringUtil.equals(visual.getCategory(),String.valueOf(VISUAL_TEMPLATE_VALUE))){
            visual.setBackgroundType(BackGroundType.THUMBNAIL.getValue());
        }else {
            visual.setBackgroundType(BackGroundType.DEFAULT.getValue());
        }
        visual.setCreateTime(DateUtil.formatDateTime(new Date()));
        visual.setUpdateTime(DateUtil.formatDateTime(new Date()));
        this.save(visual);
        VisualConfig visualConfig = dto.getConfig();
        if (StringUtil.isBlank(visualConfig.getVersion())){
            visualConfig.setVersion("v1");
        }
        visualConfig.setVisualId(visual.getId());
        long timeMillis = System.currentTimeMillis();
        visualConfig.setCreateTime(timeMillis);
        visualConfig.setUpdateTime(timeMillis);
        configService.save(visualConfig);
        roleSourceService.saveRoleSource(visual.getId(), SourceType.VISUAL);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveVisualNewVersion(VisualDTO dto) {
        //保存配置
        VisualConfig visualConfig = dto.getConfig();
        if(!StringUtil.hasText(visualConfig.getVersion())){
            return false;
        }
        String backGroundId = visualConfig.getBackgroundId();
        if (StringUtil.hasText(backGroundId)){
            this.baseMapper.updateBackgroundUrl(visualConfig.getVisualId(),backGroundId);
        }
        VisualConfig config = configService.getOne(new LambdaQueryWrapper<VisualConfig>()
                .eq(VisualConfig::getVisualId, visualConfig.getVisualId())
                .eq(VisualConfig::getVersion, visualConfig.getVersion()));
        if (ObjectUtil.isNotEmpty(config)){
            BaseException.throwException(ResultCode.get("this_version_is_exit"));
        }
        long timeMillis = System.currentTimeMillis();
        visualConfig.setCreateTime(timeMillis);
        visualConfig.setUpdateTime(timeMillis);
        boolean isSaveConfig = configService.save(visualConfig);
        //初始化图片
        commonThreadPool.addTask(()->{
            ossFileTask.initOssFileByConfigId(dto.getConfig().getId());
        });
        return isSaveConfig;
    }

    @Override
    public List<VisualVersionVO> queryVisualVersion(Long visualId) {
        return configService.queryVisualVersion(visualId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVisual(VisualDTO dto) {
        Visual visual = dto.getVisual();
        VisualConfig visualConfig = dto.getConfig();
        boolean updateVisual = false;
        if (visual != null && visual.getId() != null) {
            //大屏封面为缩略图 更新为最新的缩略图的id
            if (StringUtil.equals(BackGroundType.THUMBNAIL.getValue(),visual.getBackgroundType())){
                //visualConfig为空时 找到更新时间最新的config
                if (visualConfig.getId() == null) {
                    VisualConfig lastConfig = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, visual.getId()).orderByDesc(VisualConfig::getUpdateTime).last(" limit 1"));
                    visual.setBackgroundId(lastConfig.getBackgroundId());
                }else {
                    visual.setBackgroundId(visualConfig.getBackgroundId());
                }
            }
            visual.setUpdateTime(DateUtil.formatDateTime(new Date()));
            updateVisual = this.updateById(visual);
        }
        if (visualConfig != null && visualConfig.getId() != null) {
            boolean updateConfig = false;
            if (!StringUtil.hasText(visualConfig.getVersion())){
                BaseException.throwException(ResultCode.get("this_version_is_not_exit"));
            }
            visualConfig.setUpdateTime(System.currentTimeMillis());
            VisualConfig byId = configService.getById(visualConfig.getId());
            updateConfig = configService.update(visualConfig,new LambdaQueryWrapper<VisualConfig>()
                    .eq(VisualConfig::getId,visualConfig.getId())
                    .eq(VisualConfig::getVersion,visualConfig.getVersion())) ;
            if (updateConfig && StringUtil.hasText(byId.getBackgroundId())){
                //删除旧缩略图文件
                OssFile ossFile = ossFileMapper.selectBackOssFileWithOutTemplate(byId.getBackgroundId());
                if (ObjectUtil.isNotEmpty(ossFile)){
                    //删除minio文件
                    String[] doMain = ossFile.getDoMain().split(StringPool.SLASH, 2);
                    ossTemplate.removeFile(StdcVisualConstant.OSS_PREFIX_BUCKET,doMain[0]+StringPool.SLASH+doMain[1]);
                    ossFileMapper.deleteById(byId.getBackgroundId());
                }
            }
            return updateVisual && updateConfig;
        }
        return updateVisual;
    }

    @Override
    public boolean updateVersion(VisualVersionDTO visualVersionDTO) {
        visualVersionDTO.setUpdateTime(System.currentTimeMillis());
        return configService.updateNewVersion(visualVersionDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeVisual(List<Long> ids) {
        List<Visual> visuals = this.listByIds(ids);
        for (Visual visual : visuals) {
            visual.setStatus(1);
            visual.setIsDeleted(0);
            List<VisualConfig> configs = configService.list(new LambdaQueryWrapper<VisualConfig>()
                    .eq(VisualConfig::getVisualId, visual.getId()));
            List<Long> configIdS = configs.stream().map(VisualConfig::getId).collect(Collectors.toList());
            boolean tempV = this.removeById(visual);
            boolean tempVc = configService.removeByIds(configIdS);
            //删除发布大屏内容
            releaseMapper.deleteById(visual.getId());
//            configs.forEach(config->ossFileMapper.deleteById(config.getBackgroundId()));
            if (!(tempV && tempVc)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeVersion(Long versionId) {
        return configService.removeById(versionId);
    }

    /**
     * 2023年8月14日14:47:00，复制大屏功能修改为文件也复制
     * @param id 主键
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyVisual(Long id) {
/*        Visual visual = this.getById(id);
         List<VisualConfig> configs = configService.list(Wrappers.<VisualConfig>query().lambda().eq(VisualConfig::getVisualId, id).orderByDesc(VisualConfig::getCreateTime));
        if (visual != null && configs.size() > 0) {
            List<VisualDTO> dtoS = new ArrayList<>();
            configs.forEach(c->{
                VisualDTO dto = new VisualDTO();
                visual.setId(null);
                visual.setTitle(visual.getTitle() + StringPool.UNDERSCORE + "copy");
                dto.setVisual(visual);
                c.setId(null);
                c.setVisualId(null);
                dto.setConfig(c);
                dtoS.add(dto);
            });
            long newId = this.copyVisual(dtoS);
            if (newId!= 0L){
                return newId;
            }
        }
        return null;*/
        Long ret = null;
        Visual visual = this.getById(id);
        try {
            //获取到旧大屏导出包
            String link = fullExport(id);
            //下载导出包到本地
            //缓存地址
            String cachePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + StringUtil.randomUUID();
            String saveDir = cachePath + StringPool.SLASH + "visual" + StringPool.SLASH + "copy";
            downloadFileFromNetWithOriginalName(link, saveDir);
            saveDir = saveDir + StringPool.SLASH +getFileNameFromURL(link);
            ret = fullImportForCopy(new File(saveDir),visual.getCategory(),visual.getTitle() + StringPool.UNDERSCORE + "copy");
        } catch (Exception e) {
            e.printStackTrace();
            BaseException.throwException("复制大屏失败");
        }
        return ret;
    }

    /**
     * 复制操作时导入大屏
     * @param convFile 旧大屏包文件
     * @param category 旧大屏分类
     * @param visualName 新大屏名称
     * @throws Exception
     */
    public Long fullImportForCopy(File convFile, String category,String visualName) throws Exception {
        VisualDTO dto = null;
        //获取zip文件包对象
//        String zipPath= new File("").getAbsolutePath() + StringPool.SLASH + "static" + StringPool.SLASH + StringUtil.randomUUID() + StringPool.UNDERSCORE +file.getOriginalFilename();
//        File convFile = new File(zipPath);
//        file.transferTo(convFile);
        ZipFile zipFile = new ZipFile(convFile, Charset.forName("GBK"));

        //获取 visual.json 对象
        Optional<? extends ZipEntry> first = zipFile.stream().filter(entry -> entry.getName().contains(VISUAL_JSON_NAME)).findFirst();
        if (!first.isPresent()){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        ZipEntry visualJson = first.get();
        //读取 visual.json 内容
        String visualStr = null;
        if (ObjectUtil.isNotEmpty(visualJson)){
            visualStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(visualJson))).readLine();
        }
        if (!StringUtil.hasText(visualStr)) BaseException.throwException("解析压缩包失败");

        //获取 file.json 对象
        List<? extends ZipEntry> fileJsonCollection = zipFile.stream().filter(entry -> entry.getName().contains(FILE_JSON_NAME) && !entry.getName().contains(BACKGROUND_FILE_JSON_NAME)).collect(Collectors.toList());
        ZipEntry fileJson = null;
        if (!CollectionUtils.isEmpty(fileJsonCollection)) fileJson = fileJsonCollection.stream().findFirst().get();
        //读取 file.json 内容
        String fileStr = null;
        if (ObjectUtil.isNotEmpty(fileJson)){
            fileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(fileJson))).readLine();
        }

        //获取 background_file.json 对象
        List<? extends ZipEntry> backgroundFileJsonCollection = zipFile.stream().filter(entry -> entry.getName().contains(BACKGROUND_FILE_JSON_NAME)).collect(Collectors.toList());
        ZipEntry backgroundFile = null;
        if (!CollectionUtils.isEmpty(backgroundFileJsonCollection)) backgroundFile = backgroundFileJsonCollection.stream().findFirst().get();
        //读取 background_file.json 内容
        String backgroundFileStr = null;
        if (ObjectUtil.isNotEmpty(backgroundFile)){
            backgroundFileStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(backgroundFile))).readLine();
        }
        //背景图信息
        OssFile ossBackGroundFile = JsonUtil.toJavaObj(backgroundFileStr, OssFile.class);

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
                        if (StringUtil.equals(ossFile.getId(),ossBackGroundFile.getId())){
                            backGroundUrl.set(newFile.getId());
                        }
                        //保存新旧地址的映射关系
                        linkMap.put(ossFile.getLink(),newFile.getLink());
                        //存储新文件信息
                        newFiles.add(newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //替换visualStr中的文件地址
            for (String oldLink : linkMap.keySet()) {
                String newLink = linkMap.get(oldLink);
                visualStr = visualStr.replaceAll(oldLink, newLink);
            }
            //设置大屏信息
            dto = JsonUtil.toJavaObj(visualStr, VisualDTO.class);
            //大屏分类
            dto.getVisual().setCategory(category);
            //大屏标题
            dto.getVisual().setTitle(visualName);
            //大屏封面
            dto.getConfig().setBackgroundId(backGroundUrl.get() != null ? backGroundUrl.get() : null);

            //保存大屏信息
            dto = saveVisualRet(dto);
            if (ObjectUtil.isNotEmpty(dto)){
                final Long visualId = dto.getVisual().getId();
                final Long configId = dto.getConfig().getId();
                newFiles.forEach(newFile->{
                    //将新上传的文件信息记录,插入到数据库
                    newFile.setVisualId(String.valueOf(visualId));
                    newFile.setConfigId(String.valueOf(configId));
                    newFile.setSource(FILE_SOURCE_VISUAL);
                    int i = ossFileMapper.insert(newFile);
                    if (i > 0) roleSourceService.saveRoleSource(newFile.getId(), SourceType.OSS_FILE);
                });
            }
        }
        //文件数据为空直接新增大屏
        else {
            //设置大屏分类
            dto = JsonUtil.toJavaObj(visualStr, VisualDTO.class);
            dto.getVisual().setCategory(category);
            dto.getVisual().setTitle(visualName);
            //保存大屏信息
            dto = saveVisualRet(dto);
        }
        //关闭
        zipFile.close();
        //删除缓存文件
        delFile(convFile.getPath());
        return ObjectUtil.isNotEmpty(dto) ? dto.getVisual().getId() : 0L;
    }

    @Override
    public List<Long> getSameCateGory(Long id) {
        return this.baseMapper.getSameCateGory(id);
    }

    @Transactional
    @Override
    public Boolean psdSave(MultipartFile psdFile,String category) throws Exception{
        // psd文件路径
        String psdCachePath = cachePsdPath + StringUtil.randomUUID() + StringPool.UNDERSCORE + psdFile.getOriginalFilename();
        //文件临时上传目录
        String imgCachePath = cachePsdPath + StringUtil.randomUUID() + StringPool.SLASH;
        FileUtil.mkdir(imgCachePath);

        //psd文件上传接口
        String psdSaveHttpUrl = api + PSD_SAVE_HTTP_URL;
        //token
        String token = AuthUtils.TOKEN_HEAD + AuthUtils.getToken();
        File psdCacheFile = createNewFile(psdCachePath);

        ProcessBuilder pb = null;
        Process process = null;
        StringBuffer visualStr = null;
        //调用python命令
        try {
            FileUtil.writeBytes(psdFile.getBytes(), psdCacheFile);
            String pythonStr = pythonExePath + StringPool.SPACE +
                    pythonFilePath+ StringPool.SPACE +
                    psdCachePath + StringPool.SPACE +
                    imgCachePath + StringPool.SPACE +
                    psdSaveHttpUrl + StringPool.SPACE +
                    token;
            LogUtil.info("调用python命令 --> \n " + pythonStr + "\n");
            pb = new ProcessBuilder(pythonExePath, pythonFilePath, psdCachePath,imgCachePath,psdSaveHttpUrl,token);
            process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            visualStr = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                visualStr.append(line);
            }
            LogUtil.info("python命令返回结果为："+visualStr.toString());
        } catch (IOException e) {
            LogUtil.error("",e);
            BaseException.throwException("导入psd文件失败");
        }finally {
            if (ObjectUtil.isNotEmpty(process)){
                process.waitFor();
            }
        }
        if (ObjectUtil.isEmpty(visualStr) || !StringUtil.hasText(visualStr.toString())) BaseException.throwException("导入psd文件失败");
        String visualJson = visualStr.toString()
                .replaceAll("True","true").replaceAll("False","false");
        VisualDTO dto = JSON.parseObject(visualJson, VisualDTO.class);
        dto.getVisual().setCategory(category);
        String nameWithoutExtension = getNameWithoutExtension(psdFile.getOriginalFilename());
        dto.getVisual().setTitle(nameWithoutExtension);
        boolean save = this.saveVisual(dto);
        //初始化图片页面
        ossFileTask.initOssFileByConfigId(dto.getConfig().getId());
        LogUtil.info("删除psd缓存文件:psdPath --> " + FileUtil.del(psdCacheFile));
        return save;
    }

    /**
     * 新增、更新、导入、psd导入、复制 大屏时
     * @param title
     * @param category
     */
    @Override
    public void checkTitle(String title, String category) {
        LambdaQueryWrapper<Visual> queryWrapper = new LambdaQueryWrapper<Visual>().eq(Visual::getTitle, title).eq(Visual::getCategory, category);
        List<Visual> ret = list(queryWrapper);
        if (!CollectionUtils.isEmpty(ret)){
            BaseException.throwException("该分类下已经存在相同名称的大屏:"+title);
        }
    }

    @Override
    public IPage<Visual> getAllList(VisualRequest request, Query query) {
        String category = request.getCategory();
        request.setCategory(null);
        String title = request.getTitle();
        request.setTitle(null);
        QueryWrapper<Visual> queryWrapper = Condition.getQueryWrapper(request);
        if (!StringUtil.isBlank(title)){
            queryWrapper.like("title", title);
        }
        if (StringUtil.hasText(request.getOrder())){
            queryWrapper.orderByDesc(request.getOrder());
        }
        if (StringUtil.isBlank(category)) {
            IPage<Visual> page = baseMapper.selectPage(Condition.getPage(query), queryWrapper);
            return page;
        }
        //categoryValueList记录当前目录和所有子目录的categoryValue
        List<String> categoryValueList = new ArrayList<>();
        //查询当前目录是否有子目录
        VisualCategory visualCategory = visualCategoryService.getByCategoryValue(category);
        //当前目录如果有子目录 则递归查询所有子目录 并添加子目录的categoryValue到categoryValueList中
        getVisualData(visualCategory, categoryValueList);
        //添加当前目录的categoryValue
        categoryValueList.add(category);
        //System.out.println(categoryValueList);
        //根据categoryValue查询所有大屏数据
        if (categoryValueList != null && categoryValueList.size() > 0) {
            queryWrapper.in("category", categoryValueList);
            IPage<Visual> page = baseMapper.selectPage(Condition.getPage(query), queryWrapper);
            return page;
        }
        return new Page<>();
    }

    @Override
    public String exportVisualByProjectId(String projectId, String downFilePath) throws Exception {
        String ret = null;
        Project project = projectMapper.selectById(projectId);
        QueryWrapper<Visual> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        List<Visual> list = baseMapper.selectList(queryWrapper);
        for (Visual visual : list) {
            //大屏配置信息
            VisualDTO visualDTO = export(visual.getId());
            //大屏文件数据【不包括封面图】
            List<OssFile> ossFileList = queryFileWithVisualId(visual.getId());
            //封面图
            OssFile backgroundFile = ossFileMapper.queryBackGroundWithVisualId(visual.getId());
            if (ObjectUtil.isNotEmpty(backgroundFile)){
                ossFileList.add(backgroundFile);
            }
            //保存大屏配置信息和大屏文件
            String fileName = "可视化" + StringPool.SLASH + visual.getTitle();
            String fileSavePath = downFilePath + StringPool.SLASH + fileName;
            //保存大屏配置信息
            JsonUtil.writeJsonToFile(JSON.toJSONString(visualDTO),fileSavePath + StringPool.SLASH + VISUAL_JSON_NAME);
            //保存数据文件信息
            JsonUtil.writeJsonToFile(JSON.toJSONString(ossFileList),fileSavePath + StringPool.SLASH + FILE_JSON_NAME);
            //保存封面图信息
            JsonUtil.writeJsonToFile(JSON.toJSONString(backgroundFile),fileSavePath + StringPool.SLASH + BACKGROUND_FILE_JSON_NAME);
            //下载文件到本地
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
                    }finally {
                        latch.countDown();
                    }
                });
            });
            latch.await();
            log.info("-------->下载图片地址完成,"+ossFileList.size()+"个文件耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");

        }
        return ret;
    }

    @Override
    public String exportVisualByProjectIdAndCategory(String categoryValue, Project project) throws Exception {
        String ret = "";

        //递归下载当前菜单下大屏数据
        export(null, categoryValue, project.getId(), project.getName() );

        return ret;
    }

    //下载当前菜单下大屏数据
    private void export(String parentCategoryKey, String categoryValue, String projectId, String projectName) throws Exception {
        VisualCategory visualCategory = visualCategoryService.getByCategoryValue(categoryValue);
        QueryWrapper<Visual> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", categoryValue);
        queryWrapper.eq("project_id", projectId);
        List<Visual> list = baseMapper.selectList(queryWrapper);

        String categoryKey = null;
        if (StringUtil.isBlank(parentCategoryKey)) {
            categoryKey = visualCategory.getCategoryKey();
            if (!StringUtil.isBlank(projectName)) {
                categoryKey = projectName + StringPool.SLASH + "可视化" + StringPool.SLASH + categoryKey;
            }
        } else {
            categoryKey = parentCategoryKey + StringPool.SLASH + visualCategory.getCategoryKey();
        }

        for (Visual visual : list) {
            try {
                downloadDirData(categoryKey, visual.getId());
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
    private String downloadDirData(String categoryKey, Long id) throws Exception {
        //大屏配置信息
        VisualDTO visualDTO = export(id);
        //大屏文件数据【不包括封面图】
        List<OssFile> ossFileList = queryFileWithVisualId(id);
        //封面图
        OssFile backgroundFile = ossFileMapper.queryBackGroundWithVisualId(id);
        //保存大屏配置信息和大屏文件
        String fileName = categoryKey + StringPool.SLASH + visualDTO.getVisual().getTitle();
        String fileSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + fileName;
        if (ObjectUtil.isNotEmpty(backgroundFile)){
            ossFileList.add(backgroundFile);
        }
        //保存大屏配置信息和大屏文件
        //保存大屏配置信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(visualDTO),fileSavePath + StringPool.SLASH + VISUAL_JSON_NAME);
        //保存数据文件信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(ossFileList),fileSavePath + StringPool.SLASH + FILE_JSON_NAME);
        //保存封面图信息
        JsonUtil.writeJsonToFile(JSON.toJSONString(backgroundFile),fileSavePath + StringPool.SLASH + BACKGROUND_FILE_JSON_NAME);
        //下载文件到本地
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
                }finally {
                    latch.countDown();
                }
            });
        });
        latch.await();
        log.info("-------->下载图片地址完成,"+ossFileList.size()+"个文件耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");

        return fileSavePath;
    }


    //递归获取当前查询目录的所有子目录下 记录其categoryValue
    private void getVisualData(VisualCategory visualCategory, List<String> categoryValueList) {
        if (visualCategory != null) {
            LambdaQueryWrapper<VisualCategory> queryWrapper = new LambdaQueryWrapper<VisualCategory>();
            queryWrapper.eq(VisualCategory::getParentId, visualCategory.getId());
            List<VisualCategory> visualCategories = visualCategoryService.list(queryWrapper);
            if (visualCategories != null) {
                visualCategories.forEach(bean -> {
                    categoryValueList.add(bean.getCategoryValue());
                    getVisualData(bean, categoryValueList);
                });
            }
        }
    }

    /**
     * 创建新文件
     * @param path
     * @return
     */
    private File createNewFile(String path){
        File file = new File(path);
        if (!file.exists()){
            File parentFile = file.getParentFile();
            try{
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 上传文件
     * @param source
     * @param /import
     * @param fileName
     * @param inputStream
     * @return
     */
    public OssFile saveOssFileWithFileName(String source, String type, String fileName, InputStream inputStream) {
        OssFileVO ossFileVO = ossTemplate.putFile(StdcVisualConstant.OSS_PREFIX_BUCKET, source + StringPool.SLASH +fileNameHandler(fileName, type), inputStream);
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

    public OssFile saveOssFileWithRandomUUID(String source, String type, String fileName, InputStream inputStream) {
        fileName = StringUtil.randomUUID() + StringPool.DOT + getFileExtension(fileName);
        return saveOssFileWithFileName(source,type,fileName,inputStream);
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

    /**
     * 通过大屏id查询，但是记录里面没有保存大屏id字段的文件
     * 先查询大屏的 component 字段，通过 link 进行比对 component，找到 ossFile
     * @return
     */
    private List<OssFile> queryFileWithVisualId(Long visualId){
        //大屏文件
        VisualConfig visualConfig = configService.getOne(new LambdaQueryWrapper<VisualConfig>()
                .eq(VisualConfig::getVisualId, visualId)
                .orderByDesc(VisualConfig::getUpdateTime)
                .last("limit 1")
        );
        //遍历每一个大屏,找出以 http://  开头  文件后缀结尾的  url地址
        String lowercasePrefix = "http://";
        String lowercaseSuffix = OssFileType.SVG.getValue() + StringPool.COMMA +
                OssFileType.CSS.getValue() + StringPool.COMMA +
                OssFileType.BACK_IMG.getValue() + StringPool.COMMA +
                OssFileType.PSD.getValue() + StringPool.COMMA +
                "ttf" + StringPool.COMMA +
                "glb" + StringPool.COMMA +
                "ico" + StringPool.COMMA;
        Set<String> fileSet = new HashSet<>();
        long startTime = System.currentTimeMillis();
        fileSet.addAll(findMatchingStrings(JSON.toJSONString(visualConfig), lowercasePrefix, lowercaseSuffix,120));
        log.info("---------->>>>>匹配文件列表"+fileSet.size()+"个,耗时："+(System.currentTimeMillis() - startTime)+" ms");
        List<OssFile> configFileS = new ArrayList<>();
        for (String fileLink : fileSet) {
            OssFile ossFile = new OssFile();
            ossFile.setLink(fileLink);
            // 获取文件名
            int index = fileLink.lastIndexOf("/");
            String fileNameWithExtension = getNameWithoutExtension(fileLink.substring(index + 1));
            ossFile.setNamePrefix(fileNameWithExtension);
            //获取文件后缀
            ossFile.setNameSuffix(getFileExtension(fileLink));
            //设置domain
            ossFile.setDoMain("/stdc-visual/custom/component/"+ossFile.getNamePrefix()+ StringPool.DOT +ossFile.getNameSuffix());
            ossFile.setSystemType("custom");
            ossFile.setType("componet");
            configFileS.add(ossFile);
        }

//        List<VisualConfig> configs = configService.list(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getVisualId, visualId).orderByDesc(VisualConfig::getUpdateTime).last("limit 1"));
//        VisualConfig configBack = configs.get(0);
//        封面图文件
//        List<OssFile> backGroundFiles = ossFileMapper.selectList(new LambdaQueryWrapper<OssFile>().eq(OssFile::getId, configBack.getBackgroundId()));
//        List<Long> configIdS = configService.list(new LambdaQueryWrapper<VisualConfig>().select(VisualConfig::getId).eq(VisualConfig::getVisualId, visualId)).stream().map(VisualConfig::getId).collect(Collectors.toList());
//        List<OssFile> configFiles = new ArrayList<>();
//        configIdS.forEach(c->{
//            String configId = String.valueOf(c);
//            configFiles.addAll(ossFileMapper.selectList(new LambdaQueryWrapper<OssFile>()
//                    .select(OssFile::getId,OssFile::getNamePrefix,OssFile::getNameSuffix,
//                            OssFile::getLink,OssFile::getDoMain,OssFile::getType,
//                            OssFile::getSystemType,OssFile::getIsDeleted,OssFile::getCreateTime,OssFile::getUpdateTime)
//                    .like(OssFile::getConfigId, configId)));
//        });
//        configFiles.addAll(backGroundFiles);
//
        return configFileS;
    }

    /**
     * 获取网络地址文件名称
     * @param fileURL
     * @return
     */
    private static String getFileNameFromURL(String fileURL) {
        String fileName = "";
        int queryIndex = fileURL.indexOf(63);
        if (queryIndex > 0) {
            fileName = fileURL.substring(fileURL.lastIndexOf(47) + 1, queryIndex);
        } else {
            fileName = fileURL.substring(fileURL.lastIndexOf(47) + 1);
        }

        return fileName;
    }

    /**
     * 找到匹配列表,不区分大小写
     * @param input
     * @param prefix
     * @param suffixS
     * @return
     */
    public static List<String> findMatchingStrings(String input, String prefix, String suffixS,Integer maxLength) {

        List<String> matchedStrings = new ArrayList<>();
        String suffixRegex = String.join("|", suffixS.split(StringPool.COMMA));
        String regex = Pattern.quote(prefix) + ".*?(" + suffixRegex + ")";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String match = matcher.group();
            if (match.length() < maxLength){
                matchedStrings.add(match);
            }
        }
        return matchedStrings;
    }
}
