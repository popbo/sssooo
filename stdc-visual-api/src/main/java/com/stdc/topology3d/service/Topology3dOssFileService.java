package com.stdc.topology3d.service;

import com.stdc.visual.entity.po.OssFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: wang_jie
 * @data: 2024/12/31
 * @describe: 组态3d平台文件上传服务
 */
public interface Topology3dOssFileService {

    /**
     * 传文件
     * @param dir
     * @param file
     * @return
     */
    OssFile save(String dir, MultipartFile file);

}
