package com.stdc.topology2d.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.project.entity.Project;
import com.stdc.topology2d.entity.dto.TopologyDataDto;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.entity.vo.EngineeringGlobalSynchronizationFrom;
import com.stdc.topology2d.entity.vo.TopologyDataQueryVo;
import com.stdc.topology2d.entity.vo.TopoloyDateJumpVo;
import com.stdc.visual.entity.po.VisualCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

public interface ITopologyDataService extends IService<TopologyData> {

    //查询当前目录和子目录下所有组态大屏
    IPage<TopologyData> getAllList(TopologyDataQueryVo query, QueryWrapper<TopologyData> queryWrapper) throws Exception;

    void share(TopologyDataDto topologyDataDto) throws Exception;

    void batchShare(List<String> idList, String sharedUrlPrefix) throws Exception;

    Map<String, Object> saveByData(Map<String, Object> dataMap);

    Map<String, Object> visualUpdate(Map<String, Object> dataMap);

    void copyfile(TopologyData topologyData) throws Exception;


    //导出json文件和图片
    String fullExport(String id) throws Exception;

    //导出菜单下所有组态大屏 在同一级目录下导出
    String dirExportAll(String categoryValue) throws Exception;

    //导出菜单下所有组态大屏 按菜单结构导出
    String dirExport(String categoryValue) throws Exception;

    void topo2dImport(MultipartFile file, String category, String name) throws Exception;

    void topo2dImportFromProject(ZipFile zipFile, String projectId, HashMap<String, VisualCategory> topoCategoryMaps) throws Exception;

    void engineeringGlobalSynchronization(List<EngineeringGlobalSynchronizationFrom> list) throws Exception;


    TopoloyDateJumpVo jumpToTopo2d(String id);

    void jumpFromVisual(TopoloyDateJumpVo topoloyDateJumpVo);

    Map<Object, String> queryIdByExternalId(String externalId);

    //根据工程id导出组态大屏
    String exportTopologyByProjectId(String projectId, String downFilePath) throws Exception;

    //根据工程id导出组态大屏 按菜单结构导出
    String exportTopologyByProjectIdAndCategory(String categoryValue, Project project) throws Exception;

}
