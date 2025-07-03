package com.stdc.visual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.visual.entity.dto.VisualVersionDTO;
import com.stdc.visual.entity.dto.VisualVersionVO;
import com.stdc.visual.entity.po.VisualConfig;
import com.stdc.visual.mapper.VisualConfigMapper;
import com.stdc.visual.service.IVisualConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 可视化配置表 服务实现类
 *
 * @author wangjie
 */
@Service
public class VisualConfigServiceImpl extends ServiceImpl<VisualConfigMapper, VisualConfig> implements IVisualConfigService {


    @Autowired
    private VisualConfigMapper visualConfigMapper;

    @Override
    public List<VisualVersionVO> queryVisualVersion(Long visualId) {
        List<VisualVersionVO> visualVersionVOS = visualConfigMapper.queryVisualVersion(visualId);
        return visualVersionVOS;
    }

    public boolean updateNewVersion(VisualVersionDTO visualVersionDTO){
        return visualConfigMapper.updateNewVersion(visualVersionDTO);
    }
}
