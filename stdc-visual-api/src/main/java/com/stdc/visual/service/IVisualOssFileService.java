package com.stdc.visual.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.visual.common.utils.HttpServletUtil;
import com.stdc.visual.entity.po.OssFile;
import com.stdc.visual.entity.request.OssFileRequest;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/27--11:46
 * @describe: 文件存储
 */
public interface IVisualOssFileService {


    /**
     * css文件临时存储目录   /css/%s_font.css
     */
     String CachePath =  StringPool.SLASH + "css" + StringPool.SLASH + "%s" + StringPool.UNDERSCORE  + "font.css";


    /**
     * font.css模板
     */
    String FONT_OSS_TEMPLATE = "@font-face{font-family:\"%s\";src:url(\"%s\");font-weight:normal;font-style:normal;}";

    /**
     * 查询文件
     * @param source
     * @param type
     * @return
     */
    List<OssFile> query(String source, String type);

    /**
     * 查询3D文件
     * @return
     */
    List<OssFile> query3D();

    /**
     * 查询大屏内部文件
     * @param source
     * @param type
     * @return
     */
    List<OssFile> queryWithId(String source, String type,Long visualId,String componentId);

    /**
     * 查询当前大屏上传的组件图片
     * @param visualId
     * @param configId
     * @return
     */
    List<OssFile> queryCurrentConfigImg(Long visualId,Long configId);

    /**
     * 查询当前自定义组件上传的组件图片
     * @param componentId
     * @return
     */
    List<OssFile> queryCurrentComponentImg(String componentId);

    /**
     * 查询当前自定义组件除图片之外的文件
     * @param componentId
     * @return
     */
    List<OssFile> queryCurrentComponentFileExcludeImg(String componentId);

    /**
     * 删除当前大屏上传的组件图片
     * @param id
     * @return
     */
    boolean delCurrentConfigImg(String id);
    /**
     * 根据文件类型查询当前用户所属下的文件
     * @return
     */
    List<OssFile> queryFileByType(String source, String type, Integer current,Integer size);

    /**
     * 二级表格导出
     * @param jsonBody
     * @return
     */
    String convertJSONToExcel(String jsonBody) ;

    /**
     * 根据文件类型查询当前用户所属下的文件 个数
     * @return
     */
    Integer queryCountByType(String source, String type);

    /**
     * 分页
     * @param page
     * @param queryWrapper
     * @return
     */
    IPage page(IPage page, Wrapper queryWrapper);

    /**
     * 新增
     * @param source
     * @param type
     * @param visualId
     * @param configId
     * @param componentId
     * @param file
     * @return
     */
    OssFile save(String source, String type, Long visualId,Long configId, String componentId,MultipartFile file);

    /**
     * 新增
     * @param source
     * @param type
     * @param visualId
     * @param componentId
     * @return
     */
    OssFile save(String source, String type, Long visualId, String componentId,MultipartFile file);

    /**
     * 新增
     * @param source
     * @param type
     * @param visualId
     * @param componentId
     * @return
     */
    OssFile save3D(String source, String type, Long visualId, String componentId,MultipartFile file);

    /**
     * 更新
     * @param source
     * @param type
     * @param ossFileRequest
     * @return
     */
    boolean update(String source, String type, OssFileRequest ossFileRequest);

    /**
     * 删除
     * @param id
     * @return
     */
    boolean del(String id);


    /**
     * 临时缓存目录添加css文件
     * @return
     */
    boolean writeFontCss(String userName);


}
