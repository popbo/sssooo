package com.stdc.topology2d.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.topology2d.entity.po.TopologyTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ITopologyTemplateService extends IService<TopologyTemplate> {

    List saveByData(Map<String, Object> dataMap);

    List updateByData(Map<String, Object> dataMap);

    void topologyUpdate(Map<String, Object> dataMap);

    String export(String id) throws Exception;

    void templateImport(MultipartFile file, String category, String name) throws Exception;

    String exportByProjectId(String projectId) throws Exception;

    void templateImportByProjectId(MultipartFile file, String projectId) throws Exception;


}
