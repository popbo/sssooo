package com.stdc.visual.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.oss.prop.OssFileType;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.oss.utils.OssTemplate;
import com.stdc.core.oss.vo.OssFileVO;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.DateUtil;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.role.po.RolePO;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.*;
import com.stdc.visual.entity.dto.VisualDTO;
import com.stdc.visual.entity.po.OssFile;
import com.stdc.visual.entity.request.OssFileRequest;
import com.stdc.visual.mapper.OssFileMapper;
import com.stdc.visual.service.IVisualOssFileService;
import com.stdc.visual.service.IVisualService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.stdc.core.oss.utils.FileUtil.*;
import static com.stdc.core.oss.utils.FileUtil.delFile;
import static com.stdc.visual.entity.po.OssFile.FILE_SOURCE_VISUAL;

/**
 * @author: wang_jie
 * @data: 2022/5/27--11:47
 * @describe: 文件存储
 */
@Slf4j
@Service
public class VisualOssFileServiceImpl implements IVisualOssFileService {

    @Autowired
    private OssFileMapper ossFileMapper;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private RoleSourceService roleSourceService;

    @Autowired
    private IVisualService visualService;

    @Autowired
    private CommonThreadPool commonThreadPool;

    /**
     * 创建临时文件夹
     */
//    @PostConstruct
//    public void init(){
//        File file = new File(CachePath);
//        if (!file.exists()){
//            boolean mkdirs = file.mkdirs();
//            if (mkdirs){
//                log.info("mkdir success --- " + CachePath);
//            }else {
//                log.error("mkdir error --- " + CachePath);
//            }
//        }else {
//            log.info("CachePath exits --- " + CachePath);
//        }
//    }

    @Override
    public List<OssFile> query(String source, String type) {
        LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
        //是否为系统、自定义
        queryWrapper.eq(OssFile::getSystemType,source);
        //类型分类
        queryWrapper.eq(OssFile::getType,type);
        //权限筛选
        SourceType saveType = SourceType.OSS_FILE;
        switch (type){
            case "svg":
                saveType = SourceType.OSS_FILE_SVG;
                break;
        }
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(saveType);
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        queryWrapper.in(OssFile::getId,ids);
        return ossFileMapper.selectList(queryWrapper);
    }

