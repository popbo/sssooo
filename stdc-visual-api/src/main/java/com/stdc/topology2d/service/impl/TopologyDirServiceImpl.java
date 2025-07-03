package com.stdc.topology2d.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.topology2d.entity.dto.TopologyDirDto;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.entity.po.TopologyDir;
import com.stdc.topology2d.mapper.TopologyDataMapper;
import com.stdc.topology2d.mapper.TopologyDirMapper;
import com.stdc.topology2d.service.ITopologyDirService;
import com.stdc.visual.common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopologyDirServiceImpl extends ServiceImpl<TopologyDirMapper, TopologyDir> implements ITopologyDirService {

    @Autowired
    private TopologyDirMapper topologyDirMapper;

}
