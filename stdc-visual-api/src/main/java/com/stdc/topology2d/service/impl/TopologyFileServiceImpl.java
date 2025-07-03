package com.stdc.topology2d.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.topology2d.entity.po.TopologyFile;

import com.stdc.topology2d.mapper.TopologyFileMapper;
import com.stdc.topology2d.service.ITopologyFileService;
import org.springframework.stereotype.Service;

@Service
public class TopologyFileServiceImpl extends ServiceImpl<TopologyFileMapper, TopologyFile> implements ITopologyFileService {
}
