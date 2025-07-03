package com.stdc.visual.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stdc.visual.entity.dto.VisualVersionDTO;
import com.stdc.visual.entity.dto.VisualVersionVO;
import com.stdc.visual.entity.po.VisualConfig;

import java.util.List;

/**
 * 可视化配置表 服务类
 *
 * @author wangjie
 */
public interface IVisualConfigService extends IService<VisualConfig> {

    /**
     * 获取版本信息
     * @param visualId 大屏id
     * @return 版本信息
     */
    List<VisualVersionVO> queryVisualVersion(Long visualId);


    /**
     * 更新版本信息
     * @param visualVersionDTO
     * @return
     */
    boolean updateNewVersion(VisualVersionDTO visualVersionDTO);
}
