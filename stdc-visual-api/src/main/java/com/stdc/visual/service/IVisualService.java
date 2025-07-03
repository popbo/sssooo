package com.stdc.visual.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stdc.core.mp.utils.Query;
import com.stdc.project.entity.Project;
import com.stdc.visual.entity.dto.VisualDTO;
import com.stdc.visual.entity.dto.VisualVersionDTO;
import com.stdc.visual.entity.dto.VisualVersionVO;
import com.stdc.visual.entity.po.Visual;
import com.stdc.visual.entity.po.VisualCategory;
import com.stdc.visual.entity.request.VisualRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * 可视化表 服务类
 *
 * @author wangjie
 */
public interface IVisualService extends BaseService<Visual> {

    /**
     * 获取 可视化信息
     *
     * @param id 主键
     * @return VisualDTO
     */
    VisualDTO detail(Long id);


    /**
     * 导出功能
     * @param id
     * @return
     */
    VisualDTO export(Long id);

    /**
     * 导出包括发布地址
     * @param id
     * @return
     */
    VisualDTO exportAndRelease(Long id);

    /**
     * 导出功能-zip文件
     * @param id
     * @return
     */
    String exportZip(Long id);

    /**
     * 完整导出功能-配置+数据
     * @param id
     * @return
     */
    String fullExport(Long id) throws Exception;

    /**
     * 可视化轻量化导出功能
     * @param ids
     * @return
     * @throws Exception
     */
    String lightWeightExport(List<Long> ids);

    /**
     * 组态轻量化导出功能
     * @param ids
     * @return
     * @throws Exception
     */
    String lightWeightExportForTopology(List<Long> ids);

    /**
     * 批量导出功能-配置+数据
     * @param ids
     * @return
     */
    String batchExport(List<Long> ids) throws Exception;

    /**
     * 完整导入功能-配置+数据
     * @param file zip文件夹
     * @param category 大屏分类
     * @return
     */
    void fullImport(MultipartFile file,String category,String visualName) throws Exception;

    void fullImportFromProject(ZipFile zipFile, String projectId, HashMap<String, VisualCategory> visualCategoryMaps) throws Exception;

    /**
     * 获取相应版本可视化信息
     * @param id 大屏id
     * @param version 大屏版本
     * @return 大屏信息
     */
    VisualDTO detailVersion(Long id,String version);

    /**
     * 获取相应版本可视化信息
     * @param id 大屏id
     * @param configId 大屏配置id
     * @return 大屏信息
     */
    VisualDTO detailVersion(Long id,Long configId);

    /**
     * 保存可视化信息
     *
     * @param dtoS 配置信息
     * @return boolean
     */
    boolean saveVisual(List<VisualDTO> dtoS);

    /**
     * 保存可视化信息
     *
     * @param dtoS 配置信息
     * @return boolean
     */
    long copyVisual(List<VisualDTO> dtoS);

    /**
     * 保存可视化信息
     *
     * @param dtoS 配置信息
     * @return boolean
     */
    boolean saveVisual(VisualDTO dtoS);

    /**
     * 保存可视化信息
     *
     * @param dtoS 配置信息
     * @return boolean
     */
    VisualDTO saveVisualRet(VisualDTO dtoS);

    /**
     * 保存新版本可视化信息
     *
     * @param dto 配置信息
     * @return boolean
     */
    boolean saveVisualNewVersion(VisualDTO dto);

    /**
     * 获取版本信息
     * @param visualId 大屏id
     * @return 版本信息
     */
    List<VisualVersionVO> queryVisualVersion(Long visualId);

    /**
     * 修改可视化信息
     *
     * @param dto 配置信息
     * @return boolean
     */
    boolean updateVisual(VisualDTO dto);


    /**
     * 修改版本信息
     * @return
     */
    boolean updateVersion(VisualVersionDTO visualVersionDTO);

    /**
     * 删除可视化信息
     * @param ids
     * @return
     */
    boolean removeVisual(List<Long> ids);

    /**
     * 删除某一个版本号
     * @param visualId 大屏ID
     * @param version 版本号
     * @return
     */
    boolean removeVersion(Long versionId);

    /**
     * 复制可视化信息
     *
     * @param id 主键
     * @return 复制后主键
     */
    Long copyVisual(Long id);

    /**
     * 通过某一个大屏id获取和这个大屏相同分类的所有的大屏id
     * @param id
     * @return
     */
    List<Long> getSameCateGory(Long id);


    /**
     * 新增大屏-导入psd
     * @param psdFile psd文件
     * @param category 大屏分类
     * @return
     */
    Boolean psdSave(MultipartFile psdFile,String category) throws Exception;

    /**
     * 新增、更新、导入、psd导入、复制 大屏时
     * @param title
     * @param category
     */
    void checkTitle(String title,String category);

    //查询当前目录和子目录下所有可视化大屏
    IPage<Visual> getAllList(VisualRequest request, Query query);

    //根据工程id导出可视化大屏
    String exportVisualByProjectId(String projectId, String downFilePath) throws Exception;

    //根据工程id导出可视化大屏 按菜单导出
    String exportVisualByProjectIdAndCategory(String categoryValue, Project project) throws Exception;

}
