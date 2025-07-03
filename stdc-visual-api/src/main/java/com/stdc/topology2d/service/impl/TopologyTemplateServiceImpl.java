package com.stdc.topology2d.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.stdc.project.mapper.ProjectMapper;
import com.stdc.topology2d.constants.ModelConstants;
import com.stdc.topology2d.constants.TopologyConstants;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.entity.po.TopologyTemplate;
import com.stdc.topology2d.mapper.TopologyDataMapper;
import com.stdc.topology2d.mapper.TopologyTemplateMapper;
import com.stdc.topology2d.service.ITopologyTemplateService;
import com.stdc.topology2d.util.SnowflakeIdGenerator;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.common.utils.CommonThreadPool;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.common.utils.StdcVisualConstant;
import com.stdc.visual.common.utils.ZipFileUtils;
import com.stdc.visual.entity.po.OssFile;
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
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.stdc.core.oss.utils.FileUtil.*;

@Slf4j
@Service
public class TopologyTemplateServiceImpl extends ServiceImpl<TopologyTemplateMapper, TopologyTemplate> implements ITopologyTemplateService {

    private static final String EXPORT_FILE_CACHE_PATH = "./static/export";
    private static final String TEMPLATE_JSON_NAME = "template.json";

    @Autowired
    private SnowflakeIdGenerator idGenerator;
    
    @Autowired
    private TopologyDataMapper topologyDataMapper;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private CommonThreadPool commonThreadPool;

