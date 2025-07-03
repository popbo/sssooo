package com.stdc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.project.entity.Project;
import com.stdc.project.entity.vo.ProjectShare;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工程管理表 服务类
 * </p>
 *
 * @author dongbobo
 * @since 2025-06-23
 */
public interface IProjectService extends IService<Project> {

    Map<String, Object> getStatistics();

    //根据工程id导出
    String export(String id) throws Exception;

    //批量导出
    String batchExport(List<String> idList) throws Exception;

    void importProject(MultipartFile file) throws Exception;

    void share(ProjectShare projectShare) throws Exception;

    void batchShare(ProjectShare projectShare) throws Exception;

    void deleteById(String id) throws Exception;

    void copy(String id) throws Exception;
}
