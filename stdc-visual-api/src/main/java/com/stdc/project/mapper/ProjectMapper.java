package com.stdc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.project.entity.Project;

import java.util.Map;

/**
 * <p>
 * 工程管理表 Mapper 接口
 * </p>
 *
 * @author dongbobo
 * @since 2025-06-23
 */
public interface ProjectMapper extends BaseMapper<Project> {

    Map<String, Object> getStatistics();

}