    @Autowired
    private IVisualReleaseService visualReleaseService;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    @Transactional
    public List saveByData(Map<String, Object> dataMap) {
        TopologyTemplate topologyTemplate = new TopologyTemplate();
        Map resmap = new HashMap<>();
        //新增的时候保存用户和创建时间
        SysUserDTO user = AuthUtils.getUser();
        if (topologyTemplate.getTemplateId() == null) {
            if (user != null) {
                topologyTemplate.setUsername(user.getUsername());
                topologyTemplate.setCreateBy(user.getUsername());
            }
            topologyTemplate.setCreateTime(new Date());
        } else {
            if (user != null) {
                topologyTemplate.setUpdateBy(user.getUsername());
            }
            topologyTemplate.setUpdateTime(new Date());
        }

        List resList = new ArrayList();
        //处理数据
        if (!ObjectUtils.isEmpty(dataMap)) {
            // 新增时预获取雪花算法生成的id
            Long snowId = idGenerator.nextId();
            topologyTemplate.setData(JSONObject.toJSONString(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_PEN)));
            //工程id
            String projectId = "";
            if (dataMap.containsKey(TopologyConstants.TOPOLOGY_TEMPLATE_PROJECTID)) {
                projectId = String.valueOf(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_PROJECTID));
                topologyTemplate.setProjectId(projectId);
            }
            //文件名
            if (dataMap.containsKey(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_NAME)) {
                String templateName = String.valueOf(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_NAME));
                if (!dataMap.containsKey(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID)
                        && checkTemplateName(templateName,projectId)) {
                    resmap.put("error", "模板名字重复，请重新命名");
                    resList.add(resmap);
                    return resList;
                }
                topologyTemplate.setName(templateName);
            }
            //版本号
            if (dataMap.containsKey(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_VERSION)) {
                topologyTemplate.setVersion(String.valueOf(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_VERSION)));
            }

            //模板id
            if (dataMap.containsKey(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID)) {
                topologyTemplate.setTemplateId(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID).toString());
                topologyTemplate.setId(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID).toString());
            } else {
                dataMap.put(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID, snowId.toString());
                topologyTemplate.setId(snowId.toString());
                topologyTemplate.setTemplateId(snowId.toString());
            }

            //图纸文件夹名
            if (dataMap.containsKey(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_FOLDER)) {
                topologyTemplate.setFolder(String.valueOf(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_FOLDER)));
            } else {
                topologyTemplate.setFolder("未分类");
            }

        }

        this.saveOrUpdate(topologyTemplate);

        resmap = (Map) dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_PEN);
        resmap.put(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID, topologyTemplate.getTemplateId());
        resmap.put(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_FOLDER, topologyTemplate.getFolder());
        resmap.put(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_NAME,topologyTemplate.getName());

        resList.add(resmap);

        return resList;
    }

    @Override
    public List updateByData(Map<String, Object> dataMap) {
        List<TopologyTemplate> resList = new ArrayList();
        if (!ObjectUtils.isEmpty(dataMap)) {
            String templateId = dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID).toString();//模板id
            String penStr = JSONObject.toJSONString(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_PEN));
            JSONObject penJson = JsonUtil.parseObject(penStr); //请求参数pen
            JSONObject dataJson = penJson.getJSONObject("data"); //请求参数pen.data

            TopologyTemplate topologyTemplate = baseMapper.selectById(templateId);
            JSONObject data = JsonUtil.parseObject(topologyTemplate.getData());
            JSONObject dataData = data.getJSONObject("data");
            dataJson.entrySet().stream().forEach(entry -> {
                dataData.put(entry.getKey(), entry.getValue());
            });
            topologyTemplate.setData(data.toJSONString());
            baseMapper.updateById(topologyTemplate);
            resList.add(topologyTemplate);
        }
        return resList;
    }

    @Override
    public void topologyUpdate(Map<String, Object> dataMap) {
        String templateId = dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID).toString();//模板id
        String penStr = JSONObject.toJSONString(dataMap.get(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_PEN));
        JSONObject penJson = JsonUtil.parseObject(penStr); //请求参数pen
        JSONObject dataJson = penJson.getJSONObject("data"); //请求参数pen.data

        //idList不为空 只替换指定id的数据
        if (dataMap.containsKey("idList") && !ObjectUtils.isEmpty(dataMap.get("idList"))) {
            List<String> idList  = new ArrayList<>();
            idList = (List<String>) dataMap.get("idList");
            TopologyData topologyData = null;
            for (String id : idList) {
                System.out.println("========" + id + "========");
                topologyData = topologyDataMapper.selectById(id);
                //替换字段
                batchEditor(templateId, penJson, dataJson, topologyData);
            }
        } else if (dataMap.containsKey("projectId") && !ObjectUtils.isEmpty(dataMap.get("projectId"))) {
            // projectId不为空 替换该工程下所有数据
            QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, AuthUtils.getUser().getUsername());
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_PROJECTID_PROPERTY, dataMap.get("projectId").toString());
            List<TopologyData> list = topologyDataMapper.selectList(queryWrapper);
            list.stream().forEach(topologyData -> {
                System.out.println("========" + topologyData.getId() + "========");
                //替换字段
                batchEditor(templateId, penJson, dataJson, topologyData);
            });
        } else {
            QueryWrapper<TopologyData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_COLLECTION_PROPERTY, TopologyConstants.TOPOLOGY_DATA_TYPE);
            queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, AuthUtils.getUser().getUsername());
            List<TopologyData> list = topologyDataMapper.selectList(queryWrapper);
            list.stream().forEach(topologyData -> {
                System.out.println("========" + topologyData.getId() + "========");
                //替换字段
                batchEditor(templateId, penJson, dataJson, topologyData);
            });
        }
    }

    @Override
    public String export(String id) throws Exception {
        String ret = null;
        TopologyTemplate topologyTemplate = baseMapper.selectById(id);
        // 导出数据中加入folder 以便导入使用
        JSONObject data = JsonUtil.parseObject(topologyTemplate.getData());
        data.put(ModelConstants.TOPOLOGY_TEMPLATE_FOLDER_PROPERTY, topologyTemplate.getFolder());
        //缓存地址
        String cachePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + StringUtil.randomUUID();
        JsonUtil.writeJsonToFile(data.toJSONString(),cachePath + StringPool.SLASH + TEMPLATE_JSON_NAME);
        //将本地目录打成zip文件并且写入响应体
        //压缩包文件名称
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + topologyTemplate.getName() + ".zip";
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
    public void templateImport(MultipartFile file, String category, String name) throws Exception {
        //获取zip文件包对象
        String zipPath= new File("").getAbsolutePath() + StringPool.SLASH + "static" + StringPool.SLASH + StringUtil.randomUUID() + StringPool.UNDERSCORE +file.getOriginalFilename();
        File convFile = new File(zipPath);
        file.transferTo(convFile);
        ZipFile zipFile = new ZipFile(convFile, Charset.forName("GBK"));

        //获取 template.json 对象
        List<? extends ZipEntry> templateJson = zipFile.stream().filter(
                entry -> entry.getName().contains(TEMPLATE_JSON_NAME)).collect(Collectors.toList());
        ZipEntry templateJsonZipEntry = null;
        if (!CollectionUtils.isEmpty(templateJson)) {
            templateJsonZipEntry = templateJson.stream().findFirst().get();
        }
        //读取 template.json 内容
        String templateStr = null;
        if (ObjectUtil.isNotEmpty(templateJsonZipEntry)){
            templateStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(templateJsonZipEntry))).readLine();
        }

        TopologyTemplate topologyTemplate = new TopologyTemplate();
        JSONObject jsonObject = JsonUtil.parseObject(templateStr);
        Long snowId = idGenerator.nextId();
        jsonObject.put(ModelConstants.TOPOLOGY_TEMPLATE_ID_PROPERTY, snowId);
        jsonObject.put(ModelConstants.TOPOLOGY_TEMPLATE_NAME_PROPERTY, name);
        topologyTemplate.setId(snowId.toString());
        topologyTemplate.setTemplateId(snowId.toString());
        topologyTemplate.setName(name);
        topologyTemplate.setFolder(jsonObject.getString(ModelConstants.TOPOLOGY_TEMPLATE_FOLDER_PROPERTY));
        topologyTemplate.setData(JsonUtil.toJSONString(jsonObject));
        topologyTemplate.setCreateTime(new Date());
        SysUserDTO user = AuthUtils.getUser();
        topologyTemplate.setCreateBy(user.getUsername());
        topologyTemplate.setUsername(user.getUsername());
        this.saveOrUpdate(topologyTemplate);

        //关闭
        zipFile.close();
        //删除缓存文件
        delFile(convFile.getPath());
    }

    @Override
    public String exportByProjectId(String projectId) throws Exception {
        String ret = null;
        Project project = projectMapper.selectById(projectId);
        String projectName = project.getName() +"组件模板";
        String downFilePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + projectName;
        QueryWrapper<TopologyTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ModelConstants.TOPOLOGY_TEMPLATE_PROJECTID_PROPERTY, projectId);
        List<TopologyTemplate> list = baseMapper.selectList(queryWrapper);
        for (TopologyTemplate topologyTemplate : list) {
            // 导出数据中加入folder 以便导入使用
            JSONObject data = JsonUtil.parseObject(topologyTemplate.getData());
            data.put(ModelConstants.TOPOLOGY_TEMPLATE_FOLDER_PROPERTY, topologyTemplate.getFolder());
            //缓存地址
            String fileName = projectName + StringPool.SLASH + topologyTemplate.getName();
            String fileSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + fileName;
            JsonUtil.writeJsonToFile(data.toJSONString(),fileSavePath + StringPool.SLASH + TEMPLATE_JSON_NAME);
        }
        //将本地目录打成zip文件并且写入响应体
        //压缩包文件名称
        String zipName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + projectName + ".zip";
        String zipSavePath = EXPORT_FILE_CACHE_PATH + StringPool.SLASH + zipName;
        FileInputStream inputStream =null;
        try {
            //将文件夹压缩为zip包并且导入到本地
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
    public void templateImportByProjectId(MultipartFile file, String projectId) throws Exception {
        //获取zip文件包对象
        String zipPath= new File("").getAbsolutePath() + StringPool.SLASH + "static" + StringPool.SLASH + StringUtil.randomUUID() + StringPool.UNDERSCORE +file.getOriginalFilename();
        File convFile = new File(zipPath);
        file.transferTo(convFile);
        ZipFile zipFile = new ZipFile(convFile, Charset.forName("GBK"));

        //获取 template.json 对象
        List<? extends ZipEntry> templateJson = zipFile.stream().filter(
                entry -> entry.getName().contains(TEMPLATE_JSON_NAME)).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(templateJson)) {
            for (ZipEntry templateJsonZipEntry : templateJson) {
                //读取 template.json 内容
                String templateStr = null;
                if (ObjectUtil.isNotEmpty(templateJsonZipEntry)){
                    templateStr = new BufferedReader(new InputStreamReader(zipFile.getInputStream(templateJsonZipEntry))).readLine();
                }
                TopologyTemplate topologyTemplate = new TopologyTemplate();
                JSONObject jsonObject = JsonUtil.parseObject(templateStr);
                Long snowId = idGenerator.nextId();
                jsonObject.put(ModelConstants.TOPOLOGY_TEMPLATE_ID_PROPERTY, snowId);
                topologyTemplate.setId(snowId.toString());
                topologyTemplate.setTemplateId(snowId.toString());
                topologyTemplate.setProjectId(projectId);
                topologyTemplate.setName(jsonObject.getString(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_NAME));
                topologyTemplate.setFolder(jsonObject.getString(ModelConstants.TOPOLOGY_TEMPLATE_FOLDER_PROPERTY));
                topologyTemplate.setData(JsonUtil.toJSONString(jsonObject));
                topologyTemplate.setCreateTime(new Date());
                SysUserDTO user = AuthUtils.getUser();
                topologyTemplate.setCreateBy(user.getUsername());
                topologyTemplate.setUsername(user.getUsername());
                this.saveOrUpdate(topologyTemplate);
            }
        }

        //关闭
        zipFile.close();
        //删除缓存文件
        delFile(convFile.getPath());
    }

    //批量替换
    private void batchEditor(String templateId, JSONObject penJson, JSONObject dataJson, TopologyData topologyData) {
        JSONObject data = JsonUtil.parseObject(topologyData.getData());
        JSONArray pens = data.getJSONArray("pens"); //数据库中pens
        if (pens != null) {
            for (int i=0; i<pens.size(); i++) {
                JSONObject pen = pens.getJSONObject(i); //数据库中pens.pen
                JSONObject stencilParms = pen.getJSONObject("stencilParms");
                if (stencilParms != null) {
                    //模板id相同再进行替换
                    if (!StringUtil.isBlank(stencilParms.getString("templateId"))) {
                        if (stencilParms.getString("templateId").equals(templateId)) {
                            //请求参数pen里除data外 逐个替换pens.pen.stencilParms中的值
                            penJson.entrySet().stream().forEach(entry -> {
                                if (!entry.getKey().equals("data")) {
                                    stencilParms.put(entry.getKey(), entry.getValue());
                                }
                            });
                            //请求参数pen.data的值 逐个替换数据库中pens.pen的值
                            dataJson.entrySet().stream().forEach(entry -> {
                                pen.put(entry.getKey(), entry.getValue());
                            });
                        }
                    }
                }
            }
            topologyData.setData(data.toJSONString());
            topologyDataMapper.updateById(topologyData);

            VisualRelease visualRelease = visualReleaseService.getById(topologyData.getId());
            if  (visualRelease != null && visualRelease.getId() != null) {
                visualRelease.setComponent(data.toJSONString());
                visualReleaseService.updateById(visualRelease);
            }
        }
    }

    private boolean checkTemplateName(String templateName, String projectId) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ModelConstants.TOPOLOGY_TEMPLATE_NAME_PROPERTY, templateName);
        queryWrapper.eq(ModelConstants.TOPOLOGY_TEMPLATE_PROJECTID_PROPERTY, projectId);
        return this.count(queryWrapper) > 0;
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
