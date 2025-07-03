package com.stdc.topology2d.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.core.tool.entity.result.R;
import com.stdc.topology2d.entity.dto.TopologyFileDto;
import com.stdc.topology2d.entity.po.TopologyFile;
import com.stdc.topology2d.entity.po.TopologyIcon;
import com.stdc.topology2d.entity.vo.TopologyFileListVo;
import com.stdc.visual.entity.po.OssFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: wang_jie
 * @data: 2024/12/31
 * @describe: 组态2d平台文件上传服务
 */
public interface Topology2dOssFileService {

    /**
     * 传文件
     *
     * @param dir
     * @param file
     * @return
     */
    TopologyFileDto save(String dir, MultipartFile file);

    boolean del(String path);

    R<IPage<TopologyFile>> fileList(TopologyFileListVo topologyFileListVo);
}
