package com.stdc.visual.dynamic.service;

import com.stdc.visual.dynamic.base.datasource.po.DatasourceDriver;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/3/15--19:17
 * @describe:
 */
public interface DatasourceDriverService {

    /**
     * 查询所有数据库驱动
     * @return
     */
    List<DatasourceDriver> queryAll();

    /**
     * 通过驱动类型获取数据库驱动列表
     * @return
     */
    List<DatasourceDriver> queryListByType(String type);

    /**
     * 通过数据源驱动id获取数据库驱动详情
     * @return
     */
    DatasourceDriver queryListById(String id);

    /**
     * 保存数据库驱动
     * @return
     */
    Boolean save(DatasourceDriver datasourceDriver);

    /**
     * 编辑数据库驱动
     * @return
     */
    Boolean update(DatasourceDriver datasourceDriver);

    /**
     * 删除数据库驱动
     * @return
     */
    Boolean del(String id);

    /**
     * 上传数据库驱动jar包
     * @param file
     * @return jar包地址
     */
    String uploadDriverJar(MultipartFile file);

    /**
     * 删除数据库驱动jar包
     * @param jarPath jar包地址
     * @return
     */
    Boolean delDriverJar(String jarPath);



}
