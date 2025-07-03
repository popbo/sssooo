package com.stdc.topology2d.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.topology2d.entity.po.TopologyDir;
import com.stdc.topology2d.entity.po.TopologyIcon;
import com.stdc.topology2d.mapper.TopologyDirMapper;
import com.stdc.topology2d.mapper.TopologyIconMapper;
import com.stdc.topology2d.service.ITopologyIconService;
import org.springframework.stereotype.Service;

@Service
public class TopologyIconServiceImpl extends ServiceImpl<TopologyIconMapper, TopologyIcon> implements ITopologyIconService {
}