    @Override
    public List<OssFile> query3D() {
        LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
        //是否为系统、自定义
        queryWrapper.eq(OssFile::getSystemType,OssFileType.CUSTOM_BUCKET);
        //类型分类
        queryWrapper.eq(OssFile::getType,OssFileType.COMPONENT.getKey());
        //权限筛选
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.OSS_FILE_GLB);
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        queryWrapper.in(OssFile::getId,ids);
        return ossFileMapper.selectList(queryWrapper);
    }

    @Override
    public List<OssFile> queryWithId(String source, String type, Long visualId, String componentId) {
        LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
        //是否为系统、自定义
        queryWrapper.eq(OssFile::getSystemType,source);
        //类型分类
        queryWrapper.eq(OssFile::getType,type);
        //大屏id
        if (ObjectUtil.isNotEmpty(visualId)) queryWrapper.eq(OssFile::getVisualId,visualId);
        //组件id
        if (StringUtil.hasText(componentId)) queryWrapper.eq(OssFile::getComponetId,componentId);
        //权限筛选
        SourceType saveType = SourceType.OSS_FILE;
        switch (type){
            case "svg":
                saveType = SourceType.OSS_FILE_SVG;
                break;
        }
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(saveType);
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        queryWrapper.in(OssFile::getId,ids);
        return ossFileMapper.selectList(queryWrapper);
    }

    @Override
    public List<OssFile> queryCurrentConfigImg(Long visualId, Long configId) {
        //是否为系统文件: system 系统 custom自定义
        String source = "custom";
        //文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)
        String type = "component";
        LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
        //是否为系统、自定义
        queryWrapper.eq(OssFile::getSystemType,source);
        //类型分类
        queryWrapper.eq(OssFile::getType,type);
        //大屏id
        if (ObjectUtil.isNotEmpty(visualId)) queryWrapper.like(OssFile::getVisualId,visualId);
        //页面id
        if (ObjectUtil.isNotEmpty(configId)) queryWrapper.like(OssFile::getConfigId,configId);
        //确认后缀
        List<String> suffixS = Arrays.asList("bmp", "jpg", "jpeg", "png", "gif",
                                                    "BMP", "JPG", "JPEG", "PNG", "GIF");
        queryWrapper.in(OssFile::getNameSuffix,suffixS);
        queryWrapper.orderByDesc(OssFile::getCreateTime);
        return ossFileMapper.selectList(queryWrapper);
    }

    @Override
    public List<OssFile> queryCurrentComponentImg(String componentId) {
        //是否为系统文件: system 系统 custom自定义
        String source = "custom";
        //文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)
        String type = "component";
        LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
        //是否为系统、自定义
        queryWrapper.eq(OssFile::getSystemType,source);
        //类型分类
        queryWrapper.eq(OssFile::getType,type);
        //组件id
        if (ObjectUtil.isNotEmpty(componentId)) queryWrapper.like(OssFile::getComponetId,componentId);
        //确认后缀
        List<String> suffixS = Arrays.asList("bmp", "jpg", "jpeg", "png", "gif",
                "BMP", "JPG", "JPEG", "PNG", "GIF");
        queryWrapper.in(OssFile::getNameSuffix,suffixS);
        queryWrapper.orderByDesc(OssFile::getCreateTime);
        return ossFileMapper.selectList(queryWrapper);
    }

    @Override
    public List<OssFile> queryCurrentComponentFileExcludeImg(String componentId) {
        //是否为系统文件: system 系统 custom自定义
        String source = "custom";
        //文件类型: svg(svg图片),font(字体文件),css(css文件),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)
        String type = "component";
        LambdaQueryWrapper<OssFile> queryWrapper = new LambdaQueryWrapper<>();
        //是否为系统、自定义
        queryWrapper.eq(OssFile::getSystemType,source);
        //类型分类
        queryWrapper.eq(OssFile::getType,type);
        //组件id
        if (ObjectUtil.isNotEmpty(componentId)) queryWrapper.like(OssFile::getComponetId,componentId);
        //确认后缀
        List<String> suffixS = Arrays.asList("bmp", "jpg", "jpeg", "png", "gif",
                "BMP", "JPG", "JPEG", "PNG", "GIF");
        //排除图片文件
        queryWrapper.notIn(OssFile::getNameSuffix,suffixS);
        queryWrapper.orderByDesc(OssFile::getCreateTime);
        return ossFileMapper.selectList(queryWrapper);
    }

    @Override
    public boolean delCurrentConfigImg(String id) {
        return ossFileMapper.deleteById(id) > 0;
    }

    /**
     * 根据文件类型查询当前用户所属下的文件
     * @param source
     * @param type
     * @return
     */
    @Override
    public List<OssFile> queryFileByType(String source, String type, Integer current,Integer size) {
        SysUserDTO user = AuthUtils.getUser();
        List<String> roles = user.getRoles().stream().map(RolePO::getRoleId).collect(Collectors.toList());
        return ossFileMapper.queryFileByType(roles,source,type,(current -1 ) * size,size);
    }

    @Override
    public String convertJSONToExcel(String jsonBody) {
        String ret = "";
        JSONObject bodyObj = JSON.parseObject(jsonBody);
        String name = bodyObj.getString("name");
        String data = bodyObj.getString("data");
        //xlsx文件名称
        String xlsxName = DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + StringPool.SLASH + name + ".xlsx";
        String xlsxSavePath = "./static/export/" + xlsxName;
        FileInputStream inputStream =null;
        try {
            //将文件夹压缩为zip包并且导入到本地
            createFile(xlsxSavePath);
            PoiUtils.convertJSONToExcel(name,data,xlsxSavePath);
            File file = new File(xlsxSavePath);
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", xlsxName ,inputStream);
            ret = StringPool.SLASH + save.getDoMain();
        } catch (Exception e) {
            log.error("生成文件失败...");
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
            delFile(xlsxSavePath);
            commonThreadPool.addTask(()->{
                try {
                    Thread.sleep(20000);
                    delFile(xlsxSavePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return ret;
    }


    /**
     * 上传文件
     * @param source
     * @param typefull/import
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

    /**
     * 根据文件类型查询当前用户所属下的文件 个数
     * @return
     */
    @Override
    public Integer queryCountByType(String source, String type) {
        SysUserDTO user = AuthUtils.getUser();
        List<String> roles = user.getRoles().stream().map(RolePO::getRoleId).collect(Collectors.toList());
        return ossFileMapper.queryCountByType(roles,source,type);
    }


    @Override
    public IPage page(IPage page, Wrapper queryWrapper) {
        return ossFileMapper.selectPage(page,queryWrapper);
    }

    @Override
    public OssFile save(String source, String type, Long visualId, Long configId, String componentId, MultipartFile file) {
        OssFileVO ossFileVO = ossTemplate.putFile(StdcVisualConstant.OSS_PREFIX_BUCKET, source + StringPool.SLASH + fileNameHandler(file, type), file);
        if (ObjectUtil.isEmpty(ossFileVO)){
            BaseException.throwException(ResultCode.get("upload_file_error"));
        }
        OssFile ossFile = new OssFile();
        String id = StringUtil.randomUUID();
        long timeMillis = System.currentTimeMillis();
        String namePrefix = null;
        //大屏缩略图名称、大屏背景名称
        if ((StringUtil.equals(OssFileType.THUMBNAIL_IMG.getKey(),type) || StringUtil.equals(OssFileType.BACK_IMG.getKey(),type))
                && ObjectUtil.isNotEmpty(visualId)){
            VisualDTO detail = visualService.detail(visualId);
            if (ObjectUtil.isNotEmpty(detail)){
                //大屏标题title:版本号
                namePrefix = detail.getVisual().getTitle() + StringPool.COLON + detail.getConfig().getVersion();
            }
        }
        //外网环境更换为外网ip
        String outWorkLink = ossFileVO.getLink().replaceAll("http://10.255.102.116:9000","http://183.134.201.132:9000");
        ossFileVO.setLink(outWorkLink);
        ossFile.setId(id)
                .setNamePrefix(StringUtil.hasText(namePrefix) ? namePrefix : FileUtil.getNameWithoutExtension(file.getOriginalFilename()))
                .setNameSuffix(FileUtil.getFileExtension(file.getOriginalFilename()))
                .setLink(ossFileVO.getLink())
                .setDoMain(ossFileVO.getDoMain())
                .setVisualId(String.valueOf(visualId))
                .setConfigId(String.valueOf(configId))
                .setComponetId(componentId)
                .setType(type)
                .setSystemType(source)
                .setSource(FILE_SOURCE_VISUAL)
                .setIsDeleted(0)
                .setCreateTime(timeMillis)
                .setUpdateTime(timeMillis);
        int i = ossFileMapper.insert(ossFile);
        if (i > 0){
            SourceType saveType = SourceType.OSS_FILE;
            switch (type){
                case "svg":
                    saveType = SourceType.OSS_FILE_SVG;
                    break;
            }
            roleSourceService.saveRoleSource(id,saveType);
            return ossFileMapper.selectById(id);
        }
        return null;
    }

    @Override
    public OssFile save(String source, String type, Long visualId, String componentId, MultipartFile file) {
       return save(source,type,visualId,null,componentId,file);
    }

    @Override
    public OssFile save3D(String source, String type, Long visualId, String componentId, MultipartFile file) {
        OssFileVO ossFileVO = ossTemplate.putFile(StdcVisualConstant.OSS_PREFIX_BUCKET, source + StringPool.SLASH + fileNameHandler(file, type), file);
        if (ObjectUtil.isEmpty(ossFileVO)){
            BaseException.throwException(ResultCode.get("upload_file_error"));
        }
        OssFile ossFile = new OssFile();
        String id = StringUtil.randomUUID();
        long timeMillis = System.currentTimeMillis();
        String namePrefix = null;
        //大屏缩略图名称、大屏背景名称
        if ((StringUtil.equals(OssFileType.THUMBNAIL_IMG.getKey(),type) || StringUtil.equals(OssFileType.BACK_IMG.getKey(),type))
                && ObjectUtil.isNotEmpty(visualId)){
            VisualDTO detail = visualService.detail(visualId);
            if (ObjectUtil.isNotEmpty(detail)){
                //大屏标题title:版本号
                namePrefix = detail.getVisual().getTitle() + StringPool.COLON + detail.getConfig().getVersion();
            }
        }
        //外网环境更换为外网ip
        String outWorkLink = ossFileVO.getLink().replaceAll("http://10.255.102.116:9000","http://183.134.201.132:9000");
        ossFileVO.setLink(outWorkLink);
        ossFile.setId(id)
                .setNamePrefix(StringUtil.hasText(namePrefix) ? namePrefix : FileUtil.getNameWithoutExtension(file.getOriginalFilename()))
                .setNameSuffix(FileUtil.getFileExtension(file.getOriginalFilename()))
                .setLink(ossFileVO.getLink())
                .setDoMain(ossFileVO.getDoMain())
                .setVisualId(String.valueOf(visualId))
                .setComponetId(componentId)
                .setType(type)
                .setSystemType(source)
                .setSource(FILE_SOURCE_VISUAL)
                .setIsDeleted(0)
                .setCreateTime(timeMillis)
                .setUpdateTime(timeMillis);
        int i = ossFileMapper.insert(ossFile);
        if (i > 0){
            roleSourceService.saveRoleSource(id, SourceType.OSS_FILE_GLB);
            return ossFileMapper.selectById(id);
        }
        return null;
    }

    @Override
    public boolean update(String source, String type, OssFileRequest request) {
        if (StringUtil.isBlank(request.getId())){
            BaseException.throwException(ResultCode.get("id_is_empty"));
        }
        OssFile ossFile = new OssFile();
        BeanUtils.copyProperties(request,ossFile);
        int i = ossFileMapper.updateById(ossFile);
        return i > 0;
    }

    @Override
    public boolean del(String id) {
        if (StringUtil.isBlank(id)){
            BaseException.throwException(ResultCode.get("id_is_empty"));
        }
        OssFile ossFile = ossFileMapper.selectById(id);
        //删除minio文件
        String[] doMain = ossFile.getDoMain().split(StringPool.SLASH, 2);
        ossTemplate.removeFile(StdcVisualConstant.OSS_PREFIX_BUCKET,doMain[0]+StringPool.SLASH+doMain[1]);
        int i = ossFileMapper.deleteById(id);
        return i > 0;
    }

    /**
     * 文件分类处理-处理名称
     * @param file
     * @param type
     * @return
     */
    private String fileNameHandler(MultipartFile file,String type){
        String suffix = FileUtil.getFileExtension(file.getOriginalFilename());
        String fileName = StringUtil.randomUUID() + StringPool.DOT + suffix;
        OssFileType fileType = OssFileType.get(type);
        //校验文件格式
        if (OssFileType.isNotInValues(fileType,suffix)){
            BaseException.throwException(ResultCode.get("file_format_error"));
        }
        return fileType.getKey() + StringPool.SLASH + fileName;
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
     * 临时缓存目录添加css文件
     * @return
     */
    public boolean writeFontCss(String userName) {
        String source = "custom";
        String type = "font";
        SysUserDTO user = AuthUtils.getUserByName(userName);
        List<String> roles = user.getRoles().stream().map(RolePO::getRoleId).collect(Collectors.toList());
        List<OssFile> ossFiles = ossFileMapper.queryFileByType(roles, source, type, 0, ossFileMapper.queryCountByType(roles, source, type));
        StringBuilder data = new StringBuilder();
        ossFiles.forEach(f-> data.append(String.format(FONT_OSS_TEMPLATE,f.getNamePrefix(),f.getLink())));
        return uploadByStringToFontCss(data.toString(),userName);
    }

    /**
     * 上传信息 到 临时目录下 font.css文件中
     * @param data
     * @return 文件路径
     */
    private synchronized boolean uploadByStringToFontCss(String data,String username){
        String uploadPath = null;
        FileWriter fileWriter = null;
        try {
            uploadPath = "./static" + String.format(CachePath,username);
            File outFile = new File(uploadPath);
            //文件不存在 创建文件
            if (!outFile.exists()){
                File parentFile = outFile.getParentFile();
                if (!parentFile.exists()) parentFile.mkdirs();
                outFile.createNewFile();
            }
            fileWriter = new FileWriter(outFile,false);
            fileWriter.write(data);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (ObjectUtil.isNotEmpty(fileWriter)) fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
